package com.ats.candidate.infrastructure.out.mapper;

import com.ats.candidate.domain.model.CandidateNote;
import com.ats.candidate.infrastructure.out.entity.CandidateNoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateNotePersistenceMapper {

    @Mapping(target = "candidateId", expression = "java(note.getCandidateId() != null ? Long.valueOf(note.getCandidateId()) : null)")
    @Mapping(target = "createdBy", expression = "java(note.getCreatedBy() != null ? Long.valueOf(note.getCreatedBy()) : null)")
    @Mapping(target = "createdAt", expression = "java(note.getCreatedAt() != null ? java.time.LocalDateTime.parse(note.getCreatedAt()) : null)")
    CandidateNoteEntity toEntity(CandidateNote note);

    @Mapping(target = "candidateId", expression = "java(entity.getCandidateId() != null ? entity.getCandidateId().intValue() : null)")
    @Mapping(target = "createdBy", expression = "java(entity.getCreatedBy() != null ? String.valueOf(entity.getCreatedBy()) : null)")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    CandidateNote toDomain(CandidateNoteEntity entity);
}
