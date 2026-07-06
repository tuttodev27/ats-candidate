# HUs de Mejora — Análisis integral del proyecto

## 🔴 Prioridad crítica

---

### HU-M01 — Proteger endpoints con `anyRequest().authenticated()` en SecurityConfig

**Como** equipo de seguridad
**Quiero** reemplazar `.anyRequest().permitAll()` por `.anyRequest().authenticated()`
**Para** que todos los endpoints no matcheados explícitamente requieran autenticación, evitando acceso público inadvertido.

**Criterios:**
- Se reemplaza `.anyRequest().permitAll()` por `.anyRequest().authenticated()` en `SecurityConfig`
- Se agregan matchers explícitos para endpoints públicos (catálogos, health, swagger)
- Se agrega matcher para `POST /api/candidates/*/attachments/*/parse` con `RECRUITER_WRITE`
- Todos los endpoints catalog (country-codes, education-levels, etc.) se marcan como públicos explícitamente si es necesario
- Tests que verifiquen que endpoints sin token回应 401

**Archivo:** `infrastructure/config/SecurityConfig.java`

---

### HU-M02 — Corregir CORS: eliminar allowCredentials(true) con allowedOriginPatterns("*")

**Como** equipo de seguridad
**Quiero** restringir la configuración CORS para no permitir credenciales con orígenes comodín
**Para** evitar vulnerabilidades de CSRF con orígenes no confiables.

**Criterios:**
- Se elimina `setAllowCredentials(true)` si se usa `setAllowedOriginPatterns(List.of("*"))`
- O se listan explícitamente los orígenes permitidos en producción
- Se mantiene compatibilidad con entornos de desarrollo

**Archivo:** `infrastructure/config/SecurityConfig.java`

---

### HU-M03 — Corregir dependencias de build.gradle

**Como** desarrollador
**Quiero** corregir las dependencias inválidas en build.gradle
**Para** que el proyecto compile sin errores y los tests funcionen correctamente.

**Criterios:**
- Reemplazar `spring-boot-starter-webmvc` por `spring-boot-starter-web`
- Reemplazar los starters de test inválidos (`*-actuator-test`, `*-data-jpa-test`, `*-validation-test`, `*-webmvc-test`) por `spring-boot-starter-test`
- Eliminar la dependencia duplicada de `spring-boot-starter-oauth2-resource-server`
- Agregar `spring-boot-starter-webflux` si se requiere para integración con LLM (HU-59)
- Verificar que `./gradlew build` pase sin errores

**Archivos:** `build.gradle`

---

## 🟡 Prioridad alta

---

### HU-M04 — Agregar experiences y notes al CreateCandidateRequest

**Como** recruiter
**Quiero** poder crear un candidato con experiencias laborales y notas incluidas en el POST inicial
**Para** no tener que crearlo primero y luego actualizarlo con PUT.

**Criterios:**
- `CreateCandidateRequest` incluye campos `Set<CandidateExperienceRequest> experiences` y `Set<CandidateNoteRequest> notes`
- El mapper `CandidateWebMapper.toDomain(CreateCandidateRequest)` mapea estos campos (quitar `ignore = true`)
- `CandidateService.create()` persiste experiences y notes
- Tests que verifiquen creación con experiences y notes

**Archivos:** `infrastructure/in/web/dto/CreateCandidateRequest.java`, `infrastructure/in/web/mapper/CandidateWebMapper.java`, `application/service/CandidateService.java`

---

### HU-M05 — Exponer parseError y parsedAt en AttachmentResponse

**Como** recruiter
**Quiero** ver el error de parseo y la fecha en que se intentó parsear un CV
**Para** diagnosticar por qué falló la extracción de datos.

**Criterios:**
- `AttachmentResponse` incluye campos `LocalDateTime parsedAt` y `String parseError`
- El mapper `CandidateWebMapper.toResponse(Attachment)` mapea estos campos
- `parseError` solo se incluye si no es null (info sensible)
- Tests de mapper

**Archivos:** `infrastructure/in/web/dto/AttachmentResponse.java`, `infrastructure/in/web/mapper/CandidateWebMapper.java`

---

### HU-M06 — Implementar persistencia para CandidateExperienceDescription y CandidateExperienceTechnology

**Como** desarrollador
**Quiero** completar el stack de persistencia para descriptions y technologies de experiencia
**Para** que los datos de experiencia laboral se guarden y recuperen correctamente.

