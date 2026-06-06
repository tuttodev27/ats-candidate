package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.domain.port.in.usecase.ExperienceRangeUseCase;
import com.ats.candidate.domain.port.out.repository.ExperienceRangeRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceRangeService implements ExperienceRangeUseCase {

    private final ExperienceRangeRepositoryPort experienceRangeRepositoryPort;

    public ExperienceRangeService(ExperienceRangeRepositoryPort experienceRangeRepositoryPort) {
        this.experienceRangeRepositoryPort = experienceRangeRepositoryPort;
    }

    @Override
    public List<ExperienceRange> listActive() {
        return experienceRangeRepositoryPort.findActive();
    }
}
