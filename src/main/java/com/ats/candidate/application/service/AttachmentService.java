package com.ats.candidate.application.service;

import com.ats.candidate.application.parser.CvParser;
import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.exception.InvalidAttachmentException;
import com.ats.candidate.domain.exception.AttachmentParsingException;
import com.ats.candidate.domain.model.*;
import com.ats.candidate.domain.port.in.usecase.AttachmentUseCase;
import com.ats.candidate.domain.port.out.repository.*;
import com.ats.candidate.domain.port.out.storage.AttachmentStoragePort;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Service
public class AttachmentService implements AttachmentUseCase {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PARSE_STATUS_PENDING = "PENDING";

    private final CandidateRepositoryPort candidateRepositoryPort;
    private final AttachmentRepositoryPort attachmentRepositoryPort;
    private final AttachmentStoragePort attachmentStoragePort;
    private final CandidateParseResultRepositoryPort candidateParseResultRepositoryPort;
    private final CandidateCatalogValidationPort candidateCatalogValidationPort;
    private final EducationLevelRepositoryPort educationLevelRepositoryPort;
    private final CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort;
    private final CandidateEducationRepositoryPort educationRepositoryPort;
    private final CandidateHardSkillRepositoryPort hardSkillRepositoryPort;
    private final CandidateSoftSkillRepositoryPort softSkillRepositoryPort;

    public AttachmentService(
            CandidateRepositoryPort candidateRepositoryPort,
            AttachmentRepositoryPort attachmentRepositoryPort,
            AttachmentStoragePort attachmentStoragePort,
            CandidateParseResultRepositoryPort candidateParseResultRepositoryPort,
            CandidateCatalogValidationPort candidateCatalogValidationPort,
            EducationLevelRepositoryPort educationLevelRepositoryPort,
            CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort,
            CandidateEducationRepositoryPort educationRepositoryPort,
            CandidateHardSkillRepositoryPort hardSkillRepositoryPort,
            CandidateSoftSkillRepositoryPort softSkillRepositoryPort
    ) {
        this.candidateRepositoryPort = candidateRepositoryPort;
        this.attachmentRepositoryPort = attachmentRepositoryPort;
        this.attachmentStoragePort = attachmentStoragePort;
        this.candidateParseResultRepositoryPort = candidateParseResultRepositoryPort;
        this.candidateCatalogValidationPort = candidateCatalogValidationPort;
        this.educationLevelRepositoryPort = educationLevelRepositoryPort;
        this.professionalProfileRepositoryPort = professionalProfileRepositoryPort;
        this.educationRepositoryPort = educationRepositoryPort;
        this.hardSkillRepositoryPort = hardSkillRepositoryPort;
        this.softSkillRepositoryPort = softSkillRepositoryPort;
    }

    @Override
    @Transactional
    public Attachment uploadCv(Long candidateId, AttachmentUpload upload) {
        if (!candidateRepositoryPort.existsById(candidateId)) {
            throw new CandidateNotFoundException(candidateId);
        }
        validateUpload(upload);

        StoredAttachment storedAttachment = attachmentStoragePort.store(candidateId, upload);
        Attachment attachment = Attachment.builder()
                .candidateId(candidateId)
                .fileName(storedAttachment.getFileName())
                .fileUrl(storedAttachment.getFileUrl())
                .fileType(PDF_CONTENT_TYPE)
                .fileSize(upload.getSize())
                .checksum(calculateSha256(upload.getContent()))
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(upload.getUploadedBy())
                .parseStatus(PARSE_STATUS_PENDING)
                .build();

        Attachment saved = attachmentRepositoryPort.save(attachment);
        try {
            this.parse(candidateId, saved.getId());
            return attachmentRepositoryPort.findById(saved.getId()).orElse(saved);
        } catch (Exception ex) {
            saved.setParseStatus("FAILED");
            saved.setParseError(ex.getMessage());
            saved.setParsedAt(LocalDateTime.now());
            attachmentRepositoryPort.save(saved);
        }
        return saved;
    }

