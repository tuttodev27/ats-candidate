# Arquitectura

## Estilo

Hexagonal (puertos y adaptadores), con 3 capas:

```
domain/  →  application/  →  infrastructure/
```

- **domain/** — modelos, interfaces de puertos de entrada/salida, excepciones. Sin anotaciones Spring.
- **application/** — implementaciones de casos de uso (servicios) y lógica de parseo de CV.
- **infrastructure/** — adaptadores REST (controllers, DTOs, mappers web), JPA (entities, repositories, persistence mappers), seguridad, Ollama, almacenamiento local.

## Paquete base

`com.ats.candidate`

## Capa de dominio (`domain/`)

### Modelos (`domain/model/`)
26 clases planas (POJO). Las principales:

| Modelo | Propósito |
|---|---|
| `Candidate` | Postulante raíz |
| `CandidateEducation` | Educación del postulante |
| `CandidateExperience` | Experiencia laboral |
| `CandidateExperienceDescription` | Descripción de experiencia |
| `CandidateExperienceTechnology` | Tecnología asociada a experiencia |
| `CandidateHardSkill` | Hard skill del postulante |
| `CandidateSoftSkill` | Soft skill del postulante |
| `CandidateLanguage` | Idioma del postulante |
| `CandidateCertification` | Certificación del postulante |
| `CandidateAvailability` | Disponibilidad del postulante |
| `CandidateNote` | Nota sobre el postulante |
| `CandidateState/Status` | Estado y status del postulante |
| `CandidateProfessionalProfile` | Perfil profesional |
| `CandidateParseResult` | Resultado del parseo de CV |
| `Attachment` | Adjunto del postulante |
| `AttachmentUpload` / `StoredAttachment` | Upload y almacenamiento de adjuntos |
| `AuditEvent` | Evento de auditoría |
| `CountryCode` | Código de país (catálogo) |
| `EducationLevel` | Nivel educativo (catálogo) |
| `ExperienceRange` | Rango de experiencia (catálogo) |
| `HardSkill` | Hard skill (catálogo) |
| `SoftSkill` | Soft skill (catálogo) |
| `Language` | Idioma (catálogo) |
| `LanguageLevel` | Nivel de idioma (catálogo) |

### Puertos de entrada (`domain/port/in/usecase/`)
7 interfaces: `CandidateUseCase`, `AttachmentUseCase`, `EducationLevelUseCase`, `ExperienceRangeUseCase`, `LanguageUseCase`, `LanguageLevelUseCase`, `CountryCodeUseCase`.

### Puertos de salida (`domain/port/out/`)
- **repository/** — 17 interfaces: `CandidateRepositoryPort`, `AttachmentRepositoryPort`, `CandidateCatalogValidationPort`, `CandidateEducationRepositoryPort`, `CandidateExperienceRepositoryPort`, `CandidateHardSkillRepositoryPort`, `CandidateLanguageRepositoryPort`, `CandidateNoteRepositoryPort`, `CandidateParseResultRepositoryPort`, `CandidateProfessionalProfileRepositoryPort`, `CandidateSoftSkillRepositoryPort`, `CandidateStateRepositoryPort`, `CountryCodeRepositoryPort`, `EducationLevelRepositoryPort`, `ExperienceRangeRepositoryPort`, `LanguageRepositoryPort`, `LanguageLevelRepositoryPort`.
- **storage/** — 1 interfaz: `AttachmentStoragePort`.

### Excepciones (`domain/exception/`)
8 clases: `AttachmentParsingException`, `AttachmentStorageException`, `CandidateNotFoundException`, `EmailAlreadyExistException`, `InvalidAttachmentException`, `InvalidCandidateStateException`, `InvalidCatalogReferenceException`, `InvalidRecruiterException`.

## Capa de aplicación (`application/`)

### Servicios (`application/service/`)
7 clases que implementan los use cases:

| Servicio | Puerto que implementa |
|---|---|
| `CandidateService` | `CandidateUseCase` |
| `AttachmentService` | `AttachmentUseCase` |
| `EducationLevelService` | `EducationLevelUseCase` |
| `ExperienceRangeService` | `ExperienceRangeUseCase` |
| `LanguageService` | `LanguageUseCase` |
| `LanguageLevelService` | `LanguageLevelUseCase` |
| `CountryCodeService` | `CountryCodeUseCase` |

### Parsers (`application/parser/`)
- `CvParser` — parseo por regex (fallback).
- `AiCvParser` — parseo con IA vía Ollama. Devuelve un `CvParser.ParsedCv`.

## Capa de infraestructura (`infrastructure/`)

### Config (`infrastructure/config/`)
- `SecurityConfig` — SecurityFilterChain con OAuth2 resource server + JWT decoder HMAC-SHA256. CORS abierto. Permisos por endpoint.
- `JwtProperties` — `@ConfigurationProperties(prefix = "security.jwt")`.
- `JwtAuthorityExtractor` — extrae `permissions`/`authorities`/`roles` del JWT.
- `OllamaProperties` — `@ConfigurationProperties(prefix = "ollama")`.
- `OpenApiConfig` — Springdoc OpenAPI, título "ATS Candidate API".

### Controladores REST (`infrastructure/in/web/controller/`)
7 controladores: `CandidateController`, `AttachmentController`, `CountryCodeController`, `EducationLevelController`, `ExperienceRangeController`, `LanguageController`, `LanguageLevelController`.

### DTOs web (`infrastructure/in/web/dto/`)
26 clases request/response.

### Mappers web (`infrastructure/in/web/mapper/`)
6 interfaces MapStruct: `CandidateWebMapper`, `CountryCodeWebMapper`, `EducationLevelWebMapper`, `ExperienceRangeWebMapper`, `LanguageWebMapper`, `LanguageLevelWebMapper`.

### Exception handler (`infrastructure/in/web/exception/`)
- `GlobalExceptionHandler` — manejo centralizado de excepciones.
- `ErrorResponse` — DTO de error.

### Entidades JPA (`infrastructure/out/entity/`)
23 entidades que reflejan los modelos de dominio.

### Repositorios JPA (`infrastructure/out/repository/`)
18 interfaces que extienden `JpaRepository`.

### Adaptadores de repositorio (`infrastructure/out/adapter/`)
17 clases que implementan los puertos de salida.

### Mappers de persistencia (`infrastructure/out/mapper/`)
16 interfaces MapStruct para mapeo entidad ↔ dominio.

### Almacenamiento (`infrastructure/out/storage/`)
- `LocalAttachmentStorageAdapter` — implementa `AttachmentStoragePort`. Guarda archivos en `build/uploads/` (configurable vía `app.attachments.storage-path`).

### Ollama (`infrastructure/out/ollama/`)
- `OllamaClient` — cliente HTTP via `RestClient` contra `/api/chat` de Ollama. Soporta `isAvailable()` y `chat()`.

## Seguridad

- Spring Security + OAuth2 resource server configurado con Nimbus JWT decoder.
- JWT con HMAC-SHA256, clave desde `security.jwt.secret`.
- Autorizaciones por endpoint y método HTTP usando autoridades `RECRUITER_*`, `CANDIDATE_*`, `ROLE_ADMIN`, `ROLE_RECRUITER`.
- Endpoints públicos: `/actuator/health`, swagger.
- GET de catálogos requiere autenticación.

## Parseo de CV

Flujo:
1. Se sube un PDF/archivo como adjunto del candidato.
2. PDFBox extrae el texto.
3. `AiCvParser` intenta parsear con Ollama (modelo configurable, prompt que pide JSON).
4. Si Ollama no está disponible o falla, cae en `CvParser` (regex).
5. El resultado se persiste como `CandidateParseResult`.
