package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.port.in.usecase.ExperienceRangeUseCase;
import com.ats.candidate.infrastructure.in.web.dto.ExperienceRangeResponse;
import com.ats.candidate.infrastructure.in.web.mapper.ExperienceRangeWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/experience-ranges")
@Tag(name = "Experience Ranges", description = "Rangos de anos de experiencia.")
public class ExperienceRangeController {

    private final ExperienceRangeUseCase experienceRangeUseCase;
    private final ExperienceRangeWebMapper experienceRangeWebMapper;

    public ExperienceRangeController(
            ExperienceRangeUseCase experienceRangeUseCase,
            ExperienceRangeWebMapper experienceRangeWebMapper
    ) {
        this.experienceRangeUseCase = experienceRangeUseCase;
        this.experienceRangeWebMapper = experienceRangeWebMapper;
    }

    @GetMapping
    @Operation(summary = "Listar rangos de anos de experiencia activos")
    public ResponseEntity<List<ExperienceRangeResponse>> listActive() {
        return ResponseEntity.ok(experienceRangeUseCase.listActive()
                .stream()
                .map(experienceRangeWebMapper::toResponse)
                .toList());
    }
}
