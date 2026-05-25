package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Datos para actualizar un postulante.")
public record UpdateCandidateRequest(

        @Size(max = 40)
        @Schema(description = "Telefono de contacto.", example = "912345678")
        String phone,

        @Size(max = 10)
        @Schema(description = "Codigo de pais ISO.", example = "CL")
        String countryCode,

        @Size(max = 50)
        @Schema(description = "Documento de identidad.", example = "11111111-1")
        String identityDocument,

        @Size(max = 180)
        @Schema(description = "Ubicacion del postulante.", example = "Santiago, Chile")
        String location,

        @Size(max = 300)
        @Schema(description = "URL del perfil de LinkedIn.", example = "https://linkedin.com/in/juanperez")
        String linkedinUrl,

        @Size(max = 300)
        @Schema(description = "URL del perfil de GitHub.", example = "https://github.com/juanperez")
        String githubUrl,

        @Schema(description = "Fecha de nacimiento.", example = "1990-06-15")
        LocalDate birthDate,

        @Valid
        CreateCandidateProfessionalProfileRequest professionalProfile,

        @Valid
        Set<CreateCandidateEducationRequest> educations,

        @Valid
        Set<CreateCandidateLanguageRequest> languages,

        @Valid
        Set<CreateCandidateHardSkillRequest> hardSkills,

        @Valid
        Set<CreateCandidateSoftSkillRequest> softSkills,

        @Valid
        Set<CandidateExperienceRequest> experiences,

        @Valid
        Set<CandidateNoteRequest> notes
) {
}

