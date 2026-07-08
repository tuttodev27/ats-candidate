# Historias de Usuario — Sprint de Deuda Técnica

---

## Sprint 1 — Correcciones Críticas (Prioridad Máxima)

### HU-001: Configurar dependencias correctas de Spring Boot

**Como** desarrollador del equipo ATS,
**Quiero** que el `build.gradle` tenga las dependencias correctas de Spring Boot 3.4.4
**Para que** el proyecto compile y pueda ejecutarse en desarrollo y producción.

**Criterios de Aceptación:**
- [ ] La versión de Spring Boot se cambia de `4.0.5` a `3.4.4`
- [ ] `spring-boot-starter-webmvc` se reemplaza por `spring-boot-starter-web`
- [ ] `spring-boot-starter-actuator-test` se reemplaza por `spring-boot-starter-test`
- [ ] `spring-boot-starter-oauth2-resource-server` duplicado se elimina
- [ ] `spring-boot-starter-security-test`, `spring-boot-starter-validation-test`, `spring-boot-starter-webmvc-test` se reemplazan por dependencias correctas (`spring-boot-starter-test`, `spring-boot-starter-security-test` real)
- [ ] `gradlew build` compila sin errores

**Tareas:**
- Actualizar `build.gradle`
- Ejecutar `./gradlew build` y verificar compilación
- Ejecutar `./gradlew test` y verificar tests pasan

**Estimación:** 3 story points
**Relación con deuda técnica:** C1, C2, C3, C4, C5

---

### HU-002: Corregir algoritmo HMAC en SecurityConfig

**Como** desarrollador del equipo ATS,
**Quiero** que la configuración de JWT use el mismo algoritmo HMAC en toda la cadena
**Para que** los tokens JWT se validen correctamente y la autenticación funcione.

**Criterios de Aceptación:**
- [ ] `SecretKeySpec` usa el mismo algoritmo que `MacAlgorithm` en el decoder
- [ ] La autenticación JWT funciona correctamente en ambiente local
- [ ] Los tests de seguridad pasan

**Tareas:**
- Alinear `HmacSHA256`/`HS256` o cambiar ambos a `HmacSHA384`/`HS384`
- Probar flujo completo de autenticación

**Estimación:** 2 story points
**Relación con deuda técnica:** M4

---

## Sprint 1 — Deuda Media

### HU-003: Optimizar carga de colecciones de Candidate (eliminar N+1 queries)

**Como** reclutador usando el sistema ATS,
**Quiero** que la consulta de un candidato cargue todos sus datos (perfil, educación, habilidades, experiencias, etc.) con el mínimo de consultas a BD
**Para que** la aplicación responda rápido incluso cuando haya muchos candidatos.

**Criterios de Aceptación:**
- [ ] `CandidatePersistenceMapper.toDomain()` mapea correctamente todas las colecciones
- [ ] Se implementa `JOIN FETCH` o `@EntityGraph` en el repositorio JPA para cargar colecciones en una sola consulta
- [ ] Se elimina el método `loadDetails()` que dispara ~11 queries por candidato
- [ ] La consulta de un candidato ejecuta máximo 3 queries (candidato + colecciones principales)
- [ ] La paginación de candidatos funciona sin errores (no usa `fetch` múltiple que rompa `Page`)

**Tareas:**
- Modificar `CandidatePersistenceMapper` para mapear colecciones
- Agregar `@EntityGraph` en `CandidateJpaRepository`
- Refactorizar `CandidateService.loadDetails()`
- Agregar `spring.jpa.open-in-view: false` en `application.yaml`

**Estimación:** 8 story points
**Relación con deuda técnica:** M1, M2

---

### HU-004: Separar upload y parseo de CVs

**Como** desarrollador del equipo ATS,
**Quiero** que la subida de un CV y su parseo sean operaciones independientes
**Para que** pueda reintentar el parseo si falla, sin tener que volver a subir el archivo.

**Criterios de Aceptación:**
- [ ] `AttachmentService.uploadCv()` solo sube el archivo y persiste el attachment, no parsea
- [ ] Existe un método público `parseAttachment(attachmentId)` que parsea un attachment existente
- [ ] Se puede invocar el parseo vía un endpoint REST o evento asíncrono
- [ ] Si el parseo falla, el attachment queda con `parseStatus = "FAILED"` y se puede reintentar
- [ ] Los tests unitarios cubren ambos flujos (upload y parse por separado)

