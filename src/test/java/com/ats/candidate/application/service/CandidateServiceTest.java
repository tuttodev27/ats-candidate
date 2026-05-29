package com.ats.candidate.application.service;

import com.ats.candidate.domain.exception.EmailAlreadyExistException;
import com.ats.candidate.domain.exception.InvalidCatalogReferenceException;
import com.ats.candidate.domain.exception.CandidateNotFoundException;
import com.ats.candidate.domain.model.Attachment;
import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.domain.model.CandidateState;
import com.ats.candidate.domain.port.out.repository.AttachmentRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateCatalogValidationPort;
import com.ats.candidate.domain.port.out.repository.CandidateEducationRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateHardSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateLanguageRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateProfessionalProfileRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateSoftSkillRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateStateRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateExperienceRepositoryPort;
import com.ats.candidate.domain.port.out.repository.CandidateNoteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepositoryPort candidateRepositoryPort;

    @Mock
    private CandidateCatalogValidationPort catalogValidationPort;

    @Mock
    private CandidateProfessionalProfileRepositoryPort professionalProfileRepositoryPort;

    @Mock
    private CandidateEducationRepositoryPort educationRepositoryPort;

    @Mock
    private CandidateLanguageRepositoryPort languageRepositoryPort;

    @Mock
    private CandidateHardSkillRepositoryPort hardSkillRepositoryPort;

    @Mock
    private CandidateSoftSkillRepositoryPort softSkillRepositoryPort;

    @Mock
    private CandidateStateRepositoryPort candidateStateRepositoryPort;

    @Mock
    private AttachmentRepositoryPort attachmentRepositoryPort;

    @Mock
    private CandidateExperienceRepositoryPort candidateExperienceRepositoryPort;

    @Mock
    private CandidateNoteRepositoryPort candidateNoteRepositoryPort;

    @InjectMocks
    private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        lenient().when(catalogValidationPort.existsActiveCountryCode("CL")).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveExperienceRange(4L)).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveEducationLevel(3L)).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveLanguage(2L)).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveLanguageLevel(5L)).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveHardSkill(1L)).thenReturn(true);
        lenient().when(catalogValidationPort.existsActiveSoftSkill(2L)).thenReturn(true);
        lenient().when(candidateStateRepositoryPort.findLatestByCandidateId(any())).thenReturn(Optional.empty());
        lenient().when(candidateExperienceRepositoryPort.findByCandidateId(any())).thenReturn(List.of());
        lenient().when(candidateNoteRepositoryPort.findByCandidateId(any())).thenReturn(List.of());
    }


    @Test
    void createPersistsCandidateDetailsAndInitialState() {
        Candidate candidate = candidateWithDetails();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(candidateRepositoryPort.save(candidate)).thenAnswer(invocation -> {
            Candidate saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });
        when(professionalProfileRepositoryPort.findByCandidateId(100L))
                .thenReturn(Optional.of(candidate.getProfessionalProfile()));
        when(educationRepositoryPort.findByCandidateId(100L)).thenReturn(List.copyOf(candidate.getEducations()));
        when(languageRepositoryPort.findByCandidateId(100L)).thenReturn(List.copyOf(candidate.getLanguages()));
        when(hardSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.copyOf(candidate.getHardSkills()));
        when(softSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.copyOf(candidate.getSoftSkills()));
        when(attachmentRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());

        Candidate result = candidateService.create(candidate, 9L);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getCreatedBy()).isEqualTo(9L);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getProfessionalProfile().getCandidateId()).isEqualTo(100L);
        assertThat(result.getEducations()).allSatisfy(education -> {
            assertThat(education.getCandidateId()).isEqualTo(100L);
            assertThat(education.getCreatedAt()).isNotNull();
            assertThat(education.getUpdatedAt()).isNotNull();
        });
        assertThat(result.getLanguages()).allSatisfy(language -> {
            assertThat(language.getCandidateId()).isEqualTo(100L);
            assertThat(language.getCreatedAt()).isNotNull();
        });
        assertThat(result.getHardSkills()).allSatisfy(hardSkill -> {
            assertThat(hardSkill.getCandidateId()).isEqualTo(100L);
            assertThat(hardSkill.getCreatedAt()).isNotNull();
        });
        assertThat(result.getSoftSkills()).allSatisfy(softSkill -> {
            assertThat(softSkill.getCandidateId()).isEqualTo(100L);
            assertThat(softSkill.getCreatedAt()).isNotNull();
        });

        verify(professionalProfileRepositoryPort).save(candidate.getProfessionalProfile());
        verify(educationRepositoryPort).saveAll(candidate.getEducations());
        verify(languageRepositoryPort).saveAll(candidate.getLanguages());
        verify(hardSkillRepositoryPort).saveAll(candidate.getHardSkills());
        verify(softSkillRepositoryPort).saveAll(candidate.getSoftSkills());

        ArgumentCaptor<CandidateState> stateCaptor = ArgumentCaptor.forClass(CandidateState.class);
        verify(candidateStateRepositoryPort).save(stateCaptor.capture());
        assertThat(stateCaptor.getValue().getCandidateId()).isEqualTo(100L);
        assertThat(stateCaptor.getValue().getState()).isEqualTo("NEW");
        assertThat(stateCaptor.getValue().getCreatedBy()).isEqualTo(9L);
        assertThat(stateCaptor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void createRejectsDuplicatedEmail() {
        Candidate candidate = Candidate.builder().email("juan@example.com").build();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(EmailAlreadyExistException.class);

        verify(candidateRepositoryPort, never()).save(any());
        verify(candidateStateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsInactiveHardSkillReference() {
        Candidate candidate = candidateWithDetails();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(catalogValidationPort.existsActiveHardSkill(1L)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("hardSkillId");

        verify(candidateRepositoryPort, never()).save(any());
        verify(candidateStateRepositoryPort, never()).save(any());
    }

    @Test
    void getByIdLoadsAllCandidateDetails() {
        Candidate candidate = Candidate.builder().id(100L).email("juan@example.com").build();
        CandidateProfessionalProfile profile = CandidateProfessionalProfile.builder().id(1L).candidateId(100L).build();
        CandidateEducation education = CandidateEducation.builder().id(2L).candidateId(100L).build();
        CandidateLanguage language = CandidateLanguage.builder().id(3L).candidateId(100L).build();
        CandidateHardSkill hardSkill = CandidateHardSkill.builder().id(4L).candidateId(100L).build();
        CandidateSoftSkill softSkill = CandidateSoftSkill.builder().id(5L).candidateId(100L).build();
        Attachment attachment = Attachment.builder().id(6L).candidateId(100L).build();
        CandidateState state = CandidateState.builder().id(7L).candidateId(100L).state("NEW").build();

        when(candidateRepositoryPort.findById(100L)).thenReturn(Optional.of(candidate));
        when(professionalProfileRepositoryPort.findByCandidateId(100L)).thenReturn(Optional.of(profile));
        when(educationRepositoryPort.findByCandidateId(100L)).thenReturn(List.of(education));
        when(languageRepositoryPort.findByCandidateId(100L)).thenReturn(List.of(language));
        when(hardSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of(hardSkill));
        when(softSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of(softSkill));
        when(attachmentRepositoryPort.findByCandidateId(100L)).thenReturn(List.of(attachment));
        when(candidateStateRepositoryPort.findLatestByCandidateId(100L)).thenReturn(Optional.of(state));

        Candidate result = candidateService.getById(100L);

        assertThat(result.getProfessionalProfile()).isSameAs(profile);
        assertThat(result.getEducations()).containsExactly(education);
        assertThat(result.getLanguages()).containsExactly(language);
        assertThat(result.getHardSkills()).containsExactly(hardSkill);
        assertThat(result.getSoftSkills()).containsExactly(softSkill);
        assertThat(result.getAttachments()).containsExactly(attachment);
        assertThat(result.getStates()).hasSize(1);
        assertThat(result.getStates().iterator().next().getState()).isEqualTo("NEW");
    }

    @Test
    void getByIdExposesCurrentStateInFicha() {
        Candidate candidate = Candidate.builder().id(200L).email("ana@example.com").build();
        CandidateState state = CandidateState.builder().id(10L).candidateId(200L).state("IN_PROCESS").build();

        when(candidateRepositoryPort.findById(200L)).thenReturn(Optional.of(candidate));
        when(professionalProfileRepositoryPort.findByCandidateId(200L)).thenReturn(Optional.empty());
        when(educationRepositoryPort.findByCandidateId(200L)).thenReturn(List.of());
        when(languageRepositoryPort.findByCandidateId(200L)).thenReturn(List.of());
        when(hardSkillRepositoryPort.findByCandidateId(200L)).thenReturn(List.of());
        when(softSkillRepositoryPort.findByCandidateId(200L)).thenReturn(List.of());
        when(attachmentRepositoryPort.findByCandidateId(200L)).thenReturn(List.of());
        when(candidateStateRepositoryPort.findLatestByCandidateId(200L)).thenReturn(Optional.of(state));

        Candidate result = candidateService.getById(200L);

        assertThat(result.getStates()).isNotEmpty();
        assertThat(result.getStates().iterator().next().getState()).isEqualTo("IN_PROCESS");
    }

    @Test
    void getByIdReturnsNullCurrentStateWhenNoStateExists() {
        Candidate candidate = Candidate.builder().id(300L).email("sin@state.com").build();

        when(candidateRepositoryPort.findById(300L)).thenReturn(Optional.of(candidate));
        when(professionalProfileRepositoryPort.findByCandidateId(300L)).thenReturn(Optional.empty());
        when(educationRepositoryPort.findByCandidateId(300L)).thenReturn(List.of());
        when(languageRepositoryPort.findByCandidateId(300L)).thenReturn(List.of());
        when(hardSkillRepositoryPort.findByCandidateId(300L)).thenReturn(List.of());
        when(softSkillRepositoryPort.findByCandidateId(300L)).thenReturn(List.of());
        when(attachmentRepositoryPort.findByCandidateId(300L)).thenReturn(List.of());
        when(candidateStateRepositoryPort.findLatestByCandidateId(300L)).thenReturn(Optional.empty());

        Candidate result = candidateService.getById(300L);

        assertThat(result.getStates()).isNullOrEmpty();
    }


    @Test
    void getByIdRejectsUnknownCandidate() {
        when(candidateRepositoryPort.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.getById(404L))
                .isInstanceOf(CandidateNotFoundException.class);
    }

    @Test
    void listPassesActiveFilterAndLoadsDetails() {
        PageRequest pageable = PageRequest.of(0, 20);
        Candidate candidate = Candidate.builder().id(100L).active(true).build();
        when(candidateRepositoryPort.findAll(true, null, pageable))
                .thenReturn(new PageImpl<>(List.of(candidate), pageable, 1));
        when(professionalProfileRepositoryPort.findByCandidateId(100L)).thenReturn(Optional.empty());
        when(educationRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(languageRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(hardSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(softSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(attachmentRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());

        Page<Candidate> result = candidateService.list(true, null, pageable);

        assertThat(result.getContent()).containsExactly(candidate);
        verify(candidateRepositoryPort).findAll(true, null, pageable);
        verify(attachmentRepositoryPort).findByCandidateId(100L);
    }

    @Test
    void listPassesActiveAndSearchFiltersToRepositoryPort() {
        PageRequest pageable = PageRequest.of(0, 20);
        Candidate candidate = Candidate.builder().id(100L).active(false).build();
        when(candidateRepositoryPort.findAll(false, "perez", pageable))
                .thenReturn(new PageImpl<>(List.of(candidate), pageable, 1));
        when(professionalProfileRepositoryPort.findByCandidateId(100L)).thenReturn(Optional.empty());
        when(educationRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(languageRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(hardSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(softSkillRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());
        when(attachmentRepositoryPort.findByCandidateId(100L)).thenReturn(List.of());

        Page<Candidate> result = candidateService.list(false, "perez", pageable);

        assertThat(result.getContent()).containsExactly(candidate);
        verify(candidateRepositoryPort).findAll(false, "perez", pageable);
    }

    @Test
    void createRejectsInactiveCountryCodeReference() {
        Candidate candidate = candidateWithDetails();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(catalogValidationPort.existsActiveCountryCode("CL")).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("country code");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsInactiveExperienceRangeReference() {
        Candidate candidate = candidateWithDetails();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(catalogValidationPort.existsActiveExperienceRange(4L)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("experienceRangeId");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsInactiveLanguageLevelReference() {
        Candidate candidate = candidateWithDetails();
        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(catalogValidationPort.existsActiveLanguageLevel(5L)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("languageLevelId");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsMissingEducationLevelReference() {
        Candidate candidate = candidateWithDetails();
        CandidateEducation education = candidate.getEducations().iterator().next();
        education.setEducationLevelId(null);

        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("educationLevelId");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsInactiveEducationLevelReference() {
        Candidate candidate = candidateWithDetails();
        CandidateEducation education = candidate.getEducations().iterator().next();
        education.setEducationLevelId(999L);

        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);
        when(catalogValidationPort.existsActiveEducationLevel(999L)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("educationLevelId");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void createRejectsEducationWithInvalidDates() {
        Candidate candidate = candidateWithDetails();
        CandidateEducation education = candidate.getEducations().iterator().next();
        education.setStartDate(java.time.LocalDate.of(2026, 1, 1));
        education.setEndDate(java.time.LocalDate.of(2025, 1, 1));

        when(candidateRepositoryPort.existsByEmail(candidate.getEmail())).thenReturn(false);

        assertThatThrownBy(() -> candidateService.create(candidate, 9L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("start date must be before or equal to end date");

        verify(candidateRepositoryPort, never()).save(any());
    }

    @Test
    void updateModifiesDetailsAndReplacesCollections() {
        Long candidateId = 1L;
        Candidate existingCandidate = candidateWithDetails();
        existingCandidate.setId(candidateId);

        Candidate updateData = candidateWithDetails();
        updateData.setPhone("+56999999999");
        updateData.setExperiences(Set.of(com.ats.candidate.domain.model.CandidateExperience.builder()
                .companyName("Test Company")
                .startDate(java.time.LocalDate.of(2020, 1, 1))
                .endDate(java.time.LocalDate.of(2022, 1, 1))
                .build()));
        updateData.setNotes(Set.of(com.ats.candidate.domain.model.CandidateNote.builder()
                .note("Test observation")
                .build()));

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(candidateRepositoryPort.update(any(Candidate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Candidate result = candidateService.update(candidateId, updateData, 42L);

        assertThat(result).isNotNull();
        assertThat(result.getPhone()).isEqualTo("+56999999999");

        verify(candidateRepositoryPort).update(any(Candidate.class));
        verify(educationRepositoryPort).deleteAllByCandidateId(candidateId);
        verify(languageRepositoryPort).deleteAllByCandidateId(candidateId);
        verify(hardSkillRepositoryPort).deleteAllByCandidateId(candidateId);
        verify(softSkillRepositoryPort).deleteAllByCandidateId(candidateId);
        verify(candidateExperienceRepositoryPort).deleteAllByCandidateId(candidateId);
        verify(candidateNoteRepositoryPort).deleteAllByCandidateId(candidateId);

        verify(educationRepositoryPort).saveAll(any());
        verify(languageRepositoryPort).saveAll(any());
        verify(hardSkillRepositoryPort).saveAll(any());
        verify(softSkillRepositoryPort).saveAll(any());
        verify(candidateExperienceRepositoryPort).saveAll(any());
        verify(candidateNoteRepositoryPort).saveAll(any());
    }

    @Test
    void updateRejectsExperienceWithInvalidDates() {
        Long candidateId = 1L;
        Candidate updateData = candidateWithDetails();
        updateData.setExperiences(Set.of(com.ats.candidate.domain.model.CandidateExperience.builder()
                .companyName("Test Company")
                .startDate(java.time.LocalDate.of(2022, 1, 1))
                .endDate(java.time.LocalDate.of(2020, 1, 1))
                .build()));

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        assertThatThrownBy(() -> candidateService.update(candidateId, updateData, 42L))
                .isInstanceOf(InvalidCatalogReferenceException.class)
                .hasMessageContaining("Experience start date must be before or equal to end date");

        verify(candidateRepositoryPort, never()).update(any());
    }

    @Test
    void updateThrowsExceptionWhenCandidateNotFound() {
        Long candidateId = 1L;
        Candidate updateData = candidateWithDetails();

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.update(candidateId, updateData, 42L))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(candidateRepositoryPort, never()).update(any());
    }

    @Test
    void updateStatusSuccessfullyPersistsNewState() {
        Long candidateId = 100L;
        String newStatus = "IN_REVIEW";
        Long recruiterId = 42L;

        Candidate candidate = Candidate.builder().id(candidateId).email("juan@example.com").build();
        CandidateState state = CandidateState.builder().id(10L).candidateId(candidateId).state(newStatus).build();

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);
        when(candidateRepositoryPort.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(candidateStateRepositoryPort.findLatestByCandidateId(candidateId)).thenReturn(Optional.of(state));

        Candidate result = candidateService.updateStatus(candidateId, newStatus, recruiterId);

        assertThat(result).isNotNull();
        assertThat(result.getStates()).isNotEmpty();
        assertThat(result.getStates().iterator().next().getState()).isEqualTo(newStatus);

        ArgumentCaptor<CandidateState> stateCaptor = ArgumentCaptor.forClass(CandidateState.class);
        verify(candidateStateRepositoryPort).save(stateCaptor.capture());
        assertThat(stateCaptor.getValue().getCandidateId()).isEqualTo(candidateId);
        assertThat(stateCaptor.getValue().getState()).isEqualTo(newStatus);
        assertThat(stateCaptor.getValue().getCreatedBy()).isEqualTo(recruiterId);
        assertThat(stateCaptor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void updateStatusThrowsExceptionWhenCandidateNotFound() {
        Long candidateId = 404L;
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.updateStatus(candidateId, "IN_REVIEW", 42L))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(candidateStateRepositoryPort, never()).save(any());
    }

    @Test
    void updateStatusThrowsExceptionWhenStateIsInvalid() {
        Long candidateId = 100L;
        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        assertThatThrownBy(() -> candidateService.updateStatus(candidateId, "INVALID_STATE_VALUE", 42L))
                .isInstanceOf(com.ats.candidate.domain.exception.InvalidCandidateStateException.class)
                .hasMessageContaining("Invalid candidate status: INVALID_STATE_VALUE");

        verify(candidateStateRepositoryPort, never()).save(any());
    }

    @Test
    void deactivateSuccessfullyMarksCandidateAsInactive() {
        Long candidateId = 100L;
        Long recruiterId = 42L;

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(true);

        candidateService.deactivate(candidateId, recruiterId);

        verify(candidateRepositoryPort).deactivate(candidateId, recruiterId);
    }

    @Test
    void deactivateThrowsExceptionWhenCandidateNotFound() {
        Long candidateId = 404L;
        Long recruiterId = 42L;

        when(candidateRepositoryPort.existsById(candidateId)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.deactivate(candidateId, recruiterId))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(candidateRepositoryPort, never()).deactivate(any(), any());
    }

    @Test
    void getStatusesReturnsAllAvailableStates() {
        var statuses = candidateService.getStatuses();
        assertThat(statuses).containsExactly(
                com.ats.candidate.domain.model.CandidateStatus.NEW,
                com.ats.candidate.domain.model.CandidateStatus.IN_REVIEW,
                com.ats.candidate.domain.model.CandidateStatus.INTERVIEW,
                com.ats.candidate.domain.model.CandidateStatus.SHORTLIST,
                com.ats.candidate.domain.model.CandidateStatus.REJECTED,
                com.ats.candidate.domain.model.CandidateStatus.HIRED
        );
    }

    private Candidate candidateWithDetails() {
        return Candidate.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@example.com")
                .countryCode("CL")
                .professionalProfile(CandidateProfessionalProfile.builder()
                        .headline("Backend Developer")
                        .experienceRangeId(4L)
                        .yearsExperience(5)
                        .build())
                .educations(Set.of(CandidateEducation.builder()
                        .educationLevelId(3L)
                        .degree("Ingenieria")
                        .build()))
                .languages(Set.of(CandidateLanguage.builder()
                        .languageId(2L)
                        .languageLevelId(5L)
                        .build()))
                .hardSkills(Set.of(CandidateHardSkill.builder()
                        .hardSkillId(1L)
                        .level("Senior")
                        .yearsExperience(5)
                        .build()))
                .softSkills(Set.of(CandidateSoftSkill.builder()
                        .softSkillId(2L)
                        .build()))
                .build();
    }
}
