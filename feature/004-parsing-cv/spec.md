# 004 · Parsing de CVs

**Estado:** implementado ✅

## Qué hace

Extrae automáticamente los datos de un CV en PDF (nombre, apellido, email, teléfono, headline, resumen, último cargo, educación y habilidades hard/soft) y pre-rellena la ficha del candidato con los campos que estén vacíos. Usa IA con Ollama (JSON estructurado) con fallback a un parser regex local, persiste el resultado con trazabilidad y maneja estados `PENDING`/`COMPLETED`/`FAILED`.

## Por qué

El CV es la fuente primaria del perfil del postulante. El parseo elimina la digitación manual del reclutador y agiliza el registro, manteniendo el control humano: la IA se ejecuta en local (privacidad y costo cero) y solo completa campos vacíos, nunca sobreescribe lo ya completado.

## Criterios de aceptación

_Condiciones verificables que deben cumplirse para dar la feature por terminada. Redacta cada una de forma que se pueda comprobar con un sí/no. Marca `[x]` al cumplirse._

- [x] Al subir un CV (feature `003-adjuntos`) se dispara el parseo automático.
- [x] `POST /api/candidates/{candidateId}/attachments/{attachmentId}/parse` permite reparsizar manualmente.
- [x] El texto del PDF se extrae con Apache PDFBox (`Loader.loadPDF` + `PDFTextStripper`).
- [x] Si el PDF está corrupto o no tiene texto legible, el adjunto pasa a `FAILED` con `parseError` y se lanza `AttachmentParsingException`.
- [x] Si Ollama está disponible (`ollamaClient.isAvailable()`), se usa `AiCvParser` con prompt que exige un único JSON válido y `format: "json"`.
- [x] Si Ollama no está disponible o devuelve un JSON inválido, se usa el fallback `CvParser` (regex) sobre el texto plano.
- [x] Se persiste un `CandidateParseResult` (candidateId, attachmentId, rawText, parsedJson, status `COMPLETED`).
- [x] `preFillCandidate` solo completa campos vacíos (firstName, lastName, email, phone, perfil profesional, educaciones con nivel mapeado a `education_level.id` default 1, hard/soft skills matcheadas por nombre con `source="CV_PARSER"` y `confidence=1.0`).
- [x] Al terminar, el adjunto queda en `COMPLETED`.
- [x] El texto del CV se trunca a `ollama.max-text-length` (12000 caracteres) antes de enviarlo a la IA.
- [x] Requisito de calidad: `AiCvParser` y `CvParser` se ejercitan vía `AttachmentServiceTest` (parse con IA disponible, fallback a regex cuando IA devuelve null, PDF sin texto → excepción).

## Fuera de alcance

- Parseo previo al registro vía un endpoint dedicado (`POST /api/candidates/cv/parse`) con `confidenceScore`: pendiente (roadmap/backlog).
- Clasificación por confianza o revisión humana obligatoria de cada campo: hoy se rellena solo lo vacío sin score.
- Consumidores de eventos del resultado del parseo (`candidate.cv.parsed` en Kafka): pendiente (roadmap).
- Otros proveedores de IA (Claude API, etc.): hoy solo Ollama local.
