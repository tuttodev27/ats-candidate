package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.EducationLevel;

import java.util.List;

public interface EducationLevelUseCase {
    List<EducationLevel> listActive();
}
