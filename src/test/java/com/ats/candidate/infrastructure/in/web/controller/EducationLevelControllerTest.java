package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.domain.port.in.usecase.EducationLevelUseCase;
import com.ats.candidate.infrastructure.in.web.dto.EducationLevelResponse;
import com.ats.candidate.infrastructure.in.web.mapper.EducationLevelWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EducationLevelControllerTest {

    private final EducationLevelUseCase educationLevelUseCase = mock(EducationLevelUseCase.class);
    private final EducationLevelWebMapper educationLevelWebMapper = mock(EducationLevelWebMapper.class);
    private final EducationLevelController controller = new EducationLevelController(educationLevelUseCase, educationLevelWebMapper);

    @Test
    void listActiveReturnsOkAndMappedEducationLevels() {
        EducationLevel level = EducationLevel.builder()
                .id(1L)
                .name("Tecnico")
                .build();
        EducationLevelResponse response = new EducationLevelResponse(1L, "Tecnico");

        when(educationLevelUseCase.listActive()).thenReturn(List.of(level));
        when(educationLevelWebMapper.toResponse(level)).thenReturn(response);

        var result = controller.listActive();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }
}
