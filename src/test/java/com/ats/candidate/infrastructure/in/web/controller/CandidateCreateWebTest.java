package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.exception.InvalidCatalogReferenceException;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.infrastructure.config.SecurityConfig;
import com.ats.candidate.infrastructure.in.web.dto.CandidateResponse;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "security.jwt.secret=test-secret-key-for-unit-tests-1234567890")
class CandidateCreateWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateUseCase candidateUseCase;

    @MockitoBean
    private CandidateWebMapper candidateWebMapper;

    @Test
    void returns401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithExperienceRangeId(9999)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returns403WhenJwtLacksRecruiterAuthority() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithExperienceRangeId(9999))
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void returns400WhenExperienceRangeIdDoesNotExist() throws Exception {
        Candidate mapped = Candidate.builder().email("juan@example.com").build();
        when(candidateWebMapper.toDomain(any(CreateCandidateRequest.class))).thenReturn(mapped);
        when(candidateUseCase.create(mapped, 42L))
                .thenThrow(new InvalidCatalogReferenceException("No active experience range found for experienceRangeId: 9999"));

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithExperienceRangeId(9999))
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))
                                .jwt(j -> j.subject("42"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CATALOG_REFERENCE"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("experienceRangeId")))
                .andExpect(jsonPath("$.path").value("/api/candidates"));
    }

    @Test
    void returns201WithRecruiterRoleAndLocationHeader() throws Exception {
        Candidate mapped = Candidate.builder().email("juan@example.com").build();
        Candidate created = Candidate.builder().id(1L).email("juan@example.com").createdBy(42L).build();
        CandidateResponse response = candidateResponse(1L);
        when(candidateWebMapper.toDomain(any(CreateCandidateRequest.class))).thenReturn(mapped);
        when(candidateUseCase.create(mapped, 42L)).thenReturn(created);
        when(candidateWebMapper.toResponse(created)).thenReturn(response);

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithExperienceRangeId(4L))
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))
                                .jwt(j -> j.subject("42"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/candidates/1")));
    }

    @Test
    void returns400WhenEducationLevelIdDoesNotExist() throws Exception {
        Candidate mapped = Candidate.builder().email("juan@example.com").build();
        when(candidateWebMapper.toDomain(any(CreateCandidateRequest.class))).thenReturn(mapped);
        when(candidateUseCase.create(mapped, 42L))
                .thenThrow(new InvalidCatalogReferenceException("Invalid or inactive educationLevelId: 9999"));

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithEducationLevel(9999L))
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))
                                .jwt(j -> j.subject("42"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CATALOG_REFERENCE"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("educationLevelId")))
                .andExpect(jsonPath("$.path").value("/api/candidates"));
    }

    @Test
    void returns201WhenCandidateHasEducations() throws Exception {
        Candidate mapped = Candidate.builder().email("juan@example.com").build();
        Candidate created = Candidate.builder().id(2L).email("juan@example.com").createdBy(42L).build();
        CandidateResponse response = candidateResponse(2L);
        when(candidateWebMapper.toDomain(any(CreateCandidateRequest.class))).thenReturn(mapped);
        when(candidateUseCase.create(mapped, 42L)).thenReturn(created);
        when(candidateWebMapper.toResponse(created)).thenReturn(response);

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonWithEducationLevel(1L))
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))
                                .jwt(j -> j.subject("42"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/candidates/2")));
    }

    private String createJsonWithExperienceRangeId(long experienceRangeId) {
        return """
                {
                  "firstName": "Juan",
                  "lastName": "Perez",
                  "email": "juan@example.com",
                  "professionalProfile": {
                    "headline": "Backend Engineer",
                    "summary": "Resumen de carrera",
                    "latestPosition": "Senior Backend Developer",
                    "experienceRangeId": %d,
                    "yearsExperience": 6
                  }
                }
                """.formatted(experienceRangeId);
    }

    private String createJsonWithEducationLevel(long educationLevelId) {
        return """
                {
                  "firstName": "Juan",
                  "lastName": "Perez",
                  "email": "juan@example.com",
                  "educations": [
                    {
                      "educationLevelId": %d,
                      "degree": "Ingeniería Civil Informática",
                      "institution": "Universidad de Chile",
                      "startDate": "2015-03-01",
                      "endDate": "2020-12-31"
                    }
                  ]
                }
                """.formatted(educationLevelId);
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
                null,
                null,
                null,
                null
        );
    }
}
