package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.port.in.usecase.EducationLevelUseCase;
import com.ats.candidate.infrastructure.in.web.dto.EducationLevelResponse;
import com.ats.candidate.infrastructure.in.web.mapper.EducationLevelWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/education-levels")
@Tag(name = "Education Levels", description = "Niveles de estudios.")
public class EducationLevelController {

    private final EducationLevelUseCase educationLevelUseCase;
    private final EducationLevelWebMapper educationLevelWebMapper;

    public EducationLevelController(
            EducationLevelUseCase educationLevelUseCase,
            EducationLevelWebMapper educationLevelWebMapper
    ) {
        this.educationLevelUseCase = educationLevelUseCase;
        this.educationLevelWebMapper = educationLevelWebMapper;
    }

    @GetMapping
    @Operation(summary = "Listar niveles de estudios activos")
    public ResponseEntity<List<EducationLevelResponse>> listActive() {
        return ResponseEntity.ok(educationLevelUseCase.listActive()
                .stream()
                .map(educationLevelWebMapper::toResponse)
                .toList());
    }
}
