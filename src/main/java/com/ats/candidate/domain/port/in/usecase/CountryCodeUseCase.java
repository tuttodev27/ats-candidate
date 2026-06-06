package com.ats.candidate.domain.port.in.usecase;

import com.ats.candidate.domain.model.CountryCode;

import java.util.List;

public interface CountryCodeUseCase {
    List<CountryCode> listActive();
}
