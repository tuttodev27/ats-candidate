package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.exception.InvalidAttachmentException;
import com.ats.candidate.domain.exception.AttachmentParsingException;
import com.ats.candidate.domain.model.*;
import com.ats.candidate.domain.port.out.repository.*;
import com.ats.candidate.domain.port.out.storage.AttachmentStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private CandidateRepositoryPort candidateRepositoryPort;

    @Mock
    private AttachmentRepositoryPort attachmentRepositoryPort;

    @Mock
    private AttachmentStoragePort attachmentStoragePort;

    @Mock
    private CandidateParseResultRepositoryPort candidateParseResultRepositoryPort;

    @Mock
    private CandidateCatalogValidationPort candidateCatalogValidationPort;

    @Mock
    private EducationLevelRepositoryPort educationLevelRepositoryPort;

    @Mock
    private CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort;

    @Mock
    private CandidateEducationRepositoryPort educationRepositoryPort;

    @Mock
    private CandidateHardSkillRepositoryPort hardSkillRepositoryPort;

    @Mock
    private CandidateSoftSkillRepositoryPort softSkillRepositoryPort;

    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void uploadCvWithValidPdfTriggersAutomaticParsingAndSetsCompletedStatus() throws Exception {
        Long candidateId = 10L;
        String cvText = "Nombre: Juan\nApellido: Perez\nEmail: juan.perez@example.com\nTelefono: +56912345678\nHeadline: Software Developer\nSummary: Experienced coder\nCargo: Tech Lead";
        byte[] content = createPdfWithText(cvText);

        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();

        Candidate candidate = Candidate.builder()
                .id(candidateId)
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .build();

        Attachment attachment = Attachment.builder()
                .id(99L)
                .candidateId(candidateId)
                .fileName("cv.pdf")
                .fileUrl("candidates/10/cv.pdf")
                .fileType("application/pdf")
                .fileSize((long) content.length)
                .checksum(sha256(content))
                .uploadedBy(7L)
                .parseStatus("PENDING")
                .build();

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(attachmentStoragePort.store(candidateId, upload)).thenReturn(StoredAttachment.builder()
                .fileName("cv.pdf")
                .fileUrl("candidates/10/cv.pdf")
                .build());
        when(attachmentRepositoryPort.save(any(Attachment.class))).thenReturn(attachment);
        when(candidateRepositoryPort.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(attachmentRepositoryPort.findById(99L)).thenReturn(Optional.of(attachment));
        when(attachmentStoragePort.load("candidates/10/cv.pdf")).thenReturn(content);

        when(candidateCatalogValidationPort.getActiveHardSkillNames()).thenReturn(List.of("Java", "Spring"));
        when(candidateCatalogValidationPort.getActiveSoftSkillNames()).thenReturn(List.of("Leadership"));

        Attachment result = attachmentService.uploadCv(candidateId, upload);

        assertThat(result.getParseStatus()).isEqualTo("COMPLETED");
        verify(candidateParseResultRepositoryPort).save(any());
        verify(candidateRepositoryPort).update(any(Candidate.class));
    }

    @Test
    void uploadCvWithCorruptPdfFallbackToFailedStatus() throws Exception {
        Long candidateId = 10L;
        byte[] content = "corrupted pdf content".getBytes(StandardCharsets.UTF_8);
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();

        Attachment attachment = Attachment.builder()
                .id(99L)
                .candidateId(candidateId)
                .fileName("cv.pdf")
                .fileUrl("candidates/10/cv.pdf")
                .fileType("application/pdf")
                .fileSize((long) content.length)
                .checksum(sha256(content))
                .uploadedBy(7L)
                .parseStatus("PENDING")
                .build();

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(attachmentStoragePort.store(candidateId, upload)).thenReturn(StoredAttachment.builder()
                .fileName("cv.pdf")
                .fileUrl("candidates/10/cv.pdf")
                .build());
        when(attachmentRepositoryPort.save(any(Attachment.class))).thenReturn(attachment);
        when(candidateRepositoryPort.findById(candidateId)).thenReturn(Optional.of(Candidate.builder().id(candidateId).build()));
        when(attachmentRepositoryPort.findById(99L)).thenReturn(Optional.of(attachment));
        when(attachmentStoragePort.load("candidates/10/cv.pdf")).thenReturn(content);

        Attachment result = attachmentService.uploadCv(candidateId, upload);

        assertThat(result.getParseStatus()).isEqualTo("FAILED");
        assertThat(result.getParseError()).contains("Error reading PDF text");
    }

    @Test
    void uploadCvRejectsUnknownCandidate() {
        Long candidateId = 404L;
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(false);

        assertThatThrownBy(() -> attachmentService.uploadCv(candidateId, validUpload()))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(attachmentStoragePort, never()).store(any(), any());
        verify(attachmentRepositoryPort, never()).save(any());
    }

    @Test
    void parseSuccessExtractsDetailsAndPreFillsEmptyFields() throws Exception {
        Long candidateId = 10L;
        Long attachmentId = 99L;
        String cvText = "Nombre: Juan\nApellido: Perez\nEmail: juan@test.com\nTelefono: 123456789\nHeadline: Tech Lead\nSummary: Software architect\nCargo: Senior Dev\nNivel: Universitario\nTitulo: Ingeniero\nInstitucion: Universidad Tecnica";
        byte[] content = createPdfWithText(cvText);

        Candidate candidate = Candidate.builder()
                .id(candidateId)
                .firstName("")
                .lastName("")
                .build();

        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .candidateId(candidateId)
                .fileUrl("candidates/10/cv.pdf")
                .build();

        when(candidateRepositoryPort.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(attachmentRepositoryPort.findById(attachmentId)).thenReturn(Optional.of(attachment));
        when(attachmentStoragePort.load("candidates/10/cv.pdf")).thenReturn(content);

        when(candidateCatalogValidationPort.getActiveHardSkillNames()).thenReturn(List.of("Java", "Spring Boot"));
        when(candidateCatalogValidationPort.getActiveSoftSkillNames()).thenReturn(List.of("Comunicacion"));
        when(educationLevelRepositoryPort.findActive()).thenReturn(List.of(
                EducationLevel.builder().id(4L).name("Universitario").active(true).build()
        ));

        // Stub save method for candidate update
        when(attachmentRepositoryPort.save(any(Attachment.class))).thenAnswer(inv -> inv.getArgument(0));

        Attachment parsed = attachmentService.parse(candidateId, attachmentId);

        assertThat(parsed.getParseStatus()).isEqualTo("COMPLETED");
        assertThat(candidate.getFirstName()).isEqualTo("Juan");
        assertThat(candidate.getLastName()).isEqualTo("Perez");
        assertThat(candidate.getEmail()).isEqualTo("juan@test.com");
        assertThat(candidate.getPhone()).isEqualTo("123456789");
        assertThat(candidate.getProfessionalProfile()).isNotNull();
        assertThat(candidate.getProfessionalProfile().getHeadline()).isEqualTo("Tech Lead");
        assertThat(candidate.getProfessionalProfile().getSummary()).isEqualTo("Software architect");
        assertThat(candidate.getProfessionalProfile().getLatestPosition()).isEqualTo("Senior Dev");

        verify(candidateParseResultRepositoryPort).save(any());
        verify(candidateRepositoryPort).update(candidate);
    }

    @Test
    void parseThrowsExceptionWhenPdfHasNoReadableText() throws Exception {
        Long candidateId = 10L;
        Long attachmentId = 99L;
        // Empty text PDF
        byte[] content = createPdfWithText("");

        Candidate candidate = Candidate.builder().id(candidateId).build();
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .candidateId(candidateId)
                .fileUrl("candidates/10/cv.pdf")
                .build();

        when(candidateRepositoryPort.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(attachmentRepositoryPort.findById(attachmentId)).thenReturn(Optional.of(attachment));
        when(attachmentStoragePort.load("candidates/10/cv.pdf")).thenReturn(content);

        assertThatThrownBy(() -> attachmentService.parse(candidateId, attachmentId))
                .isInstanceOf(AttachmentParsingException.class)
                .hasMessageContaining("El PDF no contiene texto legible");

        verify(attachmentRepositoryPort).save(argThat(a -> "FAILED".equals(a.getParseStatus())));
    }

    private AttachmentUpload validUpload() {
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);
        return AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();
    }

    private String sha256(byte[] content) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }

    private byte[] createPdfWithText(String text) throws IOException {
        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument()) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cb = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                cb.beginText();
                cb.setFont(new org.apache.pdfbox.pdmodel.font.PDType1Font(org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA), 12);
                cb.newLineAtOffset(100, 700);
                for (String line : text.split("\\n")) {
                    cb.showText(line);
                    cb.newLineAtOffset(0, -15);
                }
                cb.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }
}
