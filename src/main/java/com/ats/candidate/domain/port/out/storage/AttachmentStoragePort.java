package com.ats.candidate.domain.port.out.storage;

import com.ats.candidate.domain.model.AttachmentUpload;
import com.ats.candidate.domain.model.StoredAttachment;

public interface AttachmentStoragePort {
    StoredAttachment store(Long candidateId, AttachmentUpload upload);
}
