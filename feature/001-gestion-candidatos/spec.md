# 001 · Gestión de candidatos

**Estado:** implementado ✅

## Qué hace

Gestiona el ciclo de vida completo del postulante a través de la API REST: creación con datos personales y perfiles anidados (educación, idiomas, habilidades hard/soft, experiencia, notas), consulta de ficha completa, actualización, listado paginado con filtros, borrado lógico y cambios de estado del proceso de selección con historial.

## Por qué

Es el núcleo del microservicio de candidatos: sin él no existen perfiles que adjuntar CVs ni candidatos que postular. Resuelve la necesidad del reclutador de registrar, buscar y seguir el estado de cada postulante en el pipeline (nuevo, en revisión, entrevista, finalista, rechazado, contratado).

## Criterios de aceptación

_Condiciones verificables que deben cumplirse para dar la feature por terminada. Redacta cada una de forma que se pueda comprobar con un sí/no. Marca `[x]` al cumplirse._

- [x] `POST /api/candidates` crea un candidato activo con estado inicial `NEW` y responde 201 con header `Location`; el `recruiterId` se extrae del JWT.
- [x] `POST /api/candidates` rechaza con 409 el email duplicado (`EMAIL_ALREADY_EXISTS`).
- [x] `POST /api/candidates` valida las referencias a catálogos activos (countryCode, experienceRange, educationLevel, language, languageLevel, hardSkill, softSkill) y responde 400 con `INVALID_CATALOG_REFERENCE` si alguna es inválida o inactiva.
- [x] `GET /api/candidates` lista paginado (default size 20, orden por createdAt) con filtros `active`, `estado` (alias activo/inactivo) y `search` por texto.
- [x] `GET /api/candidates/{id}` devuelve la ficha completa con estado actual calculado; 404 `CANDIDATE_NOT_FOUND` si no existe.
- [x] `PUT /api/candidates/{id}` actualiza los datos y reemplaza las colecciones de detalle (educaciones, idiomas, skills, experiencias, notas) vía delete + insert.
- [x] `PATCH /api/candidates/{id}/status` valida el estado contra `ALLOWED_STATES` y las transiciones permitidas (`NEW→{IN_REVIEW,INTERVIEW,SHORTLIST,REJECTED,HIRED}`, `IN_REVIEW→{INTERVIEW,SHORTLIST,REJECTED,HIRED}`, `INTERVIEW→{SHORTLIST,REJECTED,HIRED}`, `SHORTLIST→{REJECTED,HIRED}`, `REJECTED` y `HIRED` terminales); 400 `INVALID_CANDIDATE_STATE` en transición inválida.
- [x] `DELETE /api/candidates/{id}` hace borrado lógico (active = false) y responde 204.
- [x] `GET /api/candidates/statuses` devuelve el catálogo de estados del proceso de selección.
- [x] `GET /api/candidates/{id}/status-history` devuelve el historial de cambios de estado con `previousState` encadenado.
- [x] Toda entrada de usuario está validada con anotaciones `jakarta.validation` en los DTOs; errores normalizados en `GlobalExceptionHandler`.
- [x] Requisito de calidad: cubierto por `CandidateServiceTest` (25 tests), `CandidateControllerTest` (16 tests) y `CandidateCreateWebTest` en profile `test`.

### HU-25 · Registrar postulante

Como recruiter autenticado, quiero registrar un nuevo postulante en el ATS para iniciar su ficha y comenzar el seguimiento del proceso de selección.

Payload mínimo (firstName, lastName, email) y completo del `POST /api/candidates`:

```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan.perez@example.com",
  "phone": "912345678",
  "countryCode": "CL",
  "identityDocument": "11111111-1",
  "professionalProfile": {
    "latestPosition": "Backend Developer",
    "experienceRangeId": 4,
    "yearsExperience": 5,
    "summary": "Desarrollador Java con experiencia en APIs REST."
  },
  "educations": [
    {
      "educationLevelId": 4,
      "degree": "Ingeniería en Informática",
      "institution": "Universidad de Chile"
    }
  ],
  "languages": [
    {
      "languageId": 2,
      "languageLevelId": 4
    }
  ],
  "hardSkills": [
    {
      "hardSkillId": 1,
      "level": "Avanzado",
      "yearsExperience": 5
    }
  ],
  "softSkills": [
    {
      "softSkillId": 1
    }
  ]
}
```

Reglas derivadas del caso de uso:

- La solicitud incluye como mínimo `firstName`, `lastName` y `email`; el resto de secciones son opcionales en la misma solicitud (contacto, perfil profesional —HU-35—, estudios —HU-36—, hard skills —HU-37—, soft skills —HU-38—).
- El email debe ser único en el sistema; si ya existe, responde **409 Conflict** (`EMAIL_ALREADY_EXISTS`).
- Si un `hardSkillId` o `softSkillId` no existe o está inactivo, responde **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- El postulante se crea con estado inicial `NEW` y activo por defecto.
- La respuesta incluye la ficha completa del postulante con todas las secciones registradas.

### HU-26 · Consultar ficha completa del postulante

