package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.AttachmentUpload;

import java.util.List;

public interface AttachmentUseCase {
    Attachment uploadCv(Long candidateId, AttachmentUpload upload);
    List<Attachment> listByCandidateId(Long candidateId);
}
