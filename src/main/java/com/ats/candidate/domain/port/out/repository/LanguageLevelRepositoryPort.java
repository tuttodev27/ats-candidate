package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.LanguageLevel;

import java.util.List;

public interface LanguageLevelRepositoryPort {
    List<LanguageLevel> findActive();
}
