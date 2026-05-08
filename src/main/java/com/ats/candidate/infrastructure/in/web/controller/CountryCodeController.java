package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.port.in.usecase.CountryCodeUseCase;
import com.ats.candidate.infrastructure.in.web.dto.CountryCodeResponse;
import com.ats.candidate.infrastructure.in.web.mapper.CountryCodeWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/country-codes")
@Tag(name = "Country Codes", description = "Codigos telefonicos de pais.")
public class CountryCodeController {

    private final CountryCodeUseCase countryCodeUseCase;
    private final CountryCodeWebMapper countryCodeWebMapper;

    public CountryCodeController(CountryCodeUseCase countryCodeUseCase, CountryCodeWebMapper countryCodeWebMapper) {
        this.countryCodeUseCase = countryCodeUseCase;
        this.countryCodeWebMapper = countryCodeWebMapper;
    }

    @GetMapping
    @Operation(summary = "Listar codigos telefonicos de pais activos")
    public ResponseEntity<List<CountryCodeResponse>> listActive() {
        return ResponseEntity.ok(countryCodeUseCase.listActive()
                .stream()
                .map(countryCodeWebMapper::toResponse)
                .toList());
    }
}
