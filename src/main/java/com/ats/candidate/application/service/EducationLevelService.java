package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.domain.port.in.usecase.EducationLevelUseCase;
import com.ats.candidate.domain.port.out.repository.EducationLevelRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationLevelService implements EducationLevelUseCase {

    private final EducationLevelRepositoryPort educationLevelRepositoryPort;

    public EducationLevelService(EducationLevelRepositoryPort educationLevelRepositoryPort) {
        this.educationLevelRepositoryPort = educationLevelRepositoryPort;
    }

    @Override
    public List<EducationLevel> listActive() {
        return educationLevelRepositoryPort.findActive();
    }
}
