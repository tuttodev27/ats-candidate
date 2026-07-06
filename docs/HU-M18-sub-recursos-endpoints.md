# HU-M18 — Endpoints dedicados para sub-recursos del candidato

**Como** recruiter
**Quiero** poder agregar, modificar y eliminar estudios, idiomas, habilidades y experiencias de forma individual
**Para** no tener que enviar la ficha completa en cada cambio.

### Criterios de aceptación

1. `POST /api/candidates/{id}/educations` — agrega un estudio, responde 201 Created.
2. `PUT /api/candidates/{id}/educations/{eduId}` — modifica un estudio existente, responde 200 OK.
3. `DELETE /api/candidates/{id}/educations/{eduId}` — elimina un estudio, responde 204 No Content.
4. Ídem para: `languages`, `hard-skills`, `soft-skills`, `experiences`, `notes`.
5. Cada endpoint valida que el candidato exista (404 si no).
6. Cada endpoint requiere autorización `CANDIDATE_UPDATE` / `RECRUITER_WRITE`.
7. Los cambios quedan reflejados al consultar `GET /api/candidates/{id}`.
8. Tests de cada endpoint.

**Archivos:** `CandidateController.java` o nuevos controllers