**Criterios:**
- Crear `CandidateExperienceDescriptionRepositoryPort` y `CandidateExperienceTechnologyRepositoryPort`
- Crear `CandidateExperienceDescriptionJpaRepository` y `CandidateExperienceTechnologyJpaRepository`
- Crear adapters correspondientes
- Crear persistence mappers (o extender el existente)
- Quitar `@Mapping(target = "descriptions", ignore = true)` y `@Mapping(target = "technologies", ignore = true)` de `CandidateExperiencePersistenceMapper`
- Agregar `CandidateExperienceDescriptionRequest` y `CandidateExperienceTechnologyRequest` DTOs
- Agregar campos en `CandidateExperienceRequest`
- Tests de persistencia

**Archivos:** Nuevos repos, adapters, mappers + modificar `CandidateExperiencePersistenceMapper.java`, `CandidateExperienceRequest.java`

---

### HU-M07 — Optimizar loadDetails() para evitar N+1 queries

**Como** desarrollador
**Quiero** reducir las 8 consultas individuales en `loadDetails()` a una estrategia más eficiente
**Para** mejorar el rendimiento del listado de candidatos.

**Criterios:**
- Evaluar dos enfoques: (a) `@EntityGraph` en `CandidateEntity` o consultas con JOIN FETCH, (b) caché en memoria para catálogos
- Implementar la solución que mejor se adapte a la arquitectura actual
- El listado (`GET /api/candidates`) no debe degradarse con la cantidad de candidates
- Tests de rendimiento o verificación de queries

**Archivos:** `application/service/CandidateService.java`, entidades y repositorios relacionados

---

### HU-M08 — Agregar handler global para IllegalArgumentException e IllegalStateException

**Como** API
**Quiero** que las excepciones `IllegalArgumentException` e `IllegalStateException` tengan una respuesta estructurada
**Para** que el frontend reciba errores consistentes.

**Criterios:**
- Agregar `@ExceptionHandler(IllegalArgumentException.class)` en `GlobalExceptionHandler` → 400 Bad Request
- Agregar `@ExceptionHandler(IllegalStateException.class)` → 500 Internal Server Error
- Mensaje descriptivo y código de error apropiado
- Tests del handler

**Archivos:** `infrastructure/in/web/exception/GlobalExceptionHandler.java`

---

## 🔵 Prioridad media

---

### HU-M09 — Refactorizar update para no eliminar y re-insertar sub-entidades

**Como** desarrollador
**Quiero** que el update de candidato haga un delta (insert/new, update/existing, delete/removed) en lugar de borrar todo y re-insertar
**Para** preservar los IDs de las sub-entidades y evitar operaciones innecesarias en DB.

**Criterios:**
- Identificar las entidades agregadas, modificadas y eliminadas comparando el estado actual vs el entrante
- Solo persistir los cambios necesarios
- Los IDs de sub-entidades existentes se mantienen estables
- Tests de update con modificación parcial

**Archivos:** `application/service/CandidateService.java`

---

### HU-M10 — Eliminar JwtAuthorityExtractor (código muerto)

**Como** desarrollador
**Quiero** eliminar la clase `JwtAuthorityExtractor` que no se utiliza
**Para** mantener el códigobase limpio y sin confusión.

**Criterios:**
- Verificar que no hay referencias a `JwtAuthorityExtractor` en ningún archivo
- Eliminar el archivo
- Compilar sin errores

**Archivos:** Eliminar `infrastructure/config/JwtAuthorityExtractor.java`

---

### HU-M11 — Centralizar extractRecruiterId() en un helper común

**Como** desarrollador
**Quiero** extraer el método `extractRecruiterId()` duplicado en `CandidateController` y `AttachmentController` a una clase compartida
**Para** evitar duplicación de lógica.

**Criterios:**
- Crear un utility class o un `@Component` con el método estático
- Reemplazar las implementaciones duplicadas por llamadas al helper
- Mantener la misma lógica de extracción de claims
- Tests del helper

**Archivos:** Nuevo helper + `CandidateController.java`, `AttachmentController.java`

---

### HU-M12 — Corregir type mismatch de CandidateNote (Integer/Long, String/LocalDateTime)

**Como** desarrollador
**Quiero** unificar los tipos de `CandidateNote` entre dominio y entidad
**Para** eliminar conversiones forzadas frágiles en el mapper.

**Criterios:**
- Cambiar `candidateId` de `Integer` a `Long` en `CandidateNote` domain model
- Cambiar `createdBy` de `String` a `Long` en `CandidateNote` domain model
- Cambiar `createdAt` de `String` a `LocalDateTime` en `CandidateNote` domain model
- Actualizar el persistence mapper y el web mapper
- Actualizar usos en `CandidateService.prepareNote()`
- Tests de integración

