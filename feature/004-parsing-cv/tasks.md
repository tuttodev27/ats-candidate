# 004 · Parsing de CVs — Tareas

_Checklist accionable derivada del `plan.md`. Tareas pequeñas y concretas; marca `[x]` al completarlas._

- [x] Extracción de texto con Apache PDFBox (`Loader.loadPDF` + `PDFTextStripper`).
- [x] `CvParser` (regex): email, teléfono, nombre/apellido, headline, resumen, último cargo, educación y matching de skills por palabra completa.
- [x] `AiCvParser` (`@Component`): chequeo de disponibilidad, truncado a `max-text-length`, prompt JSON estricto y mapeo a `ParsedCv` con fallback `null`.
- [x] `OllamaClient`: `chat()` con `format:"json"` y `isAvailable()` vía `GET /api/tags`.
- [x] `OllamaProperties` con `base-url`, `model`, `timeout-seconds`, `max-text-length` (configurable por env `OLLAMA_*`).
- [x] Modelo `CandidateParseResult` + puerto `CandidateParseResultRepositoryPort` + entidad JPA, adapter y mapper de persistencia.
- [x] Orquestación en `AttachmentService`: fallback IA → regex, persistencia del resultado, actualización `COMPLETED`/`FAILED`.
- [x] `preFillCandidate`: solo campos vacíos; skills resueltas por nombre con `source="CV_PARSER"` y `confidence=1.0`; educaciones con nivel mapeado a `education_level.id` (default 1).
- [x] Manejo de PDF corrupto/sin texto con `AttachmentParsingException`.
- [x] Cobertura vía `AttachmentServiceTest` (parse con IA, fallback regex, PDF sin texto, pre-llenado).
- [x] Correr `./gradlew check` y verificar que todos los tests pasen.
- [x] Actualizar `docs/contexto/` si aplica (decisiones, flujo, errores conocidos).
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.

## Mantenimiento (checklist recurrente)

- [ ] Añadir tests unitarios dedicados para `CvParser`, `AiCvParser` y `OllamaClient` (pendiente, ver `docs/contexto/errores-conocidos.md` #8).
- [ ] Aplicar `timeoutSeconds` al `RestClient` de Ollama (pendiente en backlog).
- [ ] Al cambiar el modelo de Ollama, re-validar el `SYSTEM_PROMPT` y el mapeo JSON.
