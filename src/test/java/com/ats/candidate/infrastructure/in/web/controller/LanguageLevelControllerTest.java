package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.domain.port.in.usecase.LanguageLevelUseCase;
import com.ats.candidate.infrastructure.in.web.dto.LanguageLevelResponse;
import com.ats.candidate.infrastructure.in.web.mapper.LanguageLevelWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LanguageLevelControllerTest {

    private final LanguageLevelUseCase languageLevelUseCase = mock(LanguageLevelUseCase.class);
    private final LanguageLevelWebMapper languageLevelWebMapper = mock(LanguageLevelWebMapper.class);
    private final LanguageLevelController controller = new LanguageLevelController(languageLevelUseCase, languageLevelWebMapper);

    @Test
    void listActiveReturnsOkAndMappedLanguageLevels() {
        LanguageLevel level = LanguageLevel.builder()
                .id(1L)
                .code("B2")
                .name("Intermedio alto")
                .build();
        LanguageLevelResponse response = new LanguageLevelResponse(1L, "B2", "Intermedio alto");

        when(languageLevelUseCase.listActive()).thenReturn(List.of(level));
        when(languageLevelWebMapper.toResponse(level)).thenReturn(response);

        var result = controller.listActive();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }
}
