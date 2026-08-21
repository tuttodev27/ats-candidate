# 002 · Catálogos

**Estado:** implementado ✅

## Qué hace

Expone 5 catálogos de solo lectura que alimentan los formularios del frontend y validan las referencias de los candidatos: códigos de país, idiomas, niveles de idioma, niveles de estudios y rangos de experiencia. Todos devuelven solo los registros activos.

## Por qué

Los catálogos son datos de referencia compartidos: permiten al reclutador seleccionar valores normalizados (país, idioma, nivel educativo, rango de años) y garantizan integridad de datos al validar que las referencias del candidato existan y estén activas. Sin ellos, la ficha del candidato permitiría valores libres e inconsistentes.

## Criterios de aceptación

_Condiciones verificables que deben cumplirse para dar la feature por terminada. Redacta cada una de forma que se pueda comprobar con un sí/no. Marca `[x]` al cumplirse._

- [x] `GET /api/country-codes` devuelve los códigos telefónicos de país activos (seed: 13 países).
- [x] `GET /api/languages` devuelve los idiomas activos (seed: es, en, pt, fr, de, it).
- [x] `GET /api/language-levels` devuelve los niveles de idioma activos (A1–C2 + NATIVE).
- [x] `GET /api/education-levels` devuelve los niveles de estudios activos (6 niveles).
- [x] `GET /api/experience-ranges` devuelve los rangos de años de experiencia activos (6 rangos).
- [x] Solo se devuelven registros activos (`findActive()`), nunca los inactivos.
- [x] Los datos de catálogo se siembran con `data.sql` usando `INSERT ... ON CONFLICT DO UPDATE` (idempotente en cada arranque).
- [x] Requisito de calidad: cada controller tiene test happy-path (`CountryCodeControllerTest`, `LanguageControllerTest`, `LanguageLevelControllerTest`, `EducationLevelControllerTest`, `ExperienceRangeControllerTest`) y cobertura de delegación en `CatalogServiceTest`.

### HU-39 · Consultar códigos telefónicos de país

Como frontend, quiero consultar los códigos telefónicos de país activos para llenar el dropdown de código de país en la ficha del postulante.

Endpoint: `GET /api/country-codes`

Reglas derivadas del caso de uso:

- Con una consulta válida, el sistema responde **200 OK** con la lista de códigos de país activos.
- Solo se retornan registros con `active = true`; los inactivos no se exponen.
- La respuesta incluye por cada registro: `id`, `countryName`, `isoCode` y `phoneCode`.
- Los resultados vienen ordenados por `orderNumber` ascendente y luego por nombre de país.
- El catálogo es completo; no acepta paginación ni filtros.
- No requiere autenticación (catálogo de solo lectura).

### HU-40 · Consultar rangos de experiencia activos

Como frontend, quiero consultar los rangos de experiencia activos para llenar el dropdown de años de experiencia en la ficha del postulante.

Endpoint: `GET /api/experience-ranges`

Reglas derivadas del caso de uso:

- Con una consulta válida, el sistema responde **200 OK** con la lista de rangos activos.
- Solo se retornan registros con `active = true`; los inactivos no se exponen.
- La respuesta incluye por cada registro: `id`, `label`, `minYears` y `maxYears`.
- Los resultados vienen ordenados por `orderNumber` ascendente.
- El catálogo es completo; no acepta paginación ni filtros.
- No requiere autenticación (catálogo de solo lectura).

## Fuera de alcance

- CRUD de catálogos (alta/edición de valores): los catálogos se administran vía `data.sql`, no por API.
- Catálogos de solicitudes (empresas, búsquedas, títulos de cargo, proyectos): pertenecen al microservicio ats-request.
- Validación de referencias: implementada en `001-gestion-candidatos` vía `CandidateCatalogValidationPort` (no es parte de esta feature).
