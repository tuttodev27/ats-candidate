package com.ats.candidate.infrastructure.out.adapter;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.domain.port.out.repository.CountryCodeRepositoryPort;
import com.ats.candidate.infrastructure.out.mapper.CountryCodePersistenceMapper;
import com.ats.candidate.infrastructure.out.repository.CountryCodeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CountryCodeRepositoryAdapter implements CountryCodeRepositoryPort {

    private final CountryCodeJpaRepository countryCodeJpaRepository;
    private final CountryCodePersistenceMapper countryCodePersistenceMapper;

    public CountryCodeRepositoryAdapter(
            CountryCodeJpaRepository countryCodeJpaRepository,
            CountryCodePersistenceMapper countryCodePersistenceMapper
    ) {
        this.countryCodeJpaRepository = countryCodeJpaRepository;
        this.countryCodePersistenceMapper = countryCodePersistenceMapper;
    }

    @Override
    public List<CountryCode> findActive() {
        return countryCodeJpaRepository.findByActiveTrueOrderByOrderNumberAscCountryNameAsc()
                .stream()
                .map(countryCodePersistenceMapper::toDomain)
                .toList();
    }
}
