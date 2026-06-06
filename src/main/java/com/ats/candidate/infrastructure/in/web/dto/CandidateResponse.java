package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Postulante creado.")
public record CandidateResponse(
        @Schema(description = "Identificador del postulante.", example = "1")
        Long id,

        @Schema(description = "Nombres del postulante.", example = "Juan")
        String firstName,

        @Schema(description = "Apellidos del postulante.", example = "Perez")
        String lastName,

        @Schema(description = "Correo electronico del postulante.", example = "juan.perez@example.com")
        String email,

        @Schema(description = "Telefono de contacto.", example = "+56912345678")
        String phone,

        @Schema(description = "Documento de identidad.", example = "11111111-1")
        String identityDocument,

        @Schema(description = "Codigo de pais.", example = "CL")
        String countryCode,

        @Schema(description = "Codigo interno del postulante.", example = "CAND-0001")
        String code,

        @Schema(description = "Fecha de nacimiento.", example = "1995-06-15")
        LocalDate birthDate,

        @Schema(description = "Ubicacion del postulante.", example = "Santiago, Chile")
        String location,

        @Schema(description = "URL del perfil de LinkedIn.", example = "https://linkedin.com/in/juanperez")
        String linkedinUrl,

        @Schema(description = "URL del perfil de GitHub.", example = "https://github.com/juanperez")
        String githubUrl,

        @Schema(description = "Indica si el postulante esta activo.", example = "true")
        Boolean active,

        @Schema(description = "Identificador del reclutador que creo el postulante.", example = "1")
        Long createdBy,

        @Schema(description = "Fecha y hora de creacion.", example = "2026-04-20T12:23:57.829836973")
        LocalDateTime createdAt,

        @Schema(description = "Estado actual del proceso de seleccion del postulante.", example = "NEW")
        String currentState,

        CandidateProfessionalProfileResponse professionalProfile,
        Set<CandidateEducationResponse> educations,
        Set<CandidateLanguageResponse> languages,
        Set<CandidateHardSkillResponse> hardSkills,
        Set<CandidateSoftSkillResponse> softSkills,
        Set<AttachmentResponse> attachments,
        Set<CandidateExperienceResponse> experiences,
        Set<CandidateNoteResponse> notes
) {
}

