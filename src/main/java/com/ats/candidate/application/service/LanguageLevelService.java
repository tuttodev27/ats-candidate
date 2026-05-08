package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.domain.port.in.usecase.LanguageLevelUseCase;
import com.ats.candidate.domain.port.out.repository.LanguageLevelRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageLevelService implements LanguageLevelUseCase {

    private final LanguageLevelRepositoryPort languageLevelRepositoryPort;

    public LanguageLevelService(LanguageLevelRepositoryPort languageLevelRepositoryPort) {
        this.languageLevelRepositoryPort = languageLevelRepositoryPort;
    }

    @Override
    public List<LanguageLevel> listActive() {
        return languageLevelRepositoryPort.findActive();
    }
}
