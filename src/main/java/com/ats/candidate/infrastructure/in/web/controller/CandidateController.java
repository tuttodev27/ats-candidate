package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.exception.InvalidRecruiterException;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.UpdateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.UpdateCandidateStatusRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Locale;

@RestController
@RequestMapping("/api/candidates")
@Tag(name = "Candidates", description = "Operaciones para gestionar postulantes.")
public class CandidateController {

        private final CandidateUseCase candidateUseCase;
        private final CandidateWebMapper candidateWebMapper;

        public CandidateController(CandidateUseCase candidateUseCase, CandidateWebMapper candidateWebMapper) {
                this.candidateUseCase = candidateUseCase;
                this.candidateWebMapper = candidateWebMapper;
        }

        @GetMapping
        @Operation(summary = "Listar postulantes", description = "Devuelve una lista paginada de postulantes. Requiere JWT con permiso RECRUITER_READ.", responses = {
                        @ApiResponse(responseCode = "200", description = "Listado paginado de postulantes."),
                        @ApiResponse(responseCode = "401", description = "Token ausente o invalido."),
                        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permiso para consultar postulantes.")
        })
        public ResponseEntity<Page<CandidateResponse>> list(
                        @RequestParam(required = false) Boolean active,
                        @RequestParam(required = false) String estado,
                        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
                Boolean activeFilter = active != null ? active : parseEstado(estado);
                Page<CandidateResponse> candidates = candidateUseCase.list(activeFilter, pageable)
                                .map(candidateWebMapper::toResponse);

                return ResponseEntity.ok(candidates);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Consultar postulante por id", description = "Devuelve la ficha completa de un postulante, incluyendo perfil profesional, estudios, idiomas, habilidades y adjuntos. Requiere JWT con permiso RECRUITER_READ.", responses = {
                        @ApiResponse(responseCode = "200", description = "Postulante encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Token ausente o invalido."),
                        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permiso para consultar postulantes."),
                        @ApiResponse(responseCode = "404", description = "Postulante no encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<CandidateResponse> getById(@PathVariable Long id) {
                Candidate candidate = candidateUseCase.getById(id);
                return ResponseEntity.ok(candidateWebMapper.toResponse(candidate));
        }


        @PostMapping
        @Operation(summary = "Crear postulante", description = "Crea un postulante activo. El correo electronico debe ser unico.", parameters = {
                        @Parameter(name = "X-Recruiter-Id", description = "Identificador temporal del reclutador. Sera reemplazado por JWT mas adelante.", in = ParameterIn.HEADER, example = "1")
        }, responses = {
                        @ApiResponse(responseCode = "201", description = "Postulante creado correctamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Request invalido.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2026-04-20T16:24:04.586976732Z",
                                          "status": 400,
                                          "code": "VALIDATION_ERROR",
                                          "message": "email: must be a well-formed email address",
                                          "path": "/api/candidates"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "409", description = "Ya existe un postulante con el mismo correo electronico.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2026-04-20T16:24:04.586976732Z",
                                          "status": 409,
                                          "code": "EMAIL_ALREADY_EXISTS",
                                          "message": "Candidate already exists with email: juan.perez@example.com",
                                          "path": "/api/candidates"
                                        }
                                        """)))
        })
        public ResponseEntity<CandidateResponse> create(
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody CreateCandidateRequest request,
                        UriComponentsBuilder uriComponentsBuilder) {
                Long recruiterId = extractRecruiterId(jwt);
                Candidate created = candidateUseCase.create(candidateWebMapper.toDomain(request), recruiterId);
                URI location = uriComponentsBuilder
                                .path("/api/candidates/{id}")
                                .buildAndExpand(created.getId())
                                .toUri();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .location(location)
                                .body(candidateWebMapper.toResponse(created));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar postulante", description = "Actualiza los datos de un postulante existente. Requiere JWT con permiso RECRUITER_WRITE.", responses = {
                        @ApiResponse(responseCode = "200", description = "Postulante actualizado correctamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Request invalido.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Token ausente o invalido."),
                        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permiso para actualizar postulantes."),
                        @ApiResponse(responseCode = "404", description = "Postulante no encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<CandidateResponse> update(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody UpdateCandidateRequest request) {
                Long recruiterId = extractRecruiterId(jwt);
                Candidate updated = candidateUseCase.update(id, candidateWebMapper.toDomain(request), recruiterId);
                return ResponseEntity.ok(candidateWebMapper.toResponse(updated));
        }

        @PatchMapping("/{id}/status")
        @Operation(summary = "Change candidate status", description = "Updates the selection process status of a candidate. Requires JWT with RECRUITER_WRITE authority.", responses = {
                        @ApiResponse(responseCode = "200", description = "Candidate status successfully updated.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CandidateResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body or status value.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token."),
                        @ApiResponse(responseCode = "403", description = "User does not have RECRUITER_WRITE permission."),
                        @ApiResponse(responseCode = "404", description = "Candidate not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<CandidateResponse> updateStatus(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody UpdateCandidateStatusRequest request) {
                Long recruiterId = extractRecruiterId(jwt);
                Candidate updated = candidateUseCase.updateStatus(id, request.status(), recruiterId);
                return ResponseEntity.ok(candidateWebMapper.toResponse(updated));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Desactivar postulante", description = "Realiza el borrado logico de un postulante cambiandolo a inactivo. Requiere JWT con permiso RECRUITER_WRITE.", responses = {
                        @ApiResponse(responseCode = "204", description = "Postulante desactivado correctamente."),
                        @ApiResponse(responseCode = "401", description = "Token ausente o invalido."),
                        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permiso para desactivar postulantes."),
                        @ApiResponse(responseCode = "404", description = "Postulante no encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Void> deactivate(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {
                Long recruiterId = extractRecruiterId(jwt);
                candidateUseCase.deactivate(id, recruiterId);
                return ResponseEntity.noContent().build();
        }


        private Long extractRecruiterId(Jwt jwt) {
                if (jwt == null) {
                        throw new InvalidRecruiterException("Authenticated recruiter is required");
                }
                Object value = firstPresentClaim(jwt, "recruiterId", "recruiter_id", "userId", "user_id");
                if (value == null) {
                        value = jwt.getSubject();
                }
                if (value == null || String.valueOf(value).isBlank()) {
                        throw new InvalidRecruiterException("Authenticated recruiter id is required");
                }
                try {
                        return Long.valueOf(String.valueOf(value));
                } catch (NumberFormatException ex) {
                        throw new InvalidRecruiterException("Authenticated recruiter id must be numeric");
                }
        }

        private Object firstPresentClaim(Jwt jwt, String... claimNames) {
                for (String claimName : claimNames) {
                        Object value = jwt.getClaim(claimName);
                        if (value != null) {
                                return value;
                        }
                }
                return null;
        }

        private Boolean parseEstado(String estado) {
                if (estado == null || estado.isBlank()) {
                        return null;
                }

                return switch (estado.trim().toLowerCase(Locale.ROOT)) {
                        case "true", "activo", "active" -> true;
                        case "false", "inactivo", "inactive" -> false;
                        default -> throw new IllegalArgumentException("Invalid estado filter: " + estado);
                };
        }
}
