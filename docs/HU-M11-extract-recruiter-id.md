# HU-M11 — Centralizar extractRecruiterId() en un helper común

**Como** desarrollador
**Quiero** extraer el método `extractRecruiterId()` duplicado en `CandidateController` y `AttachmentController` a una clase compartida
**Para** evitar duplicación de lógica y facilitar el mantenimiento.

### Criterios de aceptación

1. Crear una clase utilitaria (`JwtRecruiterExtractor` o similar) como `@Component` o clase estática.
2. Reemplazar las implementaciones duplicadas en ambos controllers por llamadas al helper.
3. Mantener la misma lógica de extracción de claims (`recruiterId`, `recruiter_id`, `userId`, `user_id`, subject).
4. Mantener el manejo de errores (`InvalidRecruiterException`).
5. Tests del helper.

**Archivos:** Nuevo helper + `CandidateController.java`, `AttachmentController.java`
