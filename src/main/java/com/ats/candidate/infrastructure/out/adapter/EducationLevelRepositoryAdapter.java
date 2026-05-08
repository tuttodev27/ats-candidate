package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.domain.port.out.repository.EducationLevelRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.EducationLevelPersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.EducationLevelJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EducationLevelRepositoryAdapter implements EducationLevelRepositoryPort {

    private final EducationLevelJpaRepository educationLevelJpaRepository;
    private final EducationLevelPersistenceMapper educationLevelPersistenceMapper;

    public EducationLevelRepositoryAdapter(
            EducationLevelJpaRepository educationLevelJpaRepository,
            EducationLevelPersistenceMapper educationLevelPersistenceMapper
    ) {
        this.educationLevelJpaRepository = educationLevelJpaRepository;
        this.educationLevelPersistenceMapper = educationLevelPersistenceMapper;
    }

    @Override
    public List<EducationLevel> findActive() {
        return educationLevelJpaRepository.findByActiveTrueOrderByOrderNumberAsc()
                .stream()
                .map(educationLevelPersistenceMapper::toDomain)
                .toList();
    }
}