Como recruiter autenticado, quiero consultar la ficha completa de un postulante por su identificador para revisar toda su información personal, profesional, curricular y documentos asociados.

Endpoint: `GET /api/candidates/{id}`

Reglas derivadas del caso de uso:

- Con token válido y permiso `RECRUITER_READ`, el sistema responde **200 OK** con la ficha completa.
- La respuesta incluye:
  - Datos personales: nombres, apellidos, email, teléfono, código de país y documento de identidad.
  - Perfil profesional: headline, resumen, último cargo, años de experiencia y rango de experiencia.
  - Estudios: nivel, título, institución y fechas.
  - Idiomas: idioma y nivel de dominio.
  - Habilidades técnicas: skill, nivel de dominio y años de experiencia.
  - Habilidades blandas: skill asociado.
  - Adjuntos: archivos cargados con su metadata.
  - Estado actual y metadatos del registro (fechas de creación y modificación).
- Si el postulante no existe, responde **404 Not Found** (`CANDIDATE_NOT_FOUND`).
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_READ`, responde **403 Forbidden**.

### HU-27 · Actualizar ficha del postulante

Como recruiter autenticado, quiero actualizar la información de un postulante para mantener su ficha al día.

Endpoint: `PUT /api/candidates/{id}`

Reglas derivadas del caso de uso:

- Con una solicitud válida, el sistema actualiza la ficha y responde **200 OK** con la ficha actualizada.
- Permite actualizar: datos de contacto, perfil profesional, experiencia, habilidades técnicas, habilidades blandas y observaciones.
- Si el postulante no existe, responde **404 Not Found** (`CANDIDATE_NOT_FOUND`).
- Si la solicitud es inválida o los datos no cumplen las reglas de negocio, responde **400 Bad Request** (`VALIDATION_ERROR` o `INVALID_CATALOG_REFERENCE`).
- Si el email ya lo usa otro postulante, responde **409 Conflict** (`EMAIL_ALREADY_EXISTS`).
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-28 · Cambiar estado del postulante

Como recruiter autenticado, quiero cambiar el estado de un postulante para reflejar el avance en el proceso de selección.

Endpoint: `PATCH /api/candidates/{id}/status`

Reglas derivadas del caso de uso:

- Con una solicitud válida, el sistema actualiza el estado y responde **200 OK** con la ficha actualizada.
- Los estados permitidos son: `NEW`, `IN_REVIEW`, `INTERVIEW`, `SHORTLIST`, `REJECTED`, `HIRED`.
- Si el estado enviado no pertenece al catálogo permitido, o la transición no es válida (estados terminales `REJECTED`/`HIRED` no admiten cambios posteriores), responde **400 Bad Request** (`INVALID_CANDIDATE_STATE`).
- Si el postulante no existe, responde **404 Not Found** (`CANDIDATE_NOT_FOUND`).
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-30 · Desactivar postulante

Como recruiter con permiso de escritura, quiero desactivar un postulante para ocultarlo del flujo activo sin eliminarlo físicamente de la base de datos.

Endpoint: `DELETE /api/candidates/{id}`

Reglas derivadas del caso de uso:

- Con un usuario autorizado, el sistema realiza borrado lógico y responde **204 No Content**.
- El postulante no se elimina físicamente; solo se marca como inactivo (`active = false`).
- El postulante desactivado no aparece en los listados activos (filtro `active`).
- Si el postulante no existe, responde **404 Not Found** (`CANDIDATE_NOT_FOUND`).
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-31 · Listar postulantes

Como recruiter autenticado, quiero listar los postulantes registrados en el ATS para revisar, buscar y filtrar candidatos dentro del sistema.

Endpoint: `GET /api/candidates?active={true|false}&search={texto}&page={n}&size={n}`

Reglas derivadas del caso de uso:

- Con una consulta válida, el sistema responde **200 OK** con una lista paginada de postulantes.
- Permite filtrar por estado activo/inactivo con el parámetro `active=true` o `active=false`.
- Permite buscar por texto libre con el parámetro `search`, con coincidencias en nombre, apellido y email sin distinción de mayúsculas.
- Si no hay resultados, responde **200 OK** con una lista vacía.
- La respuesta incluye información básica del postulante: nombres, apellidos, email, estado y fecha de creación.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_READ`, responde **403 Forbidden**.

### HU-34 · Consultar estados posibles del postulante

Como recruiter autenticado, quiero consultar los estados posibles de un postulante para usarlos correctamente en el seguimiento del proceso.

Endpoint: `GET /api/candidates/statuses`

Reglas derivadas del caso de uso:

