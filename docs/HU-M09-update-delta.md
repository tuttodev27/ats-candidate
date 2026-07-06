# HU-M09 — Refactorizar update para no eliminar y re-insertar sub-entidades

**Como** desarrollador
**Quiero** que el update de candidato haga un delta (insert/new, update/existing, delete/removed) en lugar de borrar todo y re-insertar
**Para** preservar los IDs de las sub-entidades y evitar operaciones innecesarias en DB.

### Criterios de aceptación

1. Identificar entidades agregadas (sin ID), modificadas (con ID existente) y eliminadas (en DB pero no en request).
2. Solo persistir los cambios necesarios: INSERT para nuevas, UPDATE para existentes, DELETE para removidas.
3. Los IDs de sub-entidades existentes se mantienen estables entre actualizaciones.
4. El delete-and-reinsert actual se reemplaza completamente.
5. Tests de update con modificación parcial (cambiar un campo de una educación sin tocar las demás).

**Archivo:** `application/service/CandidateService.java`
