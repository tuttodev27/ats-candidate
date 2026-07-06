# HU-M04 — Agregar experiences y notes al CreateCandidateRequest

**Como** recruiter
**Quiero** poder crear un candidato con experiencias laborales y notas incluidas en el POST inicial
**Para** no tener que crearlo primero y luego actualizarlo con PUT.

### Criterios de aceptación

1. `CreateCandidateRequest` incluye campos `Set<CandidateExperienceRequest> experiences` y `Set<CandidateNoteRequest> notes`.
2. El mapper `CandidateWebMapper.toDomain(CreateCandidateRequest)` mapea estos campos (quitar `ignore = true`).
3. `CandidateService.create()` persiste experiences y notes correctamente.
4. La respuesta `CandidateResponse` incluye los datos creados.
5. Tests de integración que verifiquen creación con experiences y notes.

**Archivos:** `infrastructure/in/web/dto/CreateCandidateRequest.java`, `infrastructure/in/web/mapper/CandidateWebMapper.java`, `application/service/CandidateService.java`