    @Override
    public List<Attachment> listByCandidateId(Long candidateId) {
        if (!candidateRepositoryPort.existsById(candidateId)) {
            throw new CandidateNotFoundException(candidateId);
        }
        return attachmentRepositoryPort.findByCandidateId(candidateId);
    }

    @Override
    @Transactional
    public Attachment parse(Long candidateId, Long attachmentId) {
        Candidate candidate = candidateRepositoryPort.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException(candidateId));

        Attachment attachment = attachmentRepositoryPort.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + attachmentId));

        if (!attachment.getCandidateId().equals(candidateId)) {
            throw new IllegalArgumentException("Attachment does not belong to candidate");
        }

        byte[] fileBytes;
        try {
            fileBytes = attachmentStoragePort.load(attachment.getFileUrl());
        } catch (Exception ex) {
            attachment.setParseStatus("FAILED");
            attachment.setParseError("Could not read attachment file: " + ex.getMessage());
            attachment.setParsedAt(LocalDateTime.now());
            attachmentRepositoryPort.save(attachment);
            throw new AttachmentParsingException("Could not read attachment file", ex);
        }

        String extractedText;
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        } catch (IOException ex) {
            attachment.setParseStatus("FAILED");
            attachment.setParseError("Error reading PDF text: " + ex.getMessage());
            attachment.setParsedAt(LocalDateTime.now());
            attachmentRepositoryPort.save(attachment);
            throw new AttachmentParsingException("Error reading PDF text", ex);
        }

        if (extractedText == null || extractedText.strip().isEmpty()) {
            attachment.setParseStatus("FAILED");
            attachment.setParseError("El PDF no contiene texto legible");
            attachment.setParsedAt(LocalDateTime.now());
            attachmentRepositoryPort.save(attachment);
            throw new AttachmentParsingException("El PDF no contiene texto legible");
        }

        List<String> hardSkillNames = candidateCatalogValidationPort.getActiveHardSkillNames();
        List<String> softSkillNames = candidateCatalogValidationPort.getActiveSoftSkillNames();

        CvParser.ParsedCv parsedCv = CvParser.parse(extractedText, hardSkillNames, softSkillNames);

        CandidateParseResult parseResult = CandidateParseResult.builder()
                .candidateId(candidateId)
                .attachmentId(attachmentId)
                .rawText(extractedText)
                .parsedJson(extractedText)
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();
        candidateParseResultRepositoryPort.save(parseResult);

        preFillCandidate(candidate, parsedCv);

        attachment.setParseStatus("COMPLETED");
        attachment.setParseError(null);
        attachment.setParsedAt(LocalDateTime.now());
        return attachmentRepositoryPort.save(attachment);
    }

    private void preFillCandidate(Candidate candidate, CvParser.ParsedCv parsed) {
        if (candidate.getFirstName() == null || candidate.getFirstName().isBlank()) {
            candidate.setFirstName(parsed.firstName);
        }
        if (candidate.getLastName() == null || candidate.getLastName().isBlank()) {
            candidate.setLastName(parsed.lastName);
        }
        if (candidate.getEmail() == null || candidate.getEmail().isBlank()) {
            candidate.setEmail(parsed.email);
        }
        if (candidate.getPhone() == null || candidate.getPhone().isBlank()) {
            candidate.setPhone(parsed.phone);
        }

        CandidateProfessionalProfile profile = candidate.getProfessionalProfile();
        if (profile == null) {
            profile = CandidateProfessionalProfile.builder()
                    .candidateId(candidate.getId())
                    .headline(parsed.headline)
                    .summary(parsed.summary)
                    .latestPosition(parsed.latestPosition)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            candidate.setProfessionalProfile(profile);
            professionalProfileRepositoryPort.save(profile);
        } else {
            boolean updated = false;
            if (profile.getHeadline() == null || profile.getHeadline().isBlank()) {
                profile.setHeadline(parsed.headline);
                updated = true;
            }
            if (profile.getSummary() == null || profile.getSummary().isBlank()) {
                profile.setSummary(parsed.summary);
                updated = true;
            }
            if (profile.getLatestPosition() == null || profile.getLatestPosition().isBlank()) {
                profile.setLatestPosition(parsed.latestPosition);
                updated = true;
            }
            if (updated) {
                profile.setUpdatedAt(LocalDateTime.now());
                professionalProfileRepositoryPort.save(profile);
            }
        }

        if (candidate.getEducations() == null || candidate.getEducations().isEmpty()) {
            Set<CandidateEducation> educations = new HashSet<>();
            for (CvParser.ParsedEducation parsedEdu : parsed.educations) {
                Long levelId = findEducationLevelId(parsedEdu.level);
                CandidateEducation edu = CandidateEducation.builder()
                        .candidateId(candidate.getId())
                        .educationLevelId(levelId)
                        .degree(parsedEdu.degree)
                        .institution(parsedEdu.institution)
                        .startDate(parsedEdu.startDate)
                        .endDate(parsedEdu.endDate)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                educations.add(edu);
            }
            if (!educations.isEmpty()) {
                candidate.setEducations(educations);
                educationRepositoryPort.saveAll(educations);
            }
        }

        if (candidate.getHardSkills() == null || candidate.getHardSkills().isEmpty()) {
            Set<CandidateHardSkill> hardSkills = new HashSet<>();
            for (String skillName : parsed.matchedHardSkills) {
                Optional<Long> skillId = candidateCatalogValidationPort.findHardSkillIdByName(skillName);
                if (skillId.isPresent()) {
                    CandidateHardSkill hs = CandidateHardSkill.builder()
                            .candidateId(candidate.getId())
                            .hardSkillId(skillId.get())
                            .source("CV_PARSER")
                            .confidence(java.math.BigDecimal.valueOf(1.0))
                            .createdAt(LocalDateTime.now())
                            .build();
                    hardSkills.add(hs);
                }
            }
            if (!hardSkills.isEmpty()) {
                candidate.setHardSkills(hardSkills);
                hardSkillRepositoryPort.saveAll(hardSkills);
            }
        }

        if (candidate.getSoftSkills() == null || candidate.getSoftSkills().isEmpty()) {
            Set<CandidateSoftSkill> softSkills = new HashSet<>();
            for (String skillName : parsed.matchedSoftSkills) {
                Optional<Long> skillId = candidateCatalogValidationPort.findSoftSkillIdByName(skillName);
                if (skillId.isPresent()) {
                    CandidateSoftSkill ss = CandidateSoftSkill.builder()
                            .candidateId(candidate.getId())
                            .softSkillId(skillId.get())
                            .source("CV_PARSER")
                            .confidence(java.math.BigDecimal.valueOf(1.0))
                            .createdAt(LocalDateTime.now())
                            .build();
                    softSkills.add(ss);
                }
            }
            if (!softSkills.isEmpty()) {
                candidate.setSoftSkills(softSkills);
                softSkillRepositoryPort.saveAll(softSkills);
            }
        }

        candidateRepositoryPort.update(candidate);
    }

    private Long findEducationLevelId(String levelName) {
        if (levelName == null) return 1L;
        return educationLevelRepositoryPort.findActive().stream()
                .filter(el -> el.getName().equalsIgnoreCase(levelName))
                .map(EducationLevel::getId)
                .findFirst()
                .orElse(1L);
    }

    private void validateUpload(AttachmentUpload upload) {
        if (upload == null || upload.getContent() == null || upload.getContent().length == 0) {
            throw new InvalidAttachmentException("Attachment file is required");
        }
        if (upload.getSize() == null || upload.getSize() <= 0) {
            throw new InvalidAttachmentException("Attachment file is empty");
        }
        String fileName = upload.getOriginalFileName();
        if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new InvalidAttachmentException("Only PDF files are allowed");
        }
        String contentType = upload.getContentType();
        if (contentType != null && !contentType.isBlank() && !PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new InvalidAttachmentException("Only application/pdf content type is allowed");
        }
    }

    private String calculateSha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available", ex);
        }
    }
}
