package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateNote;
import com.ats.candidate.domain.port.out.repository.CandidateNoteRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateNotePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateNoteJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateNoteRepositoryAdapter implements CandidateNoteRepositoryPort {

    private final CandidateNoteJpaRepository noteJpaRepository;
    private final CandidateNotePersistenceMapper notePersistenceMapper;

    public CandidateNoteRepositoryAdapter(
            CandidateNoteJpaRepository noteJpaRepository,
            CandidateNotePersistenceMapper notePersistenceMapper
    ) {
        this.noteJpaRepository = noteJpaRepository;
        this.notePersistenceMapper = notePersistenceMapper;
    }

    @Override
    public Set<CandidateNote> saveAll(Set<CandidateNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(noteJpaRepository.saveAll(notes.stream()
                .map(notePersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(notePersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateNote> findByCandidateId(Long candidateId) {
        return noteJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(notePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        noteJpaRepository.deleteAllByCandidateId(candidateId);
    }
}
