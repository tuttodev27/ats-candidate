# Roadmap

_Orden y estado de las features. Es la vista de "qué hay hecho, qué toca ahora y qué viene". Cada entrada apunta a su carpeta en `feature/`._

## Hecho ✅

_Features completadas, en orden de implementación._

1. **001 · Gestión de candidatos** — CRUD del postulante con perfil completo (educación, idiomas, skills, experiencia, notas), borrado lógico, estados y transiciones del proceso de selección con historial.
2. **002 · Catálogos** — catálogos de solo lectura (códigos de país, idiomas, niveles de idioma, niveles de estudios, rangos de experiencia) sembrados con `data.sql`.
3. **003 · Adjuntos (CVs)** — subida/validación de CV en PDF, storage en disco, listado, descarga inline y reparsing manual.
4. **004 · Parsing de CVs** — extracción de texto con PDFBox, parsing con IA (Ollama) con fallback regex y pre-llenado de campos vacíos del candidato.

## Siguiente 🔜

_Lo próximo a abordar. Idealmente una sola feature "en curso" a la vez._

5. **005 · Eventos Kafka** — publicar/consumir eventos del dominio (candidato creado, estado cambiado, CV parseado) hacia el ecosistema ATS.
6. **006 · Sub-recursos de candidato** — endpoints individuales por educación/idioma/skill/experiencia para operar fuera del CRUD completo.

## Backlog / ideas 💡

_Sin comprometer ni ordenar del todo. Ideas que respetan la constitución._

- **Parseo previo con endpoint dedicado** — `POST /api/candidates/cv/parse` con `confidenceScore` y 422 si el PDF no tiene OCR.
- **Migraciones Flyway reales** — reemplazar `ddl-auto: update` + `data.sql` por migraciones versionadas.
- **Storage persistente de adjuntos** — mover `build/uploads/` a una ruta fuera de `build/` (o S3).
- **Optimizar `loadDetails()`** — eliminar el N+1 (~11 queries) de la ficha completa.
- **Upsert en `update()`** — reemplazar el delete + insert de colecciones por sincronización.
- **Tests de parsers** — unitarios dedicados para `CvParser`, `AiCvParser` y `OllamaClient`.
- **Externalizar `JWT_SECRET`** — exigir la variable de entorno y eliminar el default hardcodeado.
- **Aplicar `timeoutSeconds` de Ollama** — configurar timeouts en el `RestClient`.

> Cada feature nueva se crea como `feature/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código.
