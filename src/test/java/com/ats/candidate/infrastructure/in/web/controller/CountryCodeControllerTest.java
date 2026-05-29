package com.ats.candidate.infrastructure.in.web.controller;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.domain.port.in.usecase.CountryCodeUseCase;
import com.ats.candidate.infrastructure.in.web.dto.CountryCodeResponse;
import com.ats.candidate.infrastructure.in.web.mapper.CountryCodeWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CountryCodeControllerTest {

    private final CountryCodeUseCase countryCodeUseCase = mock(CountryCodeUseCase.class);
    private final CountryCodeWebMapper countryCodeWebMapper = mock(CountryCodeWebMapper.class);
    private final CountryCodeController controller = new CountryCodeController(countryCodeUseCase, countryCodeWebMapper);

    @Test
    void listActiveReturnsOkAndMappedCountryCodes() {
        CountryCode countryCode = CountryCode.builder()
                .id(1L)
                .countryName("Chile")
                .isoCode("CL")
                .phoneCode("+56")
                .build();
        CountryCodeResponse response = new CountryCodeResponse(1L, "Chile", "CL", "+56");

        when(countryCodeUseCase.listActive()).thenReturn(List.of(countryCode));
        when(countryCodeWebMapper.toResponse(countryCode)).thenReturn(response);

        var result = controller.listActive();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }
}
