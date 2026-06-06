package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.ExperienceRange;

import java.util.List;

public interface ExperienceRangeRepositoryPort {
    List<ExperienceRange> findActive();
}
