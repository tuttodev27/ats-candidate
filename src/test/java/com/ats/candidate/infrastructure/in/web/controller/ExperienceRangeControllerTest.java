package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.domain.port.in.usecase.ExperienceRangeUseCase;
import com.ats.candidate.infrastructure.in.web.dto.ExperienceRangeResponse;
import com.ats.candidate.infrastructure.in.web.mapper.ExperienceRangeWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExperienceRangeControllerTest {

    private final ExperienceRangeUseCase experienceRangeUseCase = mock(ExperienceRangeUseCase.class);
    private final ExperienceRangeWebMapper experienceRangeWebMapper = mock(ExperienceRangeWebMapper.class);
    private final ExperienceRangeController controller = new ExperienceRangeController(experienceRangeUseCase, experienceRangeWebMapper);

    @Test
    void listActiveReturnsOkAndMappedExperienceRanges() {
        ExperienceRange range = ExperienceRange.builder()
                .id(1L)
                .label("3-5 anos")
                .minYears(3)
                .maxYears(5)
                .build();
        ExperienceRangeResponse response = new ExperienceRangeResponse(1L, "3-5 anos", 3, 5);

        when(experienceRangeUseCase.listActive()).thenReturn(List.of(range));
        when(experienceRangeWebMapper.toResponse(range)).thenReturn(response);

        var result = controller.listActive();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }
}