**Tareas:**
- Refactorizar `AttachmentService` para separar upload y parse
- (Opcional) Crear endpoint `POST /api/candidates/{id}/attachments/{attachmentId}/parse`
- Agregar tests

**Estimación:** 5 story points
**Relación con deuda técnica:** M8

---

### HU-005: Implementar CandidateAvailability completo

**Como** reclutador usando el sistema ATS,
**Quiero** que la disponibilidad del candidato (fecha desde la que está disponible, tipo de disponibilidad) se guarde y consulte correctamente
**Para que** pueda evaluar si un candidato puede comenzar en las fechas requeridas.

**Criterios de Aceptación:**
- [ ] `CandidateAvailability` en dominio tiene todos los campos (`availability`, `availableFrom`, `createdAt`)
- [ ] El `CandidateAvailabilityPersistenceMapper` mapea todos los campos correctamente
- [ ] Al crear/actualizar un candidato se puede especificar su disponibilidad
- [ ] Al consultar un candidato se incluye la disponibilidad

**Tareas:**
- Completar `CandidateAvailability` en dominio
- Implementar mapper de persistencia
- Actualizar `CandidateWebMapper` para incluir disponibilidad en request/response
- Actualizar tests

**Estimación:** 3 story points
**Relación con deuda técnica:** M5

---

### HU-006: Mapear descripciones y tecnologías en CandidateExperience

**Como** reclutador usando el sistema ATS,
**Quiero** que al consultar la experiencia laboral de un candidato se incluyan las descripciones detalladas y las tecnologías usadas
**Para que** pueda evaluar adecuadamente su experiencia.

**Criterios de Aceptación:**
- [ ] `CandidateExperiencePersistenceMapper.toDomain()` mapea `descriptions` y `technologies`
- [ ] Al consultar un candidato vía API GET, las experiencias incluyen descripciones y tecnologías
- [ ] Los tests existentes se actualizan y pasan

**Tareas:**
- Implementar mapeo de `descriptions` y `technologies` en `CandidateExperiencePersistenceMapper`
- Verificar que `CandidateExperienceEntity` tiene las relaciones JPA correctas
- Actualizar tests

**Estimación:** 3 story points
**Relación con deuda técnica:** M6

---

### HU-007: Refactorizar `CandidateService.update()` para hacer upsert en lugar de delete+insert

**Como** desarrollador del equipo ATS,
**Quiero** que al actualizar un candidato no se borren y reinserten todas sus colecciones
**Para que** no se pierdan los IDs de registros existentes ni haya riesgo de romper relaciones externas.

**Criterios de Aceptación:**
- [ ] Al actualizar un candidato, cada colección se actualiza mediante upsert (merge por ID)
- [ ] Los registros existentes que no están en la nueva versión se eliminan
- [ ] Los registros nuevos se insertan normalmente
- [ ] Los IDs de las colecciones existentes se mantienen si no cambian

**Tareas:**
- Modificar `CandidateService.update()` para implementar merge lógico
- Agregar métodos en repositorios para buscar por ID
- Agregar tests

**Estimación:** 8 story points
**Relación con deuda técnica:** M10

---

### HU-008: Agregar `@ResponseStatus` a excepciones de dominio

**Como** desarrollador del equipo ATS,
**Quiero** que cada excepción de dominio tenga un `@ResponseStatus` por defecto
**Para que** si alguna excepción no es capturada por el `GlobalExceptionHandler`, se devuelva un HTTP status code adecuado.

**Criterios de Aceptación:**
- [ ] `CandidateNotFoundException` tiene `@ResponseStatus(HttpStatus.NOT_FOUND)`
- [ ] `EmailAlreadyExistException` tiene `@ResponseStatus(HttpStatus.CONFLICT)`
- [ ] `InvalidAttachmentException` tiene `@ResponseStatus(HttpStatus.BAD_REQUEST)`
- [ ] El resto de excepciones tienen su `@ResponseStatus` correspondiente
- [ ] Los tests verifican que el status code es correcto incluso sin el handler

