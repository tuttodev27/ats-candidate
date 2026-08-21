# 003 · Adjuntos (CVs) — Tareas

_Checklist accionable derivada del `plan.md`. Tareas pequeñas y concretas; marca `[x]` al completarlas._

- [x] Modelos de dominio `Attachment`, `AttachmentUpload`, `AttachmentContent`, `StoredAttachment`.
- [x] Excepciones `InvalidAttachmentException`, `AttachmentNotFoundException`, `AttachmentStorageException`, `AttachmentParsingException`.
- [x] Puertos de salida `AttachmentRepositoryPort` y `AttachmentStoragePort`.
- [x] Caso de uso `AttachmentUseCase` → `AttachmentService` (`uploadCv`, `listByCandidateId`, `parse`, `loadContent`).
- [x] Validación de PDF (extensión `.pdf`, content-type `application/pdf`, archivo no vacío).
- [x] Persistencia: `AttachmentEntity`, `AttachmentJpaRepository`, `AttachmentRepositoryAdapter`, `AttachmentPersistenceMapper`.
- [x] Storage en disco: `LocalAttachmentStorageAdapter` (ruta `candidates/{candidateId}/{UUID}-{nombre}`, load de contenido).
- [x] Controller `AttachmentController` con `uploadCv`, `listByCandidateId`, `getContent` (inline), `parseCv`.
- [x] DTO `AttachmentResponse` con metadatos y `parseStatus`.
- [x] Disparo del parseo automático en `uploadCv` con manejo `COMPLETED`/`FAILED`.
- [x] Reglas de autorización en `SecurityConfig` para adjuntos.
- [x] Tests: `AttachmentServiceTest`, `AttachmentControllerTest`, `AttachmentListSecurityTest`, `LocalAttachmentStorageAdapterTest`.
- [x] Correr `./gradlew check` y verificar que todos los tests pasen.
- [x] Actualizar `docs/contexto/` si aplica (decisiones, flujo, errores conocidos).
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
- [x] Validar el caso de uso HU-33 con `GET /api/candidates/{candidateId}/attachments`: 200 con lista ordenada por `uploadedAt` desc y metadata por adjunto (id, candidateId, fileName, fileUrl, fileType, fileSize, checksum, uploadedAt, uploadedBy, parseStatus incluyendo PENDING/COMPLETED/FAILED), 200 lista vacía sin adjuntos, 404 `CANDIDATE_NOT_FOUND`, 401 sin token, 403 sin `RECRUITER_READ`.

## Mantenimiento (checklist recurrente)

- [ ] Configurar `app.attachments.storage-path` fuera de `build/` (ver `docs/contexto/errores-conocidos.md` #4).
- [ ] Al añadir tipos de archivo distintos de PDF, revisar la validación y el parseo.
