# 001 · Gestión de candidatos — Plan

_Cómo se implementa lo descrito en `spec.md`. Debe respetar la `../../constitution/`._

## Enfoque

Feature implementada siguiendo la arquitectura hexagonal del proyecto: el agregado `Candidate` vive en `domain/model` con sus subagregados (educación, idiomas, skills, experiencia, notas); el caso de uso `CandidateService` orquesta las reglas de negocio (validación de catálogos, transiciones de estado, borrado lógico); la web expone los DTOs con validación `jakarta.validation` y mapeo MapStruct; la persistencia usa entidades JPA por tabla con adapters que implementan los puertos de repositorio.

## Implementación

_Pasos técnicos concretos, en orden. Indica los archivos/módulos que se tocan, siguiendo la estructura hexagonal._

1. `domain/model/Candidate.java` — agregado raíz con datos personales, `active`, timestamps y métodos `addXxx`/`removeXxx` para subagregados; enums `CandidateStatus` (`NEW`, `IN_REVIEW`, `INTERVIEW`, `SHORTLIST`, `REJECTED`, `HIRED`) y `CandidateState`.
2. `domain/model/CandidateEducation.java`, `CandidateExperience.java`, `CandidateHardSkill.java`, `CandidateSoftSkill.java`, `CandidateLanguage.java`, `CandidateNote.java`, `CandidateProfessionalProfile.java`, `CandidateAvailability.java`, `CandidateCertification.java` — subagregados de la ficha.
3. `domain/port/in/usecase/CandidateUseCase.java` — puerto de entrada: `create`, `update`, `list`, `getById`, `updateStatus`, `deactivate`, `getStatuses`, `getStatusHistory`.
4. `application/service/CandidateService.java` — implementación `@Service` con `@Transactional`: valida email duplicado (`EmailAlreadyExistException`), valida catálogos vía `CandidateCatalogValidationPort`, aplica transiciones con `ALLOWED_TRANSITIONS`, carga detalles en `loadDetails()` y persiste `CandidateState` en cada cambio.
5. `domain/exception/*.java` — `CandidateNotFoundException`, `EmailAlreadyExistException`, `InvalidCatalogReferenceException`, `InvalidCandidateStateException`, `InvalidRecruiterException`.
6. `infrastructure/out/entity/CandidateEntity.java` + subentidades JPA, `CandidateJpaRepository`, adapters `CandidateRepositoryAdapter`, `CandidateStateRepositoryAdapter` y mappers `CandidatePersistenceMapper`/`CandidateStatePersistenceMapper`.
7. `infrastructure/in/web/controller/CandidateController.java` — endpoints REST; DTOs `CreateCandidateRequest`, `UpdateCandidateRequest`, `UpdateCandidateStatusRequest`, `CandidateResponse`, `CandidateStateResponse`, `CandidateStatusResponse` con validación; mapper `CandidateWebMapper` (MapStruct, calcula `currentState`).
8. `infrastructure/in/web/exception/GlobalExceptionHandler.java` — respuestas `ErrorResponse` normalizadas para las excepciones de dominio.
9. `infrastructure/config/SecurityConfig.java` — reglas por endpoint: GET de candidatos requiere `RECRUITER_READ|CANDIDATE_READ|ROLE_ADMIN|ROLE_RECRUITER`; escrituras requieren `RECRUITER_WRITE|CANDIDATE_CREATE/UPDATE/DELETE|ROLE_ADMIN|ROLE_RECRUITER`.
10. Tests espejo: `CandidateServiceTest`, `CandidateControllerTest`, `CandidateCreateWebTest`, `CandidateJpaRepositoryTest`, `CandidateWebMapperTest`.

### Flujo del caso de uso — registrar postulante (HU-25)

Para el payload de referencia de `spec.md`, el flujo es:

