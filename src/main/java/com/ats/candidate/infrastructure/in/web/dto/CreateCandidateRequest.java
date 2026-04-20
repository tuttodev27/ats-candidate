package com.ats.candidate.infrastructure.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Datos necesarios para crear un postulante.")
public record CreateCandidateRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(description = "Nombres del postulante.", example = "Juan")
        String firstName,

        @NotBlank
        @Size(max = 120)
        @Schema(description = "Apellidos del postulante.", example = "Perez")
        String lastName,

        @NotBlank
        @Email
        @Size(max = 180)
        @Schema(description = "Correo electronico unico del postulante.", example = "juan.perez@example.com")
        String email,

        @Size(max = 40)
        @Schema(description = "Telefono de contacto.", example = "+56912345678")
        String phone,

        @Size(max = 50)
        @Schema(description = "Documento de identidad del postulante.", example = "11111111-1")
        String identityDocument,

        @Size(max = 10)
        @Schema(description = "Codigo de pais ISO o codigo interno.", example = "CL")
        String countryCode,

        @Size(max = 50)
        @Schema(description = "Codigo interno del postulante.", example = "CAND-0001")
        String code,

        @Schema(description = "Fecha de nacimiento.", example = "1995-06-15")
        LocalDate birthDate,

        @Size(max = 180)
        @Schema(description = "Ubicacion del postulante.", example = "Santiago, Chile")
        String location,

        @Size(max = 300)
        @Schema(description = "URL del perfil de LinkedIn.", example = "https://linkedin.com/in/juanperez")
        String linkedinUrl,

        @Size(max = 300)
        @Schema(description = "URL del perfil de GitHub.", example = "https://github.com/juanperez")
        String githubUrl
) {
}
