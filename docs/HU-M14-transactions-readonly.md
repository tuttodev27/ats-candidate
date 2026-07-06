# HU-M14 — Agregar @Transactional(readOnly = true) en consultas

**Como** desarrollador
**Quiero** marcar los métodos de solo lectura con `@Transactional(readOnly = true)`
**Para** optimizar las conexiones a DB y evitar locks innecesarios.

### Criterios de aceptación

1. `CandidateService.getById()` tiene `@Transactional(readOnly = true)`.
2. `CandidateService.list()` tiene `@Transactional(readOnly = true)`.
3. `CandidateService.loadDetails()` tiene `@Transactional(readOnly = true)`.
4. `CandidateService.getStatuses()` tiene `@Transactional(readOnly = true)`.
5. `CandidateService.getStatusHistory()` tiene `@Transactional(readOnly = true)`.
6. Métodos de escritura (`create`, `update`, `deactivate`, `updateStatus`) mantienen `@Transactional` sin readOnly.
7. No hay operaciones de escritura dentro de métodos marcados como readOnly.
8. Tests.

**Archivo:** `application/service/CandidateService.java`
