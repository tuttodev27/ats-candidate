package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.EducationLevel;

import java.util.List;

public interface EducationLevelRepositoryPort {
    List<EducationLevel> findActive();
}
