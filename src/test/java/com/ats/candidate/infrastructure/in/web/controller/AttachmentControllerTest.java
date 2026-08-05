package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.exception.InvalidRecruiterException;
import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.AttachmentContent;
import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.port.in.usecase.AttachmentUseCase;
import com.ats.candidate.infrastructure.in.web.dto.AttachmentResponse;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttachmentControllerTest {

    private final AttachmentUseCase attachmentUseCase = mock(AttachmentUseCase.class);
    private final CandidateWebMapper candidateWebMapper = mock(CandidateWebMapper.class);
    private final AttachmentController controller = new AttachmentController(attachmentUseCase, candidateWebMapper);

    @Test
    void uploadCvBuildsUploadFromMultipartFileAndJwtClaim() {
        Long candidateId = 10L;
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", content);
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("recruiterId", 7L)
                .subject("ignored")
                .build();
        Attachment attachment = Attachment.builder().id(99L).candidateId(candidateId).build();
        AttachmentResponse response = new AttachmentResponse(
                99L,
                candidateId,
                "cv.pdf",
                "candidates/10/cv.pdf",
                "application/pdf",
                (long) content.length,
                "checksum",
                null,
                7L,
                "PENDING"
        );

        when(attachmentUseCase.uploadCv(org.mockito.Mockito.eq(candidateId), org.mockito.Mockito.any(AttachmentUpload.class)))
                .thenReturn(attachment);
        when(candidateWebMapper.toResponse(attachment)).thenReturn(response);

        var result = controller.uploadCv(candidateId, jwt, file);

        ArgumentCaptor<AttachmentUpload> uploadCaptor = ArgumentCaptor.forClass(AttachmentUpload.class);
        verify(attachmentUseCase).uploadCv(org.mockito.Mockito.eq(candidateId), uploadCaptor.capture());
        AttachmentUpload upload = uploadCaptor.getValue();
        assertThat(upload.getOriginalFileName()).isEqualTo("cv.pdf");
        assertThat(upload.getContentType()).isEqualTo("application/pdf");
        assertThat(upload.getSize()).isEqualTo(content.length);
        assertThat(upload.getContent()).isEqualTo(content);
        assertThat(upload.getUploadedBy()).isEqualTo(7L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void uploadCvRejectsMissingAuthenticatedRecruiter() {
        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "pdf".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> controller.uploadCv(10L, null, file))
                .isInstanceOf(InvalidRecruiterException.class)
                .hasMessageContaining("Authenticated recruiter is required");
    }

    @Test
    void listByCandidateIdReturnsMappedAttachments() {
        Long candidateId = 10L;
        Attachment attachment = Attachment.builder().id(1L).candidateId(candidateId).build();
        AttachmentResponse response = new AttachmentResponse(
                1L,
                candidateId,
                "cv.pdf",
                "candidates/10/cv.pdf",
                "application/pdf",
                3L,
                "checksum",
                null,
                7L,
                "PENDING"
        );
        when(attachmentUseCase.listByCandidateId(candidateId)).thenReturn(List.of(attachment));
        when(candidateWebMapper.toResponse(attachment)).thenReturn(response);

        var result = controller.listByCandidateId(candidateId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getContentReturnsPdfBytesWithInlineDisposition() {
        Long candidateId = 10L;
        Long attachmentId = 99L;
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);
        AttachmentContent attachmentContent = new AttachmentContent(content, "cv.pdf", "application/pdf");
        when(attachmentUseCase.loadContent(candidateId, attachmentId)).thenReturn(attachmentContent);

        var result = controller.getContent(candidateId, attachmentId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(content);
        assertThat(result.getHeaders().getContentType().toString()).contains("application/pdf");
        assertThat(result.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).contains("inline");
    }

    @Test
    void parseCvTriggersUsecaseAndReturnsMappedResponse() {
        Long candidateId = 10L;
        Long attachmentId = 99L;
        Attachment attachment = Attachment.builder().id(attachmentId).candidateId(candidateId).build();
        AttachmentResponse response = new AttachmentResponse(
                attachmentId,
                candidateId,
                "cv.pdf",
                "candidates/10/cv.pdf",
                "application/pdf",
                3L,
                "checksum",
                null,
                7L,
                "COMPLETED"
        );
        when(attachmentUseCase.parse(candidateId, attachmentId)).thenReturn(attachment);
        when(candidateWebMapper.toResponse(attachment)).thenReturn(response);

        var result = controller.parseCv(candidateId, attachmentId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(attachmentUseCase).parse(candidateId, attachmentId);
    }
}
