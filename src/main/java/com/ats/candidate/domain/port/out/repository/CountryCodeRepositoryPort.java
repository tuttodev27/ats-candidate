package com.ats.candidate.domain.port.out.repository;

import com.ats.candidate.domain.model.CountryCode;

import java.util.List;

public interface CountryCodeRepositoryPort {
    List<CountryCode> findActive();
}
