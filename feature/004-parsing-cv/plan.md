# 004 · Parsing de CVs — Plan

_Cómo se implementa lo descrito en `spec.md`. Debe respetar la `../../constitution/`._

## Enfoque

Cadena de extracción en 4 etapas orquestada por `AttachmentService`: (1) extracción de texto con PDFBox; (2) parseo preferente con IA (`AiCvParser` → `OllamaClient`) que devuelve `null` si no está disponible o falla; (3) fallback a `CvParser` (regex) sobre texto plano; (4) persistencia del `CandidateParseResult` y pre-llenado del candidato solo en campos vacíos. La IA corre local (privacidad/costo) y el parser regex garantiza funcionamiento sin Ollama.

## Implementación

_Pasos técnicos concretos, en orden. Indica los archivos/módulos que se tocan, siguiendo la estructura hexagonal._

1. `application/parser/CvParser.java` — parser regex estático (sin Spring): extrae email, teléfono (mínimo 7 dígitos), nombre/apellido (`nombre:`/`apellido:` o primera línea), headline, resumen, último cargo, educación y skills por palabra completa (`\b...\b`); heurística de nivel educativo por keywords (doctorado→…→educación media) y varios formatos de fecha.
2. `application/parser/AiCvParser.java` — `@Component`: `parse(text, hardSkillNames, softSkillNames)`; si `ollamaClient.isAvailable()` es false devuelve `null`; trunca a `max-text-length` (12000); construye el mensaje con el texto + skills disponibles; mapea la respuesta JSON a `ParsedCv`; `SYSTEM_PROMPT` en español exige un único JSON válido con estructura exacta y reglas de null/formato de fechas.
3. `infrastructure/out/ollama/OllamaClient.java` — `@Component` con `RestClient` a `{base-url}/api/chat` (body `{model, messages, stream:false, format:"json"}`) y `isAvailable()` vía `GET /api/tags` (cualquier excepción → false).
4. `infrastructure/config/OllamaProperties.java` — `base-url` (default `http://localhost:11434`), `model` (`llama3`), `timeout-seconds` (60), `max-text-length` (12000); configurables por env `OLLAMA_*`.
5. `domain/model/CandidateParseResult.java` + `domain/port/out/repository/CandidateParseResultRepositoryPort.java` + `infrastructure/out/entity/CandidateParseResultEntity.java` + adapter + mapper de persistencia.
6. `application/service/AttachmentService.java` — orquestación: PDFBox (`Loader.loadPDF`/`PDFTextStripper`), nombres de skills activos vía `CandidateCatalogValidationPort`, `AiCvParser` → fallback `CvParser`, persistencia del resultado, `preFillCandidate` (solo campos vacíos) y actualización del adjunto a `COMPLETED`/`FAILED`.
7. `application/parser/ParsedCv` (o equivalente en `domain/model`) — estructura extraída: datos personales, headline, summary, latestPosition, educaciones, matchedHardSkills, matchedSoftSkills.
8. Tests: cobertura indirecta en `AttachmentServiceTest` (13 tests). Tests dedicados de `CvParser`/`AiCvParser`/`OllamaClient` pendientes (backlog).

## Decisiones

- **IA como mejora con fallback** — la disponibilidad de Ollama se chequea en runtime (`isAvailable()`) y el resultado se valida al mapear; si no hay IA se usa regex. Alternativa (depender siempre de la IA) descartada por disponibilidad variable.
- **Solo completar campos vacíos (`preFillCandidate`)** — el parseo nunca sobreescribe datos ya completados por el reclutador; mantiene el control humano (regla HU-44).
- **`SYSTEM_PROMPT` con JSON estricto** — se exige un único JSON válido con estructura exacta, fechas `YYYY-MM-DD` y nivel educativo acotado; reduce alucinaciones y facilita el mapeo.
- **Truncado a 12000 caracteres** — limita el costo y el contexto del modelo local; configurable vía `ollama.max-text-length`.
- **Regex como parser determinista** — `CvParser` es estático y sin Spring, fácil de testear y suficiente para CVs bien formateados; es la red de seguridad del sistema.
- **Ollama local (no Claude API)** — decisión de privacidad/costo (ver `docs/contexto/decisiones.md`); el cliente está aislado en `OllamaClient` para poder sustituir el proveedor.

## Riesgos

- **Sin tests dedicados de los parsers** — `CvParser`/`AiCvParser`/`OllamaClient` solo se cubren indirectamente (error conocido #8). Mitigación: añadir tests unitarios dedicados (backlog).
- **Alucinaciones del modelo local** — `llama3` puede inventar datos si el prompt es ambiguo. Mitigación: prompt estricto, mapeo con validación y fallback a `null` en error; el resultado siempre se revisa porque solo rellena campos vacíos.
- **PDF sin OCR/escaneado** — PDFBox devuelve texto vacío y el adjunto pasa a `FAILED`. Mitigación: `AttachmentParsingException` con mensaje claro y endpoint `parse` para reintentar.
- **`timeoutSeconds` de Ollama sin aplicar al `RestClient`** — la propiedad existe pero no se configura un `ClientHttpRequestFactory` con timeouts. Mitigación: revisar y aplicarlo (backlog).
- **JSON inválido de la IA** — si el parseo del JSON falla, `AiCvParser` loguea y devuelve `null`, cayendo al regex. Mitigación inherente al diseño.
