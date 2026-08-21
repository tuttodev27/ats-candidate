# 002 · Catálogos — Plan

_Cómo se implementa lo descrito en `spec.md`. Debe respetar la `../../constitution/`._

## Enfoque

Los 5 catálogos siguen exactamente el mismo patrón hexagonal: un modelo de dominio por catálogo, un puerto de salida `XxxRepositoryPort` con `findActive()`, un caso de uso de entrada `XxxUseCase` que delega en el repositorio, un controller GET, un mapper web MapStruct y una entidad JPA con su repositorio/adapter. El seed vive en `data.sql` con `ON CONFLICT DO UPDATE` para ser idempotente en cada arranque. No hay cache en esta feature (los GET van directo al repositorio).

## Implementación

_Pasos técnicos concretos, en orden. Indica los archivos/módulos que se tocan, siguiendo la estructura hexagonal._

1. `domain/model/` — `CountryCode`, `Language`, `LanguageLevel`, `EducationLevel`, `ExperienceRange` (modelos simples con `active`).
2. `domain/port/out/repository/` — `CountryCodeRepositoryPort`, `LanguageRepositoryPort`, `LanguageLevelRepositoryPort`, `EducationLevelRepositoryPort`, `ExperienceRangeRepositoryPort`, cada uno con `findActive()`.
3. `domain/port/in/usecase/` — `CountryCodeUseCase`, `LanguageUseCase`, `LanguageLevelUseCase`, `EducationLevelUseCase`, `ExperienceRangeUseCase` (un método `listActive()`).
4. `application/service/` — `CountryCodeService`, `LanguageService`, `LanguageLevelService`, `EducationLevelService`, `ExperienceRangeService` (delegan en sus repository ports).
5. `infrastructure/out/entity/` + `repository/` + `adapter/` + `mapper/` — entidades JPA (`CountryCodeEntity`, etc.), `XxxJpaRepository` con `findByActiveTrueOrderBy...`, `XxxRepositoryAdapter` y `XxxPersistenceMapper`.
6. `infrastructure/in/web/controller/` — `CountryCodeController`, `LanguageController`, `LanguageLevelController`, `EducationLevelController`, `ExperienceRangeController` (GET `/api/country-codes`, `/api/languages`, `/api/language-levels`, `/api/education-levels`, `/api/experience-ranges`).
7. `infrastructure/in/web/dto/` + `mapper/` — DTOs `XxxResponse` y `XxxWebMapper` (MapStruct).
8. `src/main/resources/data.sql` — seed de los 5 catálogos con `INSERT ... ON CONFLICT (id) DO UPDATE`.
9. Tests espejo: `CatalogServiceTest` + un test por controller de catálogo.

### Flujo del caso de uso — consultar códigos de país (HU-39)

Para `GET /api/country-codes`, el flujo es:

1. `CountryCodeController.listActive` llama a `CountryCodeService.listActive`.
2. `CountryCodeRepositoryPort.findActive` retorna los registros con `active = true`, ordenados por `orderNumber` ascendente y luego por `countryName` ascendente.
3. `CountryCodeWebMapper` mapea cada `CountryCode` a `CountryCodeResponse(id, countryName, isoCode, phoneCode)`.
4. Responde 200 con la lista completa; sin resultados responde 200 con lista vacía.

### Flujo del caso de uso — consultar rangos de experiencia (HU-40)

Para `GET /api/experience-ranges`, el flujo es:

1. `ExperienceRangeController.listActive` llama a `ExperienceRangeService.listActive`.
2. `ExperienceRangeRepositoryPort.findActive` retorna los registros con `active = true`, ordenados por `orderNumber` ascendente.
3. `ExperienceRangeWebMapper` mapea cada `ExperienceRange` a `ExperienceRangeResponse(id, label, minYears, maxYears)`.
4. Responde 200 con la lista completa; sin resultados responde 200 con lista vacía.

## Decisiones

- **Un patrón idéntico por catálogo** — se priorizó la consistencia sobre DRY: 5 servicios con la misma estructura son fáciles de mantener y extender. Alternativa (un servicio genérico parametrizado) descartada por acoplamiento innecesario.
- **Solo `findActive()`** — la API nunca expone catálogos inactivos; la activación/desactivación se gestiona en `data.sql`. Simplifica la lectura y evita filtros extra en cada controller.
- **Seed con `ON CONFLICT DO UPDATE`** — `data.sql` se re-ejecuta en cada inicio (ver `docs/contexto/decisiones.md`); la cláusula `ON CONFLICT` lo hace idempotente. Alternativa (migraciones Flyway) descartada por la decisión de `ddl-auto: update` del proyecto.
- **Sin cache** — los catálogos son pequeños y de baja frecuencia de cambio; el acceso directo es suficiente. Alternativa (Caffeine `@Cacheable`) documentada en `docs/contexto/decisiones.md` pero no aplicada aquí.
- **Consulta filtrada por orden** — los repositorios JPA usan `findByActiveTrueOrderBy<Campo>` para devolver el orden natural del seed (p. ej. niveles de idioma A1→C2).

## Riesgos

- **`data.sql` con IDs hardcodeados** — los catálogos dependen de IDs estables para que las referencias de los candidatos coincidan. Mitigación: `ON CONFLICT DO UPDATE` y revisión al agregar catálogos (backlog).
- **Divergencia entre entidad y seed** — si un catálogo se agrega a `data.sql` sin su entidad/repositorio (o viceversa) el endpoint no lo verá. Mitigación: los 5 catálogos siguen el mismo patrón y se cubren con tests por controller.
