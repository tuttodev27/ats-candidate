package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.domain.port.in.usecase.CountryCodeUseCase;
import com.ats.candidate.domain.port.out.repository.CountryCodeRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryCodeService implements CountryCodeUseCase {

    private final CountryCodeRepositoryPort countryCodeRepositoryPort;

    public CountryCodeService(CountryCodeRepositoryPort countryCodeRepositoryPort) {
        this.countryCodeRepositoryPort = countryCodeRepositoryPort;
    }

    @Override
    public List<CountryCode> listActive() {
        return countryCodeRepositoryPort.findActive();
    }
}
