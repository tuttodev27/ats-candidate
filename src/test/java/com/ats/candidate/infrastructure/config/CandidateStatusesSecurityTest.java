package com.ats.candidate.infrastructure.config;

import com.ats.candidate.domain.model.CandidateStatus;
import com.ats.candidate.domain.port.in.usecase.CandidateUseCase;
import com.ats.candidate.infrastructure.in.web.controller.CandidateController;
import com.ats.candidate.infrastructure.in.web.mapper.CandidateWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "security.jwt.secret=test-secret-key-for-unit-tests-1234567890")
class CandidateStatusesSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateUseCase candidateUseCase;

    @MockitoBean
    private CandidateWebMapper candidateWebMapper;

    @Test
    void returns401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/candidates/statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returns403WhenJwtLacksRecruiterAuthority() throws Exception {
        mockMvc.perform(get("/api/candidates/statuses").with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void returns200WithRecruiterRole() throws Exception {
        when(candidateUseCase.getStatuses()).thenReturn(List.of(CandidateStatus.values()));

        mockMvc.perform(get("/api/candidates/statuses")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].code").value("NEW"))
                .andExpect(jsonPath("$[0].label").value("Nuevo"));
    }
}
