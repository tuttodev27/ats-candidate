# HU-M16 — Optimizar consultas de validación de catálogos

**Como** desarrollador
**Quiero** cambiar las consultas de skills activos para que usen queries optimizadas en lugar de cargar todos los registros en memoria y filtrar con streams
**Para** mejorar el rendimiento con catálogos grandes.

### Criterios de aceptación

1. Agregar `List<String> findNamesByActiveTrue()` en `HardSkillJpaRepository`.
2. Agregar `List<String> findNamesByActiveTrue()` en `SoftSkillJpaRepository`.
3. Cambiar `CandidateCatalogValidationAdapter.getActiveHardSkillNames()` para usar la nueva query.
4. Cambiar `CandidateCatalogValidationAdapter.getActiveSoftSkillNames()` para usar la nueva query.
5. Las queries se ejecutan a nivel de base de datos, no en memoria.
6. Tests del adapter.

**Archivos:** `infrastructure/out/adapter/CandidateCatalogValidationAdapter.java`, `HardSkillJpaRepository.java`, `SoftSkillJpaRepository.java`
