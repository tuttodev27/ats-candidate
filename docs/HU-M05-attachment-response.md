# HU-M05 — Exponer parseError y parsedAt en AttachmentResponse

**Como** recruiter
**Quiero** ver el error de parseo y la fecha en que se intentó parsear un CV
**Para** diagnosticar por qué falló la extracción de datos.

### Criterios de aceptación

1. `AttachmentResponse` incluye campos `LocalDateTime parsedAt` y `String parseError`.
2. El mapper `CandidateWebMapper.toResponse(Attachment)` mapea estos campos.
3. `parseError` se incluye como null si no hay error.
4. Tests de mapper y controller.

**Archivos:** `infrastructure/in/web/dto/AttachmentResponse.java`, `infrastructure/in/web/mapper/CandidateWebMapper.java`
