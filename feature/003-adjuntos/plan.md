# 003 · Adjuntos (CVs) — Plan

_Cómo se implementa lo descrito en `spec.md`. Debe respetar la `../../constitution/`._

## Enfoque

Feature implementada con el patrón hexagonal: el agregado `Attachment` (con `AttachmentUpload`, `AttachmentContent`, `StoredAttachment`) vive en `domain/model`; el caso de uso `AttachmentService` orquesta validación → storage → persistencia → parseo; la persistencia usa JPA y el almacenamiento físico está aislado detrás del puerto `AttachmentStoragePort` (implementado por `LocalAttachmentStorageAdapter`), de modo que el storage es intercambiable sin tocar el dominio. El parseo automático se dispara dentro de `uploadCv` y se delega a la feature `004-parsing-cv`.

## Implementación

_Pasos técnicos concretos, en orden. Indica los archivos/módulos que se tocan, siguiendo la estructura hexagonal._

1. `domain/model/` — `Attachment` (id, candidateId, fileName, fileUrl, fileType, fileSize, checksum, uploadedAt, uploadedBy, parseStatus, parseError), `AttachmentUpload`, `AttachmentContent`, `StoredAttachment`.
2. `domain/port/out/repository/AttachmentRepositoryPort.java` — `save`, `findByCandidateId`, `findById`, `findByIdAndCandidateId`.
3. `domain/port/out/storage/AttachmentStoragePort.java` — `store(AttachmentUpload, candidateId)`, `load(fileUrl)`.
4. `domain/exception/` — `InvalidAttachmentException`, `AttachmentNotFoundException`, `AttachmentStorageException`, `AttachmentParsingException`.
5. `application/service/AttachmentService.java` — `@Service` `@Transactional`: valida candidato existente, valida PDF (extensión + content-type + no vacío), persiste `PENDING`, llama al storage, dispara parseo, actualiza a `COMPLETED`/`FAILED`.
6. `infrastructure/out/entity/AttachmentEntity.java` + `AttachmentJpaRepository` + `AttachmentRepositoryAdapter` + `AttachmentPersistenceMapper`.
7. `infrastructure/out/storage/LocalAttachmentStorageAdapter.java` — guarda en `<storage-path>/candidates/{candidateId}/{UUID}-{nombre}`, retorna `fileUrl` relativo, lee el contenido al cargar.
8. `infrastructure/in/web/controller/AttachmentController.java` — `uploadCv` (convierte `MultipartFile` → `AttachmentUpload`), `listByCandidateId`, `getContent` (inline), `parseCv`.
9. `infrastructure/in/web/dto/AttachmentResponse.java` + mapper de persistencia para la respuesta.
10. `application/parser/` — invocación a `CvParser`/`AiCvParser` (ver `004-parsing-cv`) dentro del flujo de subida.
11. Tests espejo: `AttachmentServiceTest`, `AttachmentControllerTest`, `AttachmentListSecurityTest`, `LocalAttachmentStorageAdapterTest`.

## Flujo del caso de uso — subir CV en PDF (HU · Subir CV en PDF del postulante)

Para `POST /api/candidates/{candidateId}/attachments`, el flujo es:

1. `AttachmentController.uploadCv` recibe el `MultipartFile` (parte `file`) y convierte a `AttachmentUpload` (con `uploadedBy` extraído del JWT).
2. `AttachmentService.uploadCv` verifica que el candidato exista; si no, lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. Valida el archivo con `validateUpload`: no vacío, extensión `.pdf` y content-type `application/pdf`; si no cumple, `InvalidAttachmentException` → 400 `INVALID_ATTACHMENT`.
4. `AttachmentStoragePort.store` guarda el archivo en `<storage-path>/candidates/{candidateId}/{UUID}-{nombre}` y calcula el checksum SHA-256 sobre los bytes recibidos.
5. Persiste `Attachment` con `parseStatus = PENDING` y todos los metadatos (fileName, fileUrl, fileType, fileSize, checksum, uploadedAt, uploadedBy).
6. Dispara el parseo automático (feature `004-parsing-cv`); al terminar actualiza a `COMPLETED`/`FAILED`.
7. Responde 201 con la metadata del adjunto; un error al almacenar lanza `AttachmentStorageException` → 500.

### Flujo del caso de uso — listar adjuntos (HU-33)

Para `GET /api/candidates/{candidateId}/attachments`, el flujo es:

1. `AttachmentController.listByCandidateId` recibe el path `{candidateId}`.
2. `AttachmentService.listByCandidateId` verifica que el candidato exista; si no, lanza `CandidateNotFoundException` → 404 `CANDIDATE_NOT_FOUND`.
3. `AttachmentRepositoryPort.findByCandidateId` recupera los adjuntos ordenados por `uploadedAt` descendente.
4. Mapea cada `Attachment` a `AttachmentResponse` con id, candidateId, fileName, fileUrl, fileType, fileSize, checksum, uploadedAt, uploadedBy y parseStatus.
5. Responde 200 con la lista; sin adjuntos responde 200 con lista vacía.

Nota: el listado no pagina ni filtra por `parseStatus`; incluye `PENDING`, `COMPLETED` y `FAILED` (ver Fuera de alcance en `spec.md`).

## Decisiones

- **Solo PDF** — se restringe a `application/pdf` + extensión `.pdf` para garantizar que el parseo con PDFBox siempre tenga texto extraíble. Se descartó aceptar DOCX/ODT.
- **Storage detrás de puerto** — `AttachmentStoragePort` aísla el sistema de archivos; `LocalAttachmentStorageAdapter` es la implementación actual pero se puede sustituir por S3 sin tocar dominio ni casos de uso.
- **Nombre único con UUID** — `{UUID}-{nombre}` evita colisiones y sanitiza nombres duplicados entre candidatos.
- **Checksum SHA-256** — se persiste para detectar archivos duplicados/corruptos y dar trazabilidad.
- **Parseo automático en `uploadCv`** — el parseo se dispara en el mismo flujo de subida con try/catch que deja el adjunto en `FAILED` con el error si algo falla; se mantiene además el endpoint manual `parse` para reintentos.
- **`Content-Disposition: inline`** — la descarga se sirve inline para visualización directa en el navegador en lugar de forzar descarga.
- **Storage en `build/uploads/`** — decisión deliberada de desarrollo (ver `docs/contexto/decisiones.md`); los archivos se pierden con `gradle clean` (error conocido #4).

## Riesgos

- **Pérdida de archivos con `gradle clean`** — el storage default está en `build/`. Mitigación: configurar `app.attachments.storage-path` a una ruta persistente (pendiente en backlog).
- **PDF corrupto o sin texto** — el parseo falla y deja el adjunto en `FAILED`. Mitigación: `AttachmentParsingException` con mensaje claro y endpoint `parse` para reintentar.
- **Adjunto de otro candidato** — riesgo de acceso cruzado. Mitigación: `loadContent`/`parse` validan que el adjunto pertenezca al `candidateId` (`findByIdAndCandidateId`).
- **Path traversal en el nombre del archivo** — el storage construye la ruta con el nombre del upload. Mitigación: el controller pasa `MultipartFile.getOriginalFilename()` y el storage antepone UUID; conviene validar el nombre (revisión en backlog).
