# HU-M07 — Optimizar loadDetails() para evitar N+1 queries

**Como** desarrollador
**Quiero** reducir las 8 consultas individuales en `loadDetails()` a una estrategia más eficiente
**Para** mejorar el rendimiento del listado de candidatos.

### Criterios de aceptación

1. Evaluar dos enfoques: (a) `@EntityGraph` en `CandidateEntity` o consultas con JOIN FETCH, (b) batch queries con `IN` clause.
2. Implementar la solución que mejor se adapte a la arquitectura actual (sin relaciones JPA).
3. El listado (`GET /api/candidates`) no debe hacer más de 3 consultas a DB por página.
4. El detalle (`GET /api/candidates/{id}`) no debe hacer más de 3 consultas.
5. Tests que verifiquen la cantidad de queries ejecutadas.

**Archivos:** `application/service/CandidateService.java`, entidades y repositorios relacionados
