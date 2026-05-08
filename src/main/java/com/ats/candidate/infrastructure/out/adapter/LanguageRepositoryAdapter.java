package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.Language;
import com.ats.candidate.domain.port.out.repository.LanguageRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.LanguagePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.LanguageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LanguageRepositoryAdapter implements LanguageRepositoryPort {

    private final LanguageJpaRepository languageJpaRepository;
    private final LanguagePersistenceMapper languagePersistenceMapper;

    public LanguageRepositoryAdapter(
            LanguageJpaRepository languageJpaRepository,
            LanguagePersistenceMapper languagePersistenceMapper
    ) {
        this.languageJpaRepository = languageJpaRepository;
        this.languagePersistenceMapper = languagePersistenceMapper;
    }

    @Override
    public List<Language> findActive() {
        return languageJpaRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(languagePersistenceMapper::toDomain)
                .toList();
    }
}
