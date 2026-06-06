package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.infrastructure.out.entity.AttachmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentPersistenceMapper {
    AttachmentEntity toEntity(Attachment attachment);
    Attachment toDomain(AttachmentEntity entity);
}
