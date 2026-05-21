package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.exception.InvalidRecruiterException;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private final CandidateUseCase candidateUseCase = mock(CandidateUseCase.class);
    private final CandidateWebMapper candidateWebMapper = mock(CandidateWebMapper.class);
    private final CandidateController controller = new CandidateController(candidateUseCase, candidateWebMapper);

    @Test
    void listAcceptsEstadoAliasAsActiveFilter() {
        PageRequest pageable = PageRequest.of(0, 20);
        Candidate candidate = Candidate.builder().id(1L).active(true).build();
        CandidateResponse response = candidateResponse(1L);
        when(candidateUseCase.list(true, pageable)).thenReturn(new PageImpl<>(List.of(candidate), pageable, 1));
        when(candidateWebMapper.toResponse(candidate)).thenReturn(response);

        var result = controller.list(null, "activo", pageable);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).containsExactly(response);
        verify(candidateUseCase).list(true, pageable);
    }

    @Test
    void listRejectsUnknownEstadoFilter() {
        PageRequest pageable = PageRequest.of(0, 20);

        assertThatThrownBy(() -> controller.list(null, "archivado", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid estado filter");
    }

    @Test
    void createUsesRecruiterIdFromJwtSubjectWhenClaimIsMissing() {
        CreateCandidateRequest request = createCandidateRequest();
        Candidate mapped = Candidate.builder().email("juan@example.com").build();
        Candidate created = Candidate.builder().id(1L).email("juan@example.com").createdBy(42L).build();
        CandidateResponse response = candidateResponse(1L);
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("42")
                .build();

        when(candidateWebMapper.toDomain(request)).thenReturn(mapped);
        when(candidateUseCase.create(mapped, 42L)).thenReturn(created);
        when(candidateWebMapper.toResponse(created)).thenReturn(response);

        var result = controller.create(jwt, request, UriComponentsBuilder.fromPath(""));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).hasToString("/api/candidates/1");
        assertThat(result.getBody()).isEqualTo(response);
        verify(candidateUseCase).create(mapped, 42L);
    }

    @Test
    void createRejectsMissingAuthenticatedRecruiter() {
        CreateCandidateRequest request = createCandidateRequest();

        assertThatThrownBy(() -> controller.create(null, request, UriComponentsBuilder.fromPath("")))
                .isInstanceOf(InvalidRecruiterException.class)
                .hasMessageContaining("Authenticated recruiter is required");
    }

    private CreateCandidateRequest createCandidateRequest() {
        return new CreateCandidateRequest(
                "Juan",
                "Perez",
                "juan@example.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private CandidateResponse candidateResponse(Long id) {
        return new CandidateResponse(
                id,
                "Juan",
                "Perez",
                "juan@example.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                42L,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