1. `CandidateController.create` recibe `CreateCandidateRequest` y lo valida con `jakarta.validation` (`firstName`, `lastName`, `email` obligatorios).
2. Extrae `recruiterId` de los claims del JWT (claims `recruiterId`/`recruiter_id`/`userId`/`user_id`, fallback al `subject`); si no llega, `InvalidRecruiterException` (400).
3. `CandidateService.create` verifica que el email no exista (`EmailAlreadyExistException` → 409).
4. `CandidateCatalogValidationPort` valida que `countryCode`, `experienceRangeId`, `educationLevelId`, `languageId`, `languageLevelId`, `hardSkillId` y `softSkillId` existan y estén activos (`InvalidCatalogReferenceException` → 400).
5. Se construye el agregado `Candidate` (active=true), se persisten los subagregados y se registra `CandidateState` inicial `NEW`.
6. Se responde 201 con header `Location` y la ficha completa (`CandidateResponse` con `currentState=NEW`).

El payload del caso de uso cubre las HUs anidadas: HU-35 (professionalProfile), HU-36 (educations), HU-37 (hardSkills con level y yearsExperience), HU-38 (softSkills).

### Flujo del caso de uso — consultar ficha completa (HU-26)

Para `GET /api/candidates/{id}`, el flujo es:

1. `CandidateController.getById` recibe el path `{id}`.
2. `CandidateService.getById` consulta el candidato; si no existe lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. `loadDetails()` carga perfil profesional, educaciones, idiomas, skills (hard/soft), experiencias, notas, adjuntos y el último estado.
4. `CandidateWebMapper` arma el `CandidateResponse` con todas las secciones y calcula `currentState`.
5. Se responde 200 con la ficha completa.

Nota: este flujo depende de `loadDetails()`, que hoy dispara múltiples queries (~11) — riesgo N+1 documentado en la sección Riesgos.

### Flujo del caso de uso — actualizar ficha (HU-27)

Para `PUT /api/candidates/{id}`, el flujo es:

1. `CandidateController.update` recibe `UpdateCandidateRequest` y lo valida con `jakarta.validation`.
2. `CandidateService.update` verifica que el candidato exista; si no, lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. Valida las referencias a catálogos activos vía `CandidateCatalogValidationPort` (`InvalidCatalogReferenceException` → 400) y que el email no pertenezca a otro postulante (`EmailAlreadyExistException` → 409).
4. Reemplaza las colecciones de detalle (educaciones, idiomas, skills, experiencias, notas) vía delete + insert.
5. Persiste el candidato actualizado y responde 200 con la ficha completa.

Nota: el paso 4 usa delete + insert en lugar de upsert — deuda conocida documentada en la sección Decisiones/Riesgos.

### Flujo del caso de uso — cambiar estado (HU-28)

Para `PATCH /api/candidates/{id}/status`, el flujo es:

1. `CandidateController.updateStatus` recibe `UpdateCandidateStatusRequest` (status `@NotBlank`) y lo valida con `jakarta.validation`.
2. `CandidateService.updateStatus` verifica que el candidato exista; si no, lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. Valida el estado contra `ALLOWED_STATES` y la transición desde el estado actual contra `ALLOWED_TRANSITIONS`; si no es válida, lanza `InvalidCandidateStateException` → 400.
4. Persiste el nuevo estado del candidato y registra un `CandidateState` en el historial (feed del endpoint `GET /api/candidates/{id}/status-history`).
5. Responde 200 con la ficha completa (`currentState` actualizado).

### Flujo del caso de uso — desactivar postulante (HU-30)

Para `DELETE /api/candidates/{id}`, el flujo es:

1. `CandidateController.deactivate` recibe el path `{id}`.
2. `CandidateService.deactivate` verifica que el candidato exista; si no, lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. Marca `active = false` (borrado lógico), preservando datos, historial y adjuntos.
4. Persiste el cambio y responde **204 No Content** sin cuerpo.

