package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.exception.InvalidAttachmentException;
import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.model.StoredAttachment;
import com.ats.candidate.domain.port.out.repository.AttachmentRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.domain.port.out.storage.AttachmentStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private CandidateRepositoryPort candidateRepositoryPort;

    @Mock
    private AttachmentRepositoryPort attachmentRepositoryPort;

    @Mock
    private AttachmentStoragePort attachmentStoragePort;

    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void uploadCvStoresPdfMetadataWithChecksumAndPendingParseStatus() throws Exception {
        Long candidateId = 10L;
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("application/pdf")
                .size((long) content.length)
                .content(content)
                .uploadedBy(7L)
                .build();

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(attachmentStoragePort.store(candidateId, upload)).thenReturn(StoredAttachment.builder()
                .fileName("cv.pdf")
                .fileUrl("candidates/10/cv.pdf")
                .build());
        when(attachmentRepositoryPort.save(any(Attachment.class))).thenAnswer(invocation -> {
            Attachment attachment = invocation.getArgument(0);
            attachment.setId(99L);
            return attachment;
        });

        Attachment result = attachmentService.uploadCv(candidateId, upload);

        ArgumentCaptor<Attachment> attachmentCaptor = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepositoryPort).save(attachmentCaptor.capture());
        Attachment saved = attachmentCaptor.getValue();

        assertThat(result.getId()).isEqualTo(99L);
        assertThat(saved.getCandidateId()).isEqualTo(candidateId);
        assertThat(saved.getFileName()).isEqualTo("cv.pdf");
        assertThat(saved.getFileUrl()).isEqualTo("candidates/10/cv.pdf");
        assertThat(saved.getFileType()).isEqualTo("application/pdf");
        assertThat(saved.getFileSize()).isEqualTo(content.length);
        assertThat(saved.getChecksum()).isEqualTo(sha256(content));
        assertThat(saved.getUploadedBy()).isEqualTo(7L);
        assertThat(saved.getUploadedAt()).isNotNull();
        assertThat(saved.getParseStatus()).isEqualTo("PENDING");
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
    void uploadCvRejectsNonPdfExtension() {
        Long candidateId = 10L;
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.docx")
                .contentType("application/pdf")
                .size(5L)
                .content("dummy".getBytes(StandardCharsets.UTF_8))
                .uploadedBy(7L)
                .build();
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        assertThatThrownBy(() -> attachmentService.uploadCv(candidateId, upload))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Only PDF files are allowed");

        verify(attachmentStoragePort, never()).store(any(), any());
        verify(attachmentRepositoryPort, never()).save(any());
    }

    @Test
    void uploadCvRejectsEmptyFile() {
        Long candidateId = 10L;
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("application/pdf")
                .size(0L)
                .content(new byte[0])
                .uploadedBy(7L)
                .build();
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        assertThatThrownBy(() -> attachmentService.uploadCv(candidateId, upload))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Attachment file is required");

        verify(attachmentStoragePort, never()).store(any(), any());
        verify(attachmentRepositoryPort, never()).save(any());
    }

    @Test
    void uploadCvRejectsInvalidContentType() {
        Long candidateId = 10L;
        AttachmentUpload upload = AttachmentUpload.builder()
                .originalFileName("cv.pdf")
                .contentType("text/plain")
                .size(5L)
                .content("dummy".getBytes(StandardCharsets.UTF_8))
                .uploadedBy(7L)
                .build();
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        assertThatThrownBy(() -> attachmentService.uploadCv(candidateId, upload))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Only application/pdf content type is allowed");

        verify(attachmentStoragePort, never()).store(any(), any());
        verify(attachmentRepositoryPort, never()).save(any());
    }

    @Test
    void listByCandidateIdReturnsStoredAttachmentsWhenCandidateExists() {
        Long candidateId = 10L;
        List<Attachment> attachments = List.of(Attachment.builder().id(1L).candidateId(candidateId).build());
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(attachmentRepositoryPort.findByCandidateId(candidateId)).thenReturn(attachments);

        assertThat(attachmentService.listByCandidateId(candidateId)).isSameAs(attachments);
    }

    @Test
    void listByCandidateIdRejectsUnknownCandidate() {
        Long candidateId = 404L;
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(false);

        assertThatThrownBy(() -> attachmentService.listByCandidateId(candidateId))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(attachmentRepositoryPort, never()).findByCandidateId(any());
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
}