**Tareas:**
- Agregar `@ResponseStatus` a cada clase de excepción en `domain/exception/`
- Actualizar tests

**Estimación:** 2 story points
**Relación con deuda técnica:** M9

---

## Sprint 1 — Deuda Baja

### HU-009: Limpiar código muerto

**Como** desarrollador del equipo ATS,
**Quiero** eliminar las clases y configuraciones que no se usan (`JwtAuthorityExtractor`, `JwtProperties`)
**Para que** el código sea más mantenible y tenga menos ruido.

**Criterios de Aceptación:**
- [ ] `JwtAuthorityExtractor.java` se elimina si no se usa
- [ ] `JwtProperties.java` se elimina o se integra en `SecurityConfig`
- [ ] El proyecto compila sin errores después de la limpieza

**Tareas:**
- Verificar referencias a ambas clases
- Eliminar archivos no utilizados
- Compilar para verificar

**Estimación:** 1 story point
**Relación con deuda técnica:** B1, B7

---

### HU-010: Desactivar SQL logging en producción

**Como** administrador del sistema ATS,
**Quiero** que las consultas SQL no se muestren en los logs de producción
**Para que** no se exponga información sensible de la base de datos.

**Criterios de Aceptación:**
- [ ] `spring.jpa.show-sql: true` se mueve a un perfil `dev` o se elimina
- [ ] Se crea perfil `application-dev.yaml` con configuraciones de desarrollo
- [ ] `application.yaml` solo contiene configuraciones seguras para producción

**Tareas:**
- Crear `application-dev.yaml`
- Mover `show-sql: true` y `format_sql: true` al perfil dev
- Documentar cómo activar el perfil dev

**Estimación:** 1 story point
**Relación con deuda técnica:** B3

---

### HU-011: Agregar límite de tamaño para subida de archivos

**Como** administrador del sistema ATS,
**Quiero** que la subida de archivos PDF tenga un tamaño máximo configurable
**Para que** no se sature el almacenamiento del servidor.

**Criterios de Aceptación:**
- [ ] Se configura `spring.servlet.multipart.max-file-size` en `application.yaml`
- [ ] Se valida explícitamente el tamaño antes de procesar el archivo
- [ ] Se devuelve un error 400 si el archivo excede el límite

**Tareas:**
- Agregar configuración en `application.yaml`
- Agregar validación en `AttachmentService.uploadCv()`
- Agregar test

**Estimación:** 1 story point
**Relación con deuda técnica:** B4

---

### HU-012: Unificar `@Transactional`

**Como** desarrollador del equipo ATS,
**Quiero** que todas las anotaciones `@Transactional` usen `org.springframework.transaction.annotation.Transactional`
**Para que** haya consistencia en el código.

**Criterios de Aceptación:**
- [ ] `CandidateService.java` usa `org.springframework.transaction.annotation.Transactional`
- [ ] No hay imports de `jakarta.transaction.Transactional`
- [ ] El proyecto compila y los tests pasan

**Tareas:**
- Cambiar import en `CandidateService.java`
- Verificar compilación y tests

**Estimación:** 1 story point
**Relación con deuda técnica:** B5

---

## Resumen del Sprint

| Historia | Descripción | Estimación | Prioridad |
|----------|-------------|-----------|-----------|
| HU-001 | Dependencias correctas de Spring Boot | 3 | Crítica |
| HU-002 | Corregir algoritmo HMAC | 2 | Crítica |
| HU-003 | Optimizar carga de colecciones (N+1) | 8 | Media |
| HU-004 | Separar upload y parseo de CVs | 5 | Media |
| HU-005 | CandidateAvailability completo | 3 | Media |
| HU-006 | Mapear descripciones y tecnologías | 3 | Media |
| HU-007 | Upsert en lugar de delete+insert | 8 | Media |
| HU-008 | @ResponseStatus en excepciones | 2 | Media |
| HU-009 | Limpiar código muerto | 1 | Baja |
| HU-010 | Desactivar SQL logging en producción | 1 | Baja |
| HU-011 | Límite de tamaño para archivos | 1 | Baja |
| HU-012 | Unificar @Transactional | 1 | Baja |
| **Total** | | **38** | |