Nota: alineado con la decisión de borrado lógico documentada en la sección Decisiones; el postulante inactivo queda excluido de los listados activos por el filtro `active`.

### Flujo del caso de uso — listar postulantes (HU-31)

Para `GET /api/candidates`, el flujo es:

1. `CandidateController.findAll` recibe los query params opcionales `active`, `search`, `page` y `size`.
2. `CandidateService.findAll` delega en `CandidatePersistenceAdapter` un `PageRequest` con orden por fecha de creación descendente.
3. El filtro `active` (o su alias `estado`) filtra por `active`; el parámetro `search` filtra por nombre, apellido o email en minúsculas (`LOWER(...) LIKE %search%`), sin distinción de mayúsculas.
4. `CandidatePersistenceMapper` mapea cada `CandidateEntity` a un `CandidateListItemResponse` con nombres, apellidos, email, estado actual y fecha de creación.
5. Responde 200 con `Page<CandidateListItemResponse>`; sin resultados devuelve una página vacía.

Nota: el alias `estado` para `active` está documentado en la sección Decisiones.

### Flujo del caso de uso — consultar estados (HU-34)

Para `GET /api/candidates/statuses`, el flujo es:

1. `CandidateController.getStatuses` recorre `CandidateStatus.values()`.
2. `CandidateUseCase.getStatuses` devuelve los estados del enum; el controller mapea cada uno a `CandidateStatusResponse(code, label)` con la etiqueta en español del enum.
3. Responde 200 con la lista en el orden de la enumeración (NEW → IN_REVIEW → INTERVIEW → SHORTLIST → REJECTED → HIRED); sin dependencias externas ni base de datos.

### Flujo del caso de uso — registrar perfil profesional (HU-35)

Dentro de `POST /api/candidates`, el flujo es:

1. `CreateCandidateRequest` recibe el objeto anidado `professionalProfile` (`CreateCandidateProfessionalProfileRequest`) con `headline`, `summary`, `latestPosition`, `yearsExperience` (`@Min(0)`) y `experienceRangeId`, todos opcionales.
2. `CandidateService.create` valida el `experienceRangeId` contra `CandidateCatalogValidationPort.existsActiveExperienceRange`; si no existe o está inactivo, lanza `InvalidCatalogReferenceException` → 400 `INVALID_CATALOG_REFERENCE`.
3. `CandidatePersistenceMapper` persiste el `CandidateProfessionalProfile` asociado al candidato creado.
4. Responde 201 con la ficha completa del candidato (incluye el perfil registrado).

Nota: sin validación cruzada entre `yearsExperience` y el rango seleccionado; la validación de rango solo se dispara si se envía el campo.

### Flujo del caso de uso — registrar estudios (HU-36)

Dentro de `POST /api/candidates`, el flujo es:

1. `CreateCandidateRequest` recibe el conjunto opcional `educations` (`CreateCandidateEducationRequest`) con `educationLevelId` (`@NotNull`), `degree`, `institution`, `startDate` y `endDate`.
2. `CandidateService.create` valida cada `educationLevelId` contra `CandidateCatalogValidationPort.existsActiveEducationLevel`; si no existe o está inactivo, lanza `InvalidCatalogReferenceException` → 400 `INVALID_CATALOG_REFERENCE`.
3. Valida que `startDate` ≤ `endDate` cuando ambas están presentes → 400 `INVALID_CATALOG_REFERENCE`.
4. `CandidatePersistenceMapper` persiste cada `CandidateEducation` asociado al candidato creado.
5. Responde 201 con la ficha completa (incluye los estudios registrados).

Nota: los estudios registrados en el alta se reemplazan en cada `PUT /api/candidates/{id}` (delete + insert, ver Decisiones).

### Flujo del caso de uso — registrar habilidades técnicas (HU-37)

Dentro de `POST /api/candidates`, el flujo es:

