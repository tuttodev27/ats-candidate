package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.port.in.usecase.LanguageUseCase;
import com.ats.candidate.infrastructure.in.web.dto.LanguageResponse;
import com.ats.candidate.infrastructure.in.web.mapper.LanguageWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@Tag(name = "Languages", description = "Idiomas.")
public class LanguageController {

    private final LanguageUseCase languageUseCase;
    private final LanguageWebMapper languageWebMapper;

    public LanguageController(LanguageUseCase languageUseCase, LanguageWebMapper languageWebMapper) {
        this.languageUseCase = languageUseCase;
        this.languageWebMapper = languageWebMapper;
    }

    @GetMapping
    @Operation(summary = "Listar idiomas activos")
    public ResponseEntity<List<LanguageResponse>> listActive() {
        return ResponseEntity.ok(languageUseCase.listActive()
                .stream()
                .map(languageWebMapper::toResponse)
                .toList());
    }
}
