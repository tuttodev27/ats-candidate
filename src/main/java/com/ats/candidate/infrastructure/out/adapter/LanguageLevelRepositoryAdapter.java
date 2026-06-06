package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.domain.port.out.repository.LanguageLevelRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.LanguageLevelPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.LanguageLevelJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LanguageLevelRepositoryAdapter implements LanguageLevelRepositoryPort {

    private final LanguageLevelJpaRepository languageLevelJpaRepository;
    private final LanguageLevelPersistenceMapper languageLevelPersistenceMapper;

    public LanguageLevelRepositoryAdapter(
            LanguageLevelJpaRepository languageLevelJpaRepository,
            LanguageLevelPersistenceMapper languageLevelPersistenceMapper
    ) {
        this.languageLevelJpaRepository = languageLevelJpaRepository;
        this.languageLevelPersistenceMapper = languageLevelPersistenceMapper;
    }

    @Override
    public List<LanguageLevel> findActive() {
        return languageLevelJpaRepository.findByActiveTrueOrderByOrderNumberAsc()
                .stream()
                .map(languageLevelPersistenceMapper::toDomain)
                .toList();
    }
}
