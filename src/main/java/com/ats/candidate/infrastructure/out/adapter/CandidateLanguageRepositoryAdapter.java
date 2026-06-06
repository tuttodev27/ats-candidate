package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.domain.port.out.repository.CandidateLanguageRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CandidateLanguagePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CandidateLanguageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CandidateLanguageRepositoryAdapter implements CandidateLanguageRepositoryPort {

    private final CandidateLanguageJpaRepository languageJpaRepository;
    private final CandidateLanguagePersistenceMapper languagePersistenceMapper;

    public CandidateLanguageRepositoryAdapter(
            CandidateLanguageJpaRepository languageJpaRepository,
            CandidateLanguagePersistenceMapper languagePersistenceMapper
    ) {
        this.languageJpaRepository = languageJpaRepository;
        this.languagePersistenceMapper = languagePersistenceMapper;
    }

    @Override
    public Set<CandidateLanguage> saveAll(Set<CandidateLanguage> languages) {
        return new HashSet<>(languageJpaRepository.saveAll(languages.stream()
                .map(languagePersistenceMapper::toEntity)
                .toList())
                .stream()
                .map(languagePersistenceMapper::toDomain)
                .toList());
    }

    @Override
    public List<CandidateLanguage> findByCandidateId(Long candidateId) {
        return languageJpaRepository.findByCandidateId(candidateId)
                .stream()
                .map(languagePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByCandidateId(Long candidateId) {
        languageJpaRepository.deleteAllByCandidateId(candidateId);
    }
}