**Archivos:** `domain/model/CandidateNote.java`, persistence mapper, web mapper, `CandidateService.java`

---

### HU-M13 — Agregar @Builder a entidades JPA faltantes

**Como** desarrollador
**Quiero** agregar `@Builder` a todas las entidades JPA que no lo tienen
**Para** mantener consistencia con `CandidateEntity` y facilitar la creación de instancias en tests.

**Criterios:**
- `@Builder` no interfiere con JPA (requiere `@NoArgsConstructor` y `@AllArgsConstructor` que ya existen)
- Afecta a: `AttachmentEntity`, `CandidateEducationEntity`, `CandidateExperienceEntity`, `CandidateHardSkillEntity`, `CandidateSoftSkillEntity`, `CandidateLanguageEntity`, `CandidateNoteEntity`, `CandidateProfessionalProfileEntity`, `CandidateStateEntity`, `CandidateParseResultEntity` y entidades de catálogo
- Compilar sin errores

**Archivos:** Todas las entidades JPA

---

### HU-M14 — Agregar @Transactional(readOnly = true) en loadDetails y consultas

**Como** desarrollador
**Quiero** marcar los métodos de solo lectura con `@Transactional(readOnly = true)`
**Para** optimizar las conexiones a DB y evitar locks innecesarios.

**Criterios:**
- `CandidateService.getById()`, `list()`, `loadDetails()` deben tener `readOnly = true`
- No modificar métodos de escritura
- Verificar que no hay operaciones de escritura dentro de transacciones readOnly
- Tests

**Archivos:** `application/service/CandidateService.java`

---

### HU-M15 — Configurar límite de tamaño de archivos multipart

**Como** sistema
**Quiero** configurar un límite máximo para la subida de archivos CV
**Para** evitar abusos y problemas de memoria.

**Criterios:**
- Agregar `spring.servlet.multipart.max-file-size` y `spring.servlet.multipart.max-request-size` en `application.yaml`
- Valor sugerido: 10MB para archivo, 12MB para request
- Validar que archivos mayores devuelvan error apropiado
- Documentar el límite en OpenAPI

**Archivos:** `src/main/resources/application.yaml`

---

### HU-M16 — Optimizar CandidateCatalogValidationAdapter: evitar carga completa en memoria

**Como** desarrollador
**Quiero** cambiar las consultas de skills activos para que usen una query optimizada en lugar de cargar todos los registros en memoria y filtrar con streams
**Para** mejorar el rendimiento con catálogos grandes.

**Criterios:**
- Agregar métodos en `HardSkillJpaRepository` y `SoftSkillJpaRepository` como `findAllActiveNames()` que retornen solo nombres
- Cambiar `CandidateCatalogValidationAdapter` para usar estas queries
- Mantener la misma interfaz del port

**Archivos:** `infrastructure/out/adapter/CandidateCatalogValidationAdapter.java`, `HardSkillJpaRepository.java`, `SoftSkillJpaRepository.java`

---

### HU-M17 — Agregar spring.jpa.open-in-view explícito

**Como** desarrollador
**Quiero** configurar explícitamente `spring.jpa.open-in-view: false` en lugar de dejar el valor por defecto (true)
**Para** evitar LazyInitializationException y forzar decisiones conscientes sobre carga de relaciones.

**Criterios:**
- Agregar `spring.jpa.open-in-view: false` en `application.yaml`
- Identificar y resolver cualquier LazyInitializationException que surja
- Verificar que todos los datos necesarios se carguen dentro de la transacción

**Archivos:** `src/main/resources/application.yaml`

---

### HU-M18 — Habilitar sub-recursos del candidato con endpoints dedicados

**Como** recruiter
**Quiero** poder agregar, modificar y eliminar estudios, idiomas, habilidades y experiencias de forma individual
**Para** no tener que enviar la ficha completa en cada cambio.

**Criterios:**
- `POST /api/candidates/{id}/educations` — agregar estudio
- `PUT /api/candidates/{id}/educations/{eduId}` — modificar estudio
- `DELETE /api/candidates/{id}/educations/{eduId}` — eliminar estudio
- Ídem para languages, hard-skills, soft-skills, experiences, notes
- Cada endpoint valida que el candidato exista
- Tests

**Archivos:** Nuevos endpoints en `CandidateController.java` o nuevos controllers
