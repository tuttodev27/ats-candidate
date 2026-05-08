package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.Language;

import java.util.List;

public interface LanguageUseCase {
    List<Language> listActive();
}
