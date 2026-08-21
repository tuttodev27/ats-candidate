# 003 · Adjuntos (CVs)

**Estado:** implementado ✅

## Qué hace

Gestiona el CV en PDF de los candidatos: subida con validación de formato, almacenamiento en disco, listado, descarga del contenido y reparsing manual. Al subir un CV se dispara automáticamente el parseo (flujo descrito en `004-parsing-cv`).

## Por qué

El CV es la fuente primaria del perfil del postulante. Asociar el documento curricular a la ficha permite al reclutador guardar el archivo original, consultarlo inline sin descargarlo, y aprovechar el parseo automático para pre-llenar la ficha del candidato sin digitación manual.

## Criterios de aceptación

### HU · Subir CV en PDF del postulante

Endpoint: `POST /api/candidates/{candidateId}/attachments`

- [x] Acepta `multipart/form-data` con el campo `file` y responde **201 Created** con la metadata del adjunto; `uploadedBy` se extrae del JWT.
- [x] Solo se aceptan archivos PDF: extensión `.pdf` y content-type `application/pdf`, archivo no vacío; en caso contrario **400** `INVALID_ATTACHMENT`.
- [x] Se valida que el postulante exista antes de guardar; si no, **404** `CANDIDATE_NOT_FOUND`.
- [x] El archivo se almacena en disco (`LocalAttachmentStorageAdapter`) en `app.attachments.storage-path` bajo `candidates/{candidateId}/{UUID}-{nombre}`, con checksum SHA-256 persistido.
- [x] Se persiste la metadata: fileName, fileUrl, fileType, fileSize, checksum, uploadedAt, uploadedBy y `parseStatus = PENDING`.
- [x] El checksum SHA-256 se calcula sobre los bytes recibidos antes del almacenamiento.
- [x] Al subir se dispara el parseo automático; al terminar el adjunto queda en `COMPLETED` (o `FAILED` con `parseError`).
- [x] Si ocurre un error al almacenar el archivo, responde **500** `ATTACHMENT_STORAGE_ERROR`.
- [x] Sin token o token inválido → **401**; sin `RECRUITER_WRITE` → **403**.

### HU-33 · Listar adjuntos del postulante

Como recruiter autenticado, quiero listar los archivos adjuntos de un postulante para revisar los documentos cargados en su ficha.

Endpoint: `GET /api/candidates/{candidateId}/attachments`

Reglas derivadas del caso de uso:

- Con una consulta válida, el sistema responde **200 OK** con la lista de adjuntos del postulante.
- El sistema valida que el postulante exista antes de listar.
- Los adjuntos se devuelven ordenados por `uploadedAt` descendente (más recientes primero).
- La respuesta incluye por cada adjunto: `id`, `candidateId`, `fileName`, `fileUrl`, `fileType`, `fileSize`, `checksum`, `uploadedAt`, `uploadedBy` y `parseStatus`.
- El listado no filtra por `parseStatus`: incluye `PENDING`, `COMPLETED` y `FAILED`.
- Si el postulante no tiene adjuntos, responde **200 OK** con una lista vacía (`[]`).
- Si el postulante no existe, responde **404 Not Found** (`CANDIDATE_NOT_FOUND`).
- Si no se envía token o el token es inválido, responde **401 Unauthorized**.
- Si el usuario no tiene permiso `RECRUITER_READ`, responde **403 Forbidden**.

### Otras operaciones de adjuntos

- [x] `GET /api/candidates/{candidateId}/attachments/{attachmentId}/content` devuelve el contenido con `Content-Disposition: inline` y content-type del archivo; 404 `ATTACHMENT_NOT_FOUND` si no existe y error si el adjunto no pertenece al candidato.
- [x] `POST /api/candidates/{candidateId}/attachments/{attachmentId}/parse` repara el CV manualmente y actualiza el `parseStatus`.
- [x] `GET /api/candidates/1/attachments` requiere JWT y autorización (`AttachmentListSecurityTest`: 401/403/200 con `ROLE_RECRUITER`).
- [x] Requisito de calidad: cubierto por `AttachmentServiceTest`, `AttachmentControllerTest`, `AttachmentListSecurityTest` y `LocalAttachmentStorageAdapterTest`.

## Fuera de alcance

- Extracción de texto y parsing del CV (PDFBox + IA + regex): feature `004-parsing-cv`.
- Pre-llenado de la ficha del candidato con datos del CV: feature `004-parsing-cv`.
- Otros tipos de archivo (imágenes, DOCX, portafolios): hoy solo PDF.
- Subida múltiple de archivos en una sola llamada: una parte `file` por petición.
- Filtros por `parseStatus` o tipo de archivo y paginación en `GET /api/candidates/{candidateId}/attachments`.
- Storage en la nube (S3) o volumen persistente: hoy storage local en `build/uploads/` (ver `docs/contexto/errores-conocidos.md` #4).
