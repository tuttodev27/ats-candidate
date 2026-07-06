# HU-M06 — Implementar persistencia para descripciones y tecnologías de experiencia

**Como** desarrollador
**Quiero** completar el stack de persistencia para descriptions y technologies de experiencia
**Para** que los datos de experiencia laboral se guarden y recuperen correctamente.

### Criterios de aceptación

1. Crear `CandidateExperienceDescriptionRepositoryPort` y `CandidateExperienceTechnologyRepositoryPort`.
2. Crear `CandidateExperienceDescriptionJpaRepository` y `CandidateExperienceTechnologyJpaRepository`.
3. Crear adapters correspondientes.
4. Crear persistence mappers (o extender el existente).
5. Quitar `@Mapping(target = "descriptions", ignore = true)` y `@Mapping(target = "technologies", ignore = true)` de `CandidateExperiencePersistenceMapper`.
6. Agregar DTOs: `CandidateExperienceDescriptionRequest` y `CandidateExperienceTechnologyRequest`.
7. Agregar campos en `CandidateExperienceRequest` para recibir descripciones y tecnologías.
8. Tests de persistencia y API.

**Archivos:** Nuevos ports, repos, adapters, mappers + `CandidateExperiencePersistenceMapper.java`, `CandidateExperienceRequest.java`
