package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.Language;
import com.ats.candidate.domain.port.in.usecase.LanguageUseCase;
import com.ats.candidate.domain.port.out.repository.LanguageRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService implements LanguageUseCase {

    private final LanguageRepositoryPort languageRepositoryPort;

    public LanguageService(LanguageRepositoryPort languageRepositoryPort) {
        this.languageRepositoryPort = languageRepositoryPort;
    }

    @Override
    public List<Language> listActive() {
        return languageRepositoryPort.findActive();
    }
}
