package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.ExperienceRange;

import java.util.List;

public interface ExperienceRangeUseCase {
    List<ExperienceRange> listActive();
}
