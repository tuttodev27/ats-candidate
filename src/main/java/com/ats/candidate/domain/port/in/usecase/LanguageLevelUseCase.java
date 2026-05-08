package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.LanguageLevel;

import java.util.List;

public interface LanguageLevelUseCase {
    List<LanguageLevel> listActive();
}
