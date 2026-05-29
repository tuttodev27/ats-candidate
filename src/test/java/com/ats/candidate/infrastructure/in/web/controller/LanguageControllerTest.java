package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.Language;
import com.ats.candidate.domain.port.in.usecase.LanguageUseCase;
import com.ats.candidate.infrastructure.in.web.dto.LanguageResponse;
import com.ats.candidate.infrastructure.in.web.mapper.LanguageWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LanguageControllerTest {

    private final LanguageUseCase languageUseCase = mock(LanguageUseCase.class);
    private final LanguageWebMapper languageWebMapper = mock(LanguageWebMapper.class);
    private final LanguageController controller = new LanguageController(languageUseCase, languageWebMapper);

    @Test
    void listActiveReturnsOkAndMappedLanguages() {
        Language language = Language.builder()
                .id(1L)
                .name("Ingles")
                .isoCode("EN")
                .build();
        LanguageResponse response = new LanguageResponse(1L, "Ingles", "EN");

        when(languageUseCase.listActive()).thenReturn(List.of(language));
        when(languageWebMapper.toResponse(language)).thenReturn(response);

        var result = controller.listActive();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }
}
