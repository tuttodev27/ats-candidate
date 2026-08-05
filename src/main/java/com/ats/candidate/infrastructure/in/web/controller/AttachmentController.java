package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.exception.AttachmentStorageException;
import com.ats.candidate.domain.exception.InvalidRecruiterException;
import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.port.in.usecase.AttachmentUseCase;
import com.ats.candidate.infrastructure.in.web.dto.AttachmentResponse;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidates/{candidateId}/attachments")
@Tag(name = "Attachments", description = "Archivos adjuntos de postulantes.")
public class AttachmentController {

    private final AttachmentUseCase attachmentUseCase;
    private final CandidateWebMapper candidateWebMapper;

    public AttachmentController(AttachmentUseCase attachmentUseCase, CandidateWebMapper candidateWebMapper) {
        this.attachmentUseCase = attachmentUseCase;
        this.candidateWebMapper = candidateWebMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir CV PDF de un postulante")
    public ResponseEntity<AttachmentResponse> uploadCv(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") MultipartFile file
    ) {
        Long recruiterId = extractRecruiterId(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateWebMapper.toResponse(
                attachmentUseCase.uploadCv(candidateId, toUpload(file, recruiterId))
        ));
    }

    @GetMapping
    @Operation(summary = "Listar archivos adjuntos de un postulante", description = "Devuelve los adjuntos de un postulante. Requiere JWT con permiso RECRUITER_READ.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de adjuntos (vacia si el postulante no tiene archivos)."),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido."),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permiso para consultar adjuntos."),
            @ApiResponse(responseCode = "404", description = "El postulante no existe.")
    })
    public ResponseEntity<List<AttachmentResponse>> listByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(attachmentUseCase.listByCandidateId(candidateId)
                .stream()
                .map(candidateWebMapper::toResponse)
                .toList());
    }

    @GetMapping("/{attachmentId}/content")
    @Operation(summary = "Descargar el contenido de un archivo adjunto")
    public ResponseEntity<byte[]> getContent(
            @PathVariable Long candidateId,
            @PathVariable Long attachmentId
    ) {
        var content = attachmentUseCase.loadContent(candidateId, attachmentId);
        String contentType = content.fileType() != null && !content.fileType().isBlank()
                ? content.fileType()
                : MediaType.APPLICATION_PDF_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(content.fileName()))
                .body(content.content());
    }

    private String contentDispositionInline(String fileName) {
        return ContentDisposition.builder("inline")
                .filename(fileName != null ? fileName : "attachment.pdf")
                .build()
                .toString();
    }

    @PostMapping("/{attachmentId}/parse")
    @Operation(summary = "Parsear CV de un postulante manualmente")
    public ResponseEntity<AttachmentResponse> parseCv(
            @PathVariable Long candidateId,
            @PathVariable Long attachmentId
    ) {
        return ResponseEntity.ok(candidateWebMapper.toResponse(
                attachmentUseCase.parse(candidateId, attachmentId)
        ));
    }

    private AttachmentUpload toUpload(MultipartFile file, Long recruiterId) {
        try {
            return AttachmentUpload.builder()
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .content(file.getBytes())
                    .uploadedBy(recruiterId)
                    .build();
        } catch (IOException ex) {
            throw new AttachmentStorageException("Could not read attachment file", ex);
        }
    }

    private Long extractRecruiterId(Jwt jwt) {
        if (jwt == null) {
            throw new InvalidRecruiterException("Authenticated recruiter is required");
        }
        Object value = firstPresentClaim(jwt, "recruiterId", "recruiter_id", "userId", "user_id");
        if (value == null) {
            value = jwt.getSubject();
        }
        if (value == null || String.valueOf(value).isBlank()) {
            throw new InvalidRecruiterException("Authenticated recruiter id is required");
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new InvalidRecruiterException("Authenticated recruiter id must be numeric");
        }
    }

    private Object firstPresentClaim(Jwt jwt, String... claimNames) {
        for (String claimName : claimNames) {
            Object value = jwt.getClaim(claimName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
