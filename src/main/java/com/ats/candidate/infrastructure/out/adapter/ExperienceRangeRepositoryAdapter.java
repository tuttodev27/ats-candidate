package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.domain.port.out.repository.ExperienceRangeRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.ExperienceRangePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.ExperienceRangeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExperienceRangeRepositoryAdapter implements ExperienceRangeRepositoryPort {

    private final ExperienceRangeJpaRepository experienceRangeJpaRepository;
    private final ExperienceRangePersistenceMapper experienceRangePersistenceMapper;

    public ExperienceRangeRepositoryAdapter(
            ExperienceRangeJpaRepository experienceRangeJpaRepository,
            ExperienceRangePersistenceMapper experienceRangePersistenceMapper
    ) {
        this.experienceRangeJpaRepository = experienceRangeJpaRepository;
        this.experienceRangePersistenceMapper = experienceRangePersistenceMapper;
    }

    @Override
    public List<ExperienceRange> findActive() {
        return experienceRangeJpaRepository.findByActiveTrueOrderByOrderNumberAsc()
                .stream()
                .map(experienceRangePersistenceMapper::toDomain)
                .toList();
    }
}
