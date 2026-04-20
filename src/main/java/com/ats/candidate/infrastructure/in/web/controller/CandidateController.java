package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.exception.ErrorResponse;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping({"/candidates", "/api/candidates"})
@Tag(name = "Candidates", description = "Operaciones para gestionar postulantes.")
public class CandidateController {

    private final CandidateUseCase candidateUseCase;
    private final CandidateWebMapper candidateWebMapper;

    public CandidateController(CandidateUseCase candidateUseCase, CandidateWebMapper candidateWebMapper) {
        this.candidateUseCase = candidateUseCase;
        this.candidateWebMapper = candidateWebMapper;
    }

    @PostMapping
    @Operation(
            summary = "Crear postulante",
            description = "Crea un postulante activo. El correo electronico debe ser unico.",
            parameters = {
                    @Parameter(
                            name = "X-Recruiter-Id",
                            description = "Identificador temporal del reclutador. Sera reemplazado por JWT mas adelante.",
                            in = ParameterIn.HEADER,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Postulante creado correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CandidateResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request invalido.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-04-20T16:24:04.586976732Z",
                                              "status": 400,
                                              "code": "VALIDATION_ERROR",
                                              "message": "email: must be a well-formed email address",
                                              "path": "/candidates"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Ya existe un postulante con el mismo correo electronico.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-04-20T16:24:04.586976732Z",
                                              "status": 409,
                                              "code": "EMAIL_ALREADY_EXISTS",
                                              "message": "Candidate already exists with email: juan.perez@example.com",
                                              "path": "/candidates"
                                            }
                                            """)
                            )
                    )
            }
    )
    public ResponseEntity<CandidateResponse> create(
            @RequestHeader(value = "X-Recruiter-Id", required = false) Long recruiterId,
            @Valid @RequestBody CreateCandidateRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Candidate created = candidateUseCase.create(candidateWebMapper.toDomain(request), recruiterId);
        URI location = uriComponentsBuilder
                .path("/candidates/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(candidateWebMapper.toResponse(created));
    }


}
