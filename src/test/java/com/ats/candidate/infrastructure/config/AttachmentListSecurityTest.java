package com.ats.candidate.infrastructure.config;

import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.port.in.usecase.AttachmentUseCase;
import com.ats.candidate.infrastructure.in.web.controller.AttachmentController;
import com.ats.candidate.infrastructure.in.web.dto.AttachmentResponse;
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

@WebMvcTest(AttachmentController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "security.jwt.secret=test-secret-key-for-unit-tests-1234567890")
class AttachmentListSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentUseCase attachmentUseCase;

    @MockitoBean
    private CandidateWebMapper candidateWebMapper;

    @Test
    void returns401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/candidates/1/attachments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returns403WhenJwtLacksRecruiterAuthority() throws Exception {
        mockMvc.perform(get("/api/candidates/1/attachments").with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void returns200WithRecruiterRoleAndMappedAttachments() throws Exception {
        Attachment attachment = Attachment.builder().id(1L).candidateId(1L).build();
        AttachmentResponse response = new AttachmentResponse(
                1L,
                1L,
                "cv.pdf",
                "candidates/1/cv.pdf",
                "application/pdf",
                3L,
                "checksum",
                null,
                7L,
                "PENDING"
        );
        when(attachmentUseCase.listByCandidateId(1L)).thenReturn(List.of(attachment));
        when(candidateWebMapper.toResponse(attachment)).thenReturn(response);

        mockMvc.perform(get("/api/candidates/1/attachments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_RECRUITER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].candidateId").value(1))
                .andExpect(jsonPath("$[0].fileName").value("cv.pdf"))
                .andExpect(jsonPath("$[0].fileType").value("application/pdf"))
                .andExpect(jsonPath("$[0].parseStatus").value("PENDING"));
    }
}
