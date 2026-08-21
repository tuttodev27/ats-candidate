# 001 · Gestión de candidatos — Tareas

_Checklist accionable derivada del `plan.md`. Tareas pequeñas y concretas; marca `[x]` al completarlas._

- [x] Modelo de dominio: `Candidate` + subagregados (`CandidateEducation`, `CandidateExperience`, `CandidateHardSkill`, `CandidateSoftSkill`, `CandidateLanguage`, `CandidateNote`, `CandidateProfessionalProfile`).
- [x] Enums de estado `CandidateStatus` / `CandidateState` con la máquina de transiciones.
- [x] Excepciones de dominio (`CandidateNotFoundException`, `EmailAlreadyExistException`, `InvalidCatalogReferenceException`, `InvalidCandidateStateException`, `InvalidRecruiterException`).
- [x] Puertos de salida: `CandidateRepositoryPort`, `CandidateStateRepositoryPort`, `CandidateCatalogValidationPort` + repositorios por subagregado.
- [x] Caso de uso `CandidateUseCase` → `CandidateService` (`create`, `update`, `list`, `getById`, `updateStatus`, `deactivate`, `getStatuses`, `getStatusHistory`).
- [x] Persistencia: `CandidateEntity` + entidades JPA de detalle, repositorios JPA y adapters (`CandidateRepositoryAdapter`, `CandidateStateRepositoryAdapter`, etc.).
- [x] Mappers de persistencia (`CandidatePersistenceMapper`, `CandidateStatePersistenceMapper`).
- [x] Controller `CandidateController` con todos los endpoints (`/api/candidates`, `/statuses`, `/{id}`, `/{id}/status`, `/{id}/status-history`).
- [x] DTOs con validación `jakarta.validation` (`CreateCandidateRequest`, `UpdateCandidateRequest`, `UpdateCandidateStatusRequest`, respuestas).
- [x] Mapper web `CandidateWebMapper` (MapStruct) con cálculo de `currentState`.
- [x] Mapeo de errores en `GlobalExceptionHandler` (`CANDIDATE_NOT_FOUND` 404, `EMAIL_ALREADY_EXISTS` 409, `INVALID_CATALOG_REFERENCE` 400, `INVALID_CANDIDATE_STATE` 400, `INVALID_RECRUITER` 400, `VALIDATION_ERROR` 400).
- [x] Reglas de autorización en `SecurityConfig` (lectura y escritura por endpoint).
- [x] Tests: `CandidateServiceTest`, `CandidateControllerTest`, `CandidateCreateWebTest`, `CandidateJpaRepositoryTest`, `CandidateWebMapperTest`.
- [x] Correr `./gradlew check` y verificar que todos los tests pasen.
- [x] Validar el caso de uso HU-25 con el payload de `spec.md`: 201 + ficha completa, 409 email duplicado, 400 catálogo inactivo, 400 validación, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-26 con `GET /api/candidates/{id}`: 200 con ficha completa (todas las secciones + adjuntos + estado actual + timestamps), 404 `CANDIDATE_NOT_FOUND`, 401 sin token, 403 sin `RECRUITER_READ`.
- [x] Validar el caso de uso HU-27 con `PUT /api/candidates/{id}`: 200 con ficha actualizada (contacto, perfil, experiencia, skills, observaciones), 404 `CANDIDATE_NOT_FOUND`, 400 validación/`INVALID_CATALOG_REFERENCE`, 409 email duplicado, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-28 con `PATCH /api/candidates/{id}/status`: 200 con ficha actualizada, 400 estado/transición inválida (`INVALID_CANDIDATE_STATE`), 404 `CANDIDATE_NOT_FOUND`, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-30 con `DELETE /api/candidates/{id}`: 204 No Content, postulante ausente de los listados activos, 404 `CANDIDATE_NOT_FOUND`, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-31 con `GET /api/candidates`: 200 con lista paginada, filtro `active=true/false`, búsqueda `search` sin distinción de mayúsculas (nombre/apellido/email), 200 con lista vacía sin resultados, 401 sin token, 403 sin `RECRUITER_READ`.
- [x] Validar el caso de uso HU-34 con `GET /api/candidates/statuses`: 200 con los 6 estados (`NEW`, `IN_REVIEW`, `INTERVIEW`, `SHORTLIST`, `REJECTED`, `HIRED`) y sus `code`/`label` en español, 401 sin token, 403 sin `RECRUITER_READ`.
- [x] Validar el caso de uso HU-35 en `POST /api/candidates` con `professionalProfile`: 201 con ficha incluyendo el perfil, 400 `INVALID_CATALOG_REFERENCE` si `experienceRangeId` no existe/inactivo, perfil opcional (201 sin el objeto), 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-36 en `POST /api/candidates` con `educations`: 201 con uno o más estudios asociados, 400 `INVALID_CATALOG_REFERENCE` si `educationLevelId` no existe/inactivo, 400 `VALIDATION_ERROR` si falta `educationLevelId`, 400 `INVALID_CATALOG_REFERENCE` con fechas invertidas, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-37 en `POST /api/candidates` con `hardSkills`: 201 con una o más habilidades asociadas e incluidas en la respuesta, 400 `INVALID_CATALOG_REFERENCE` si `hardSkillId` no existe/inactivo, 400 `VALIDATION_ERROR` si falta `hardSkillId`, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Validar el caso de uso HU-38 en `POST /api/candidates` con `softSkills`: 201 con una o más habilidades blandas asociadas e incluidas en la respuesta, 400 `INVALID_CATALOG_REFERENCE` si `softSkillId` no existe/inactivo, 400 `VALIDATION_ERROR` si falta `softSkillId`, 401 sin token, 403 sin `RECRUITER_WRITE`.
- [x] Actualizar `docs/contexto/` si aplica (decisiones, flujo, errores conocidos).
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.

## Mantenimiento (checklist recurrente)

- [ ] Revisar que las reglas de `SecurityConfig` para candidatos sigan alineadas con los permisos emitidos por ats-usuarios.
- [ ] Vigilar el N+1 de `loadDetails()` si crecen los datos de los candidatos.