1. `CreateCandidateRequest` recibe el conjunto opcional `hardSkills` (`CreateCandidateHardSkillRequest`) con `hardSkillId` (`@NotNull`), `level` (`@Size(max = 80)`), `yearsExperience` (`@Min(0)`), `source` y `confidence`.
2. `CandidateService.create` valida cada `hardSkillId` contra `CandidateCatalogValidationPort.existsActiveHardSkill`; si no existe o está inactivo, lanza `InvalidCatalogReferenceException` → 400 `INVALID_CATALOG_REFERENCE`.
3. `CandidatePersistenceMapper` persiste cada `CandidateHardSkill` asociado al candidato creado.
4. Responde 201 con la ficha completa (incluye las habilidades técnicas registradas).

Nota: `source` y `confidence` existen en el modelo para el parseo de CVs; no los envía el recruiter en el alta normal.

### Flujo del caso de uso — registrar habilidades blandas (HU-38)

Dentro de `POST /api/candidates`, el flujo es:

1. `CreateCandidateRequest` recibe el conjunto opcional `softSkills` (`CreateCandidateSoftSkillRequest`) con `softSkillId` (`@NotNull`), `source` y `confidence`.
2. `CandidateService.create` valida cada `softSkillId` contra `CandidateCatalogValidationPort.existsActiveSoftSkill`; si no existe o está inactivo, lanza `InvalidCatalogReferenceException` → 400 `INVALID_CATALOG_REFERENCE`.
3. `CandidatePersistenceMapper` persiste cada `CandidateSoftSkill` asociado al candidato creado.
4. Responde 201 con la ficha completa (incluye las habilidades blandas registradas).

Nota: `source` y `confidence` son campos de parseo de CVs; el recruiter no los envía en el alta normal. A diferencia de las habilidades técnicas, las blandas no tienen nivel ni años de experiencia.

## Decisiones

- **Estado inicial `NEW` y máquina de transiciones** — el candidato nace en `NEW` y solo puede moverse por transiciones explícitas (`ALLOWED_TRANSITIONS`); `REJECTED` y `HIRED` son terminales. Se descartó permitir transiciones libres entre estados.
- **Borrado lógico con `active`** — se desactiva en lugar de eliminar físicamente, preservando el historial de estados y adjuntos. Alternativa (DELETE físico) descartada por pérdida de auditoría.
- **`update()` con delete + insert de colecciones** — se reemplazan las colecciones de detalle en cada PUT para simplificar la sincronización. Deuda conocida: podría usarse upsert (backlog).
- **`recruiterId` desde JWT** — nunca se recibe en el body; se extrae de claims `recruiterId`/`recruiter_id`/`userId`/`user_id` con fallback al `subject`. Evita spoofing del autor.
- **Máquina de estados en `CandidateService`** — las reglas viven en el caso de uso (no en la entidad) por simplicidad; la excepción `InvalidCandidateStateException` centraliza el mensaje.
- **Alias `estado` en el filtro de listado** — mapea `activo`/`inactivo`/`true`/`false` al campo `active` para compatibilidad con el frontend.

## Riesgos

- **Borrado + re-inserción de colecciones en `update()`** — puede romper relaciones de auditoría o generar IDs nuevos en cada PUT. Mitigación: transaccionalidad completa (`@Transactional`) y persistencia de detalles con mapeo por agregado.
- **N+1 en `loadDetails()`** — la ficha completa dispara múltiples queries (~11) al cargar cada subagregado. Mitigación parcial: `EntityGraph` en repositorios; optimización completa en backlog.
- **Catálogos inactivos** — una referencia a un catálogo desactivado rompe la creación/actualización. Mitigación: validación centralizada en `CandidateCatalogValidationPort`.
- **Dependencia del JWT del gateway** — si el claim `recruiterId` no llega (p. ej. gateway mal configurado), la creación falla con `InvalidRecruiterException`. Mitigación: fallback a `subject` y error 400 claro.
