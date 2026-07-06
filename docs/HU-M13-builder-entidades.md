# HU-M13 — Agregar @Builder a entidades JPA faltantes

**Como** desarrollador
**Quiero** agregar `@Builder` a todas las entidades JPA que no lo tienen
**Para** mantener consistencia con `CandidateEntity` y facilitar la creación de instancias en tests.

### Criterios de aceptación

1. `@Builder` se agrega a: `AttachmentEntity`, `CandidateEducationEntity`, `CandidateExperienceEntity`, `CandidateHardSkillEntity`, `CandidateSoftSkillEntity`, `CandidateLanguageEntity`, `CandidateNoteEntity`, `CandidateProfessionalProfileEntity`, `CandidateStateEntity`, `CandidateParseResultEntity`.
2. `@Builder` se agrega a entidades de catálogo: `CountryCodeEntity`, `EducationLevelEntity`, `ExperienceRangeEntity`, `LanguageEntity`, `LanguageLevelEntity`, `HardSkillEntity`, `SoftSkillEntity`.
3. Cada entidad ya tiene `@NoArgsConstructor` y `@AllArgsConstructor` (requisito para que `@Builder` funcione con JPA).
4. El proyecto compila sin errores.
5. Tests existentes pasan.

**Archivos:** Todas las entidades JPA en `infrastructure/out/entity/`
