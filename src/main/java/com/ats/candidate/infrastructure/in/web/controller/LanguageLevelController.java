package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.port.in.usecase.LanguageLevelUseCase;
import com.ats.candidate.infrastructure.in.web.dto.LanguageLevelResponse;
import com.ats.candidate.infrastructure.in.web.mapper.LanguageLevelWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/language-levels")
@Tag(name = "Language Levels", description = "Niveles de idioma.")
public class LanguageLevelController {

    private final LanguageLevelUseCase languageLevelUseCase;
    private final LanguageLevelWebMapper languageLevelWebMapper;

    public LanguageLevelController(
            LanguageLevelUseCase languageLevelUseCase,
            LanguageLevelWebMapper languageLevelWebMapper
    ) {
        this.languageLevelUseCase = languageLevelUseCase;
        this.languageLevelWebMapper = languageLevelWebMapper;
    }

    @GetMapping
    @Operation(summary = "Listar niveles de idioma activos")
    public ResponseEntity<List<LanguageLevelResponse>> listActive() {
        return ResponseEntity.ok(languageLevelUseCase.listActive()
                .stream()
                .map(languageLevelWebMapper::toResponse)
                .toList());
    }
}
