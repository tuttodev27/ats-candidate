package com.ats.candidate.application.service;

import com.ats.candidate.domain.model.CountryCode;
import com.ats.candidate.domain.model.EducationLevel;
import com.ats.candidate.domain.model.ExperienceRange;
import com.ats.candidate.domain.model.Language;
import com.ats.candidate.domain.model.LanguageLevel;
import com.ats.candidate.domain.port.out.repository.CountryCodeRepositoryPort;
import com.ats.candidate.domain.port.out.repository.EducationLevelRepositoryPort;
import com.ats.candidate.domain.port.out.repository.ExperienceRangeRepositoryPort;
import com.ats.candidate.domain.port.out.repository.LanguageLevelRepositoryPort;
import com.ats.candidate.domain.port.out.repository.LanguageRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogServiceTest {

    @Test
    void countryCodeServiceDelegatesActiveCatalogLookup() {
        CountryCodeRepositoryPort repository = mock(CountryCodeRepositoryPort.class);
        List<CountryCode> values = List.of(CountryCode.builder().id(1L).isoCode("CL").build());
        when(repository.findActive()).thenReturn(values);

        assertThat(new CountryCodeService(repository).listActive()).isSameAs(values);
        verify(repository).findActive();
    }

    @Test
    void educationLevelServiceDelegatesActiveCatalogLookup() {
        EducationLevelRepositoryPort repository = mock(EducationLevelRepositoryPort.class);
        List<EducationLevel> values = List.of(EducationLevel.builder().id(1L).name("Tecnico").build());
        when(repository.findActive()).thenReturn(values);

        assertThat(new EducationLevelService(repository).listActive()).isSameAs(values);
        verify(repository).findActive();
    }

    @Test
    void experienceRangeServiceDelegatesActiveCatalogLookup() {
        ExperienceRangeRepositoryPort repository = mock(ExperienceRangeRepositoryPort.class);
        List<ExperienceRange> values = List.of(ExperienceRange.builder().id(1L).label("3-5 anos").build());
        when(repository.findActive()).thenReturn(values);

        assertThat(new ExperienceRangeService(repository).listActive()).isSameAs(values);
        verify(repository).findActive();
    }

    @Test
    void languageServiceDelegatesActiveCatalogLookup() {
        LanguageRepositoryPort repository = mock(LanguageRepositoryPort.class);
        List<Language> values = List.of(Language.builder().id(1L).name("Ingles").build());
        when(repository.findActive()).thenReturn(values);

        assertThat(new LanguageService(repository).listActive()).isSameAs(values);
        verify(repository).findActive();
    }

    @Test
    void languageLevelServiceDelegatesActiveCatalogLookup() {
        LanguageLevelRepositoryPort repository = mock(LanguageLevelRepositoryPort.class);
        List<LanguageLevel> values = List.of(LanguageLevel.builder().id(1L).code("B2").build());
        when(repository.findActive()).thenReturn(values);

        assertThat(new LanguageLevelService(repository).listActive()).isSameAs(values);
        verify(repository).findActive();
    }
}
