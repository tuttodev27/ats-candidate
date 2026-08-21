# 002 · Catálogos — Tareas

_Checklist accionable derivada del `plan.md`. Tareas pequeñas y concretas; marca `[x]` al completarlas._

- [x] Modelos de dominio `CountryCode`, `Language`, `LanguageLevel`, `EducationLevel`, `ExperienceRange`.
- [x] Puertos de salida `XxxRepositoryPort` con `findActive()`.
- [x] Puertos de entrada `XxxUseCase` con `listActive()`.
- [x] Servicios `CountryCodeService`, `LanguageService`, `LanguageLevelService`, `EducationLevelService`, `ExperienceRangeService`.
- [x] Entidades JPA `CountryCodeEntity`, `LanguageEntity`, `LanguageLevelEntity`, `EducationLevelEntity`, `ExperienceRangeEntity` + repositorios JPA con consulta de activos ordenada.
- [x] Adapters `XxxRepositoryAdapter` y mappers de persistencia `XxxPersistenceMapper`.
- [x] Controllers GET (`/api/country-codes`, `/api/languages`, `/api/language-levels`, `/api/education-levels`, `/api/experience-ranges`).
- [x] DTOs `XxxResponse` y mappers web `XxxWebMapper` (MapStruct).
- [x] Seed en `src/main/resources/data.sql` con `INSERT ... ON CONFLICT (id) DO UPDATE` (13 países, 6 idiomas, 7 niveles de idioma, 6 niveles de estudios, 6 rangos de experiencia).
- [x] Tests: `CatalogServiceTest` + `CountryCodeControllerTest`, `LanguageControllerTest`, `LanguageLevelControllerTest`, `EducationLevelControllerTest`, `ExperienceRangeControllerTest`.
- [x] Correr `./gradlew check` y verificar que todos los tests pasen.
- [x] Actualizar `docs/contexto/` si aplica (decisiones, flujo, errores conocidos).
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
- [x] Validar el caso de uso HU-39 con `GET /api/country-codes`: 200 con códigos de país activos (solo `active = true`), ordenados por `orderNumber` y luego `countryName`, con campos `id`, `countryName`, `isoCode`, `phoneCode`, sin JWT requerido, sin paginación ni filtros.
- [x] Validar el caso de uso HU-40 con `GET /api/experience-ranges`: 200 con rangos activos (solo `active = true`), ordenados por `orderNumber`, con campos `id`, `label`, `minYears`, `maxYears`, sin JWT requerido, sin paginación ni filtros.

## Mantenimiento (checklist recurrente)

- [ ] Al agregar un catálogo nuevo, replicar el mismo patrón y agregar su test por controller.
- [ ] Mantener el orden natural en la consulta del repositorio (p. ej. A1→C2, 0→10+ años).