- Con una consulta válida, el sistema responde **200 OK** con el catálogo completo de estados disponibles.
- El catálogo incluye: `NEW`, `IN_REVIEW`, `INTERVIEW`, `SHORTLIST`, `REJECTED`, `HIRED`.
- Cada estado incluye su código (`code`) y etiqueta descriptiva (`label`) en español.
- El orden del catálogo respeta la enumeración (NEW → IN_REVIEW → INTERVIEW → SHORTLIST → REJECTED → HIRED), no alfabético.
- Las etiquetas son fijas (constantes del código); no se agregan ni eliminan estados en runtime.
- El catálogo es completo: no acepta paginación ni filtros.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_READ`, responde **403 Forbidden**.

### HU-35 · Registrar perfil profesional del candidato

Como recruiter autenticado, quiero registrar el perfil profesional del candidato para conocer su último cargo y experiencia dentro de la ficha del ATS.

Endpoint relacionado: `POST /api/candidates`

Reglas derivadas del caso de uso:

- El perfil profesional se registra junto con el alta del candidato (`POST /api/candidates`) — ver HU-26.
- Permite guardar: `headline`, `summary`, `latestPosition`, `yearsExperience` y `experienceRangeId`.
- El `experienceRangeId` debe corresponder a un id válido del catálogo de rangos — ver HU-40.
- El perfil queda asociado al candidato creado (objeto anidado `professionalProfile` en el request).
- Si el `experienceRangeId` no existe o está inactivo, responde **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- Todos los campos del perfil son opcionales al momento del alta; si no se envía el objeto, el candidato se crea sin perfil.
- `yearsExperience` debe ser un entero no negativo (≥ 0).
- `headline` y `latestPosition` ≤ 180 caracteres; `summary` ≤ 4000 caracteres.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-36 · Registrar estudios del candidato

Como recruiter autenticado, quiero registrar los estudios del candidato para clasificar su formación académica dentro de la ficha del ATS.

Endpoint relacionado: `POST /api/candidates`

Reglas derivadas del caso de uso:

- Los estudios se registran junto con el alta del candidato (`POST /api/candidates`) — ver HU-26.
- Permite asociar uno o más estudios al candidato; los estudios son opcionales en el alta.
- Cada estudio incluye: `educationLevelId` (obligatorio), `degree` (título), `institution` (institución), `startDate` (fecha de inicio) y `endDate` (fecha de término, opcional).
- El `educationLevelId` debe corresponder a un nivel activo del catálogo — ver HU-41.
- Los estudios quedan asociados al candidato creado.
- Si el `educationLevelId` no existe o está inactivo, responde **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- Si se envía un estudio sin `educationLevelId`, responde **400 Bad Request** (`VALIDATION_ERROR`).
- Si se envían `startDate` y `endDate`, `startDate` debe ser ≤ `endDate`; en caso contrario **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- `degree` e `institution` ≤ 180 caracteres.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-37 · Registrar habilidades técnicas del postulante

Como recruiter autenticado, quiero registrar las habilidades técnicas de un postulante para identificar sus competencias técnicas dentro de la ficha del ATS.

Endpoint relacionado: `POST /api/candidates`

Reglas derivadas del caso de uso:

- Las habilidades técnicas se registran junto con el alta del candidato (`POST /api/candidates`) — ver HU-26.
- Permite registrar una o más habilidades técnicas al crear la ficha; las habilidades son opcionales en el alta.
- Cada habilidad técnica debe estar asociada a un `hardSkillId` existente y activo del catálogo.
- Permite registrar `level` (nivel de dominio, texto libre ≤ 80 caracteres), si viene informado.
- Permite registrar `yearsExperience` (años de experiencia por habilidad, entero ≥ 0), si viene informado.
- Si se informa un `hardSkillId` inexistente o inactivo, responde **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- Si se envía una habilidad sin `hardSkillId`, responde **400 Bad Request** (`VALIDATION_ERROR`).
- Las habilidades técnicas quedan asociadas al postulante creado.
- La respuesta del postulante incluye las habilidades técnicas registradas.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

### HU-38 · Registrar habilidades blandas del postulante

Como recruiter autenticado, quiero registrar las habilidades blandas de un postulante para complementar su evaluación profesional dentro del ATS.

Endpoint relacionado: `POST /api/candidates`

Reglas derivadas del caso de uso:

- Las habilidades blandas se registran junto con el alta del candidato (`POST /api/candidates`) — ver HU-26.
- Permite registrar una o más habilidades blandas al crear la ficha; las habilidades son opcionales en el alta.
- Cada habilidad blanda debe estar asociada a un `softSkillId` existente y activo del catálogo.
- Si se informa un `softSkillId` inexistente o inactivo, responde **400 Bad Request** (`INVALID_CATALOG_REFERENCE`).
- Si se envía una habilidad sin `softSkillId`, responde **400 Bad Request** (`VALIDATION_ERROR`).
- Las habilidades blandas quedan asociadas al postulante creado.
- La respuesta del postulante incluye las habilidades blandas registradas.
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_WRITE`, responde **403 Forbidden**.

## Fuera de alcance

- Endpoints individuales por sub-recurso (educación, idioma, skill…): se gestionan solo dentro del CRUD del candidato (backlog).
- Eventos Kafka de cambio de estado (`candidate.status.changed`): pendiente (roadmap).
- Parsing de CVs y adjuntos: viven en `003-adjuntos` y `004-parsing-cv`.
- Autenticación de usuarios: delegada a ats-usuarios; aquí solo se consume el JWT para extraer `recruiterId`.
