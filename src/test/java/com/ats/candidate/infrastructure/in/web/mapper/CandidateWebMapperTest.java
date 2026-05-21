package com.ats.candidate.infrastructure.in.web.mapper;

import com.ats.candidate.domain.model.Candidate;
import com.ats.candidate.domain.model.CandidateEducation;
import com.ats.candidate.domain.model.CandidateHardSkill;
import com.ats.candidate.domain.model.CandidateLanguage;
import com.ats.candidate.domain.model.CandidateProfessionalProfile;
import com.ats.candidate.domain.model.CandidateSoftSkill;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateEducationRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateHardSkillRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateLanguageRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateProfessionalProfileRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateRequest;
import com.ats.candidate.infrastructure.in.web.dto.CreateCandidateSoftSkillRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateWebMapperTest {

    private final CandidateWebMapper mapper = Mappers.getMapper(CandidateWebMapper.class);

    @Test
    void toDomainTrimsCandidateStringsAndMapsNestedDetails() {
        CreateCandidateRequest request = new CreateCandidateRequest(
                " Juan ",
                " Perez ",
                " juan@example.com ",
                " +56912345678 ",
                " 11111111-1 ",
                " CL ",
                " CAND-1 ",
                null,
                " Santiago ",
                " https://linkedin.com/in/juan ",
                " https://github.com/juan ",
                new CreateCandidateProfessionalProfileRequest(" Dev ", " Summary ", " Engineer ", 4L, 5),
                Set.of(new CreateCandidateEducationRequest(3L, " Degree ", " University ", null, null)),
                Set.of(new CreateCandidateLanguageRequest(2L, 5L, " manual ", BigDecimal.ONE)),
                Set.of(new CreateCandidateHardSkillRequest(1L, " Senior ", 5, " manual ", BigDecimal.ONE)),
                Set.of(new CreateCandidateSoftSkillRequest(2L, " manual ", BigDecimal.ONE))
        );

        Candidate result = mapper.toDomain(request);

        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Perez");
        assertThat(result.getEmail()).isEqualTo("juan@example.com");
        assertThat(result.getPhone()).isEqualTo("+56912345678");
        assertThat(result.getIdentityDocument()).isEqualTo("11111111-1");
        assertThat(result.getCountryCode()).isEqualTo("CL");
        assertThat(result.getCode()).isEqualTo("CAND-1");
        assertThat(result.getLocation()).isEqualTo("Santiago");
        assertThat(result.getLinkedinUrl()).isEqualTo("https://linkedin.com/in/juan");
        assertThat(result.getGithubUrl()).isEqualTo("https://github.com/juan");
        assertThat(result.getId()).isNull();
        assertThat(result.getActive()).isNull();
        assertThat(result.getCreatedAt()).isNull();

        CandidateProfessionalProfile profile = result.getProfessionalProfile();
        assertThat(profile.getHeadline()).isEqualTo("Dev");
        assertThat(profile.getSummary()).isEqualTo("Summary");
        assertThat(profile.getLatestPosition()).isEqualTo("Engineer");
        assertThat(profile.getExperienceRangeId()).isEqualTo(4L);

        CandidateEducation education = result.getEducations().iterator().next();
        assertThat(education.getEducationLevelId()).isEqualTo(3L);
        assertThat(education.getDegree()).isEqualTo("Degree");
        assertThat(education.getInstitution()).isEqualTo("University");

        CandidateLanguage language = result.getLanguages().iterator().next();
        assertThat(language.getLanguageId()).isEqualTo(2L);
        assertThat(language.getLanguageLevelId()).isEqualTo(5L);
        assertThat(language.getSource()).isEqualTo("manual");

        CandidateHardSkill hardSkill = result.getHardSkills().iterator().next();
        assertThat(hardSkill.getHardSkillId()).isEqualTo(1L);
        assertThat(hardSkill.getLevel()).isEqualTo("Senior");
        assertThat(hardSkill.getSource()).isEqualTo("manual");

        CandidateSoftSkill softSkill = result.getSoftSkills().iterator().next();
        assertThat(softSkill.getSoftSkillId()).isEqualTo(2L);
        assertThat(softSkill.getSource()).isEqualTo("manual");
    }
}
