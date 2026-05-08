package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.Language;

import java.util.List;

public interface LanguageRepositoryPort {
    List<Language> findActive();
}
