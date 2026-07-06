# HU-M12 — Unificar tipos de CandidateNote entre dominio y entidad

**Como** desarrollador
**Quiero** unificar los tipos de `CandidateNote` entre dominio y entidad
**Para** eliminar conversiones forzadas frágiles en el mapper.

### Criterios de aceptación

1. Cambiar `candidateId` de `Integer` a `Long` en `CandidateNote` domain model.
2. Cambiar `createdBy` de `String` a `Long` en `CandidateNote` domain model.
3. Cambiar `createdAt` de `String` a `LocalDateTime` en `CandidateNote` domain model.
4. Actualizar `CandidateNotePersistenceMapper` (simplificar las expresiones complejas).
5. Actualizar `CandidateService.prepareNote()` (eliminar `intValue()`).
6. Actualizar `CreateCandidateNoteRequest` si es necesario.
7. Tests de integración.

**Archivos:** `domain/model/CandidateNote.java`, persistence mapper, `CandidateService.java`
