# Deuda Técnica — ats-postulant

## CRÍTICA

### C1. `build.gradle` — Versión de Spring Boot inexistente (4.0.5)
- **Archivo:** `build.gradle:3`
- **Problema:** `springBootVersion = '4.0.5'` no existe en el repositorio de Spring. La versión más reciente estable es 3.x.
- **Impacto:** El proyecto no compila. Error de resolución de dependencias.
- **Solución:** Cambiar a `3.4.4` (o la última estable 3.x compatible con Java 21).

### C2. `build.gradle` — Dependencia `spring-boot-starter-webmvc` inexistente
- **Archivo:** `build.gradle:38`
- **Problema:** El starter correcto es `spring-boot-starter-web`, no `spring-boot-starter-webmvc`.
- **Impacto:** El proyecto no compila. Error de dependencia no encontrada.
- **Solución:** Reemplazar con `org.springframework.boot:spring-boot-starter-web`.

### C3. `build.gradle` — Dependencia `spring-boot-starter-actuator-test` inexistente
- **Archivo:** `build.gradle:49`
- **Problema:** No existe un starter `actuator-test`. Debe usarse `spring-boot-starter-test`.
- **Impacto:** Los tests de integración fallan al no resolver la dependencia.
- **Solución:** Reemplazar con `spring-boot-starter-test`.

### C4. `build.gradle` — `spring-boot-starter-oauth2-resource-server` duplicado
- **Archivo:** `build.gradle:34 y 36`
- **Problema:** La dependencia aparece declarada dos veces.
- **Impacto:** Innecesario, aunque no causa error, es redundante y confuso.
- **Solución:** Eliminar la línea duplicada (línea 36).

### C5. `build.gradle` — Faltan starters de test de seguridad, validación y webmvc
- **Archivos:** `build.gradle:51-53`
- **Problema:** `spring-boot-starter-security-test` no existe. `spring-boot-starter-validation-test` y `spring-boot-starter-webmvc-test` tampoco.
- **Impacto:** Los tests no compilan.
- **Solución:** Usar `spring-boot-starter-test` que ya incluye lo necesario.

---

## MEDIA

### M1. `CandidatePersistenceMapper` ignora todas las colecciones del dominio
- **Archivo:** `infrastructure/out/mapper/CandidatePersistenceMapper.java:14-26`
- **Problema:** Las 13 colecciones (`professionalProfile`, `experiences`, `educations`, `certifications`, `notes`, `languages`, `softSkills`, `hardSkills`, `attachments`, `states`, `availabilities`, `parseResults`, `auditEvents`) se mapean con `ignore = true` en `toDomain()`. Esto significa que al leer un `Candidate` desde BD, nunca se cargan sus colecciones. La capa de servicio debe llamar manualmente a `loadDetails()` (ver `CandidateService.java:303-321`) que hace 10 consultas adicionales a BD.
- **Impacto:** N+1 queries severo. Cada vez que se consulta un candidato se disparan ~11 queries. Escalabilidad pésima.
- **Solución:** Implementar mapeo real de colecciónes en el mapper o usar `fetch join`/`EntityGraph` en el repositorio JPA.

### M2. `open-in-view` habilitado por defecto
- **Archivo:** `application.yaml`
- **Problema:** `spring.jpa.open-in-view` no está configurado, por lo que Spring Boot lo activa por defecto (`true`). Esto mantiene la sesión de Hibernate abierta durante toda la petición HTTP, permitiendo lazy loading en la vista pero con riesgo de LazyInitializationException y performance degradado.
- **Impacto:** Performance degradado, conexiones BD retenidas más de lo necesario, riesgo de errores en production.
- **Solución:** Agregar `spring.jpa.open-in-view: false` y asegurar que todas las colecciones se carguen dentro de la transacción.

### M3. `data.sql` con IDs hardcodeados para catálogos
- **Archivo:** `src/main/resources/data.sql`
- **Problema:** Los INSERTs usan IDs numéricos fijos (1, 2, 3...) con cláusula `ON CONFLICT`. Si otro microservicio inserta registros, habrá conflictos de IDs. Además, no se usa una secuencia/auto-increment para estos datos maestros.
- **Impacto:** Problemas de consistencia si múltiples servicios comparten BD o si se reinicia la secuencia.
- **Solución:** Migrar a `flyway`/`liquibase` para manejo de migraciones, usar claves naturales (ISO code, name) como PK en catálogos.

### M4. `SecurityConfig` — Algoritmo HMAC inconsistente
- **Archivo:** `infrastructure/config/SecurityConfig.java:78-80`
- **Problema:** Se crea `SecretKeySpec` con algoritmo `HmacSHA256` pero se configura el decoder con `MacAlgorithm.HS384`. Son algoritmos incompatibles.
- **Impacto:** Los JWTs firmados con HS256 no pueden ser decodificados con HS384 y viceversa. Autenticación falla.
- **Solución:** Usar `MacAlgorithm.HS256` o cambiar `SecretKeySpec` a `HmacSHA384`.

### M5. `CandidateAvailability` solo tiene campo `id`
- **Archivo:** `domain/model/CandidateAvailability.java`
- **Problema:** El modelo de dominio `CandidateAvailability` solo contiene `Long id`. La entidad JPA `CandidateAvailabilityEntity` sí tiene campos adicionales (`availability`, `availableFrom`, `createdAt`), pero el mapper ignora el mapeo.
- **Impacto:** La disponibilidad del candidato nunca se persiste/consulta correctamente desde el dominio. Funcionalidad incompleta.
- **Solución:** Completar el modelo de dominio y mappers para `CandidateAvailability`.

### M6. `CandidateExperiencePersistenceMapper` ignora `descriptions` y `technologies`
- **Archivo:** `infrastructure/out/mapper/CandidateExperiencePersistenceMapper.java:10-11`
- **Problema:** `descriptions` y `technologies` se ignoran en `toDomain()`. Esto significa que al leer experiencias desde BD, se pierden las descripciones detalladas y las tecnologías asociadas.
- **Impacto:** Datos incompletos al consultar experiencias de un candidato.
- **Solución:** Implementar mapeo correcto de sub-colecciones.

### M7. `CandidateWebMapper` ignora `experiences` y `notes` en `toDomain(CreateCandidateRequest)`
- **Archivo:** `infrastructure/in/web/mapper/CandidateWebMapper.java:44-46`
- **Problema:** Al crear un candidato vía API REST, las experiencias y notas no se mapean desde el request.
- **Impacto:** No se puede crear un candidato con experiencias/notas iniciales en una sola llamada API.
- **Solución:** Implementar mapeo de estas colecciones.

### M8. `AttachmentService` mezcla lógica de parseo con subida de archivos
- **Archivo:** `application/service/AttachmentService.java:67-97`
- **Problema:** El método `uploadCv()` llama directamente a `parse()` dentro del mismo método. La responsabilidad de subir un CV y de parsearlo están acopladas. Si el parseo falla, se marca como fallido pero no se puede reintentar fácilmente.
- **Impacto:** Violación del principio de responsabilidad única. No se puede subir un archivo sin parsearlo inmediatamente.
- **Solución:** Separar upload y parse en dos métodos públicos. El parseo debe ser invocable de forma asíncrona y desacoplada.

### M9. Faltan `@ResponseStatus` en excepciones de dominio
- **Archivo:** `domain/exception/` (8 clases de excepción)
- **Problema:** Ninguna excepción de dominio tiene la anotación `@ResponseStatus`. Todas se manejan centralizadamente en `GlobalExceptionHandler` con `@ExceptionHandler`, lo cual es correcto pero no hay consistencia. Algunas excepciones no tienen handler (ej: `IllegalArgumentException`).
- **Impacto:** Si se lanza una excepción sin handler específico, se devuelve un error 500 genérico.
- **Solución:** Agregar `@ResponseStatus` a cada excepción como fallback, y mantener el `GlobalExceptionHandler` para mensajes personalizados.

### M10. Método `update()` en `CandidateService` borra y recrea colecciones
- **Archivo:** `application/service/CandidateService.java:138-146`
- **Problema:** Al actualizar un candidato se borran (`deleteAllByCandidateId`) y se vuelven a insertar todas las colecciones (educación, idiomas, skills, etc.). Esto es ineficiente y genera IDs nuevos para cada registro.
- **Impacto:** Se pierde el historial de IDs. Las relaciones externas que referencien estos IDs se rompen. Performance subóptimo.
- **Solución:** Implementar upsert/lógica de merge por ID.

---

## BAJA

### B1. `JwtAuthorityExtractor` existe pero no se usa
- **Archivo:** `infrastructure/config/JwtAuthorityExtractor.java`
- **Problema:** La clase está definida pero `SecurityConfig` implementa su propia lógica de extracción inline (`extractAuthorities`). La clase es código muerto.
- **Impacto:** Código muerto que aumenta la complejidad sin beneficio.
- **Solución:** Eliminar la clase o integrarla en `SecurityConfig`.

### B2. Puerto 5434 no estándar para PostgreSQL
- **Archivo:** `application.yaml:7`, `docker-compose.yml:10`
- **Problema:** Se usa el puerto `5434` en lugar del estándar `5432`.
- **Impacto:** Bajo, pero puede causar confusión en desarrolladores nuevos.
- **Solución:** Usar puerto estándar `5432` o documentar la razón.

### B3. `spring.jpa.show-sql: true` en producción
- **Archivo:** `application.yaml:16`
- **Problema:** `show-sql: true` expone las consultas SQL en los logs. En producción esto es riesgo de seguridad y degradación de performance.
- **Impacto:** Riesgo bajo de seguridad, logs excesivos.
- **Solución:** Mover a un perfil `dev` o `debug`.

### B4. Falta validación de tamaño máximo de archivo adjunto
- **Archivo:** `application/service/AttachmentService.java`
- **Problema:** No hay límite en el tamaño del archivo PDF que se puede subir. Un usuario malicioso podría saturar el almacenamiento.
- **Impacto:** Riesgo de denial of service por almacenamiento.
- **Solución:** Agregar `spring.servlet.multipart.max-file-size` en configuración y validación explícita.

### B5. `@Transactional` mezclado entre `javax` y `jakarta`
- **Archivo:** `application/service/CandidateService.java:30` usa `jakarta.transaction.Transactional`
- **Archivo:** `application/service/AttachmentService.java:15` usa `org.springframework.transaction.annotation.Transactional`
- **Problema:** Inconsistencia en la importación de `@Transactional`.
- **Impacto:** Bajo, ambas funcionan pero es mala práctica.
- **Solución:** Unificar usando `org.springframework.transaction.annotation.Transactional`.

### B6. Servicios de catálogo anémicos (boilerplate repetitivo)
- **Archivos:** `CountryCodeService`, `EducationLevelService`, `ExperienceRangeService`, `LanguageService`, `LanguageLevelService`
- **Problema:** 5 servicios casi idénticos que solo delegan a su respectivo puerto. Los controladores también son casi idénticos.
- **Impacto:** Código duplicado, mantenimiento más costoso.
- **Solución:** Crear un servicio genérico `CatalogService<T>` o usar un enfoque de reflectación.

### B7. `JwtProperties` sin usar
- **Archivo:** `infrastructure/config/JwtProperties.java`
- **Problema:** La clase `@ConfigurationProperties` existe pero `SecurityConfig` inyecta el secret via `@Value` en lugar de usar las properties.
- **Impacto:** Código muerto.
- **Solución:** Integrar `JwtProperties` en `SecurityConfig` o eliminar la clase.

### B8. Endpoints de catálogo sin autenticación
- **Archivo:** `SecurityConfig.java:54` — `.anyRequest().permitAll()`
- **Problema:** Todos los endpoints no listados explícitamente (incluyendo catálogos: `/api/country-codes`, `/api/languages`, etc.) son de acceso público sin autenticación.
- **Impacto:** Datos maestros expuestos sin control. Bajo impacto porque son datos públicos, pero inconsistente con el diseño de seguridad.
- **Solución:** Configurar explícitamente el permiso para cada endpoint.

### B9. `Dockerfile` copia JAR de `build/libs/` pero el `build.gradle` usa Gradle
- **Archivo:** `Dockerfile:3`
- **Problema:** El Dockerfile copia `build/libs/*.jar`, que es la ruta de salida de Gradle. Es correcto, pero no hay un `docker-build` task configurado ni instrucciones de build.
- **Impacto:** Bajo, falta de documentación.
- **Solución:** Agregar un script `build-and-docker.sh` o task de Gradle.

### B10. `CandidateNote.candidateId` es `int` mientras `Candidate.candidateId` es `Long`
- **Archivo:** `application/service/CandidateService.java:192` — `note.setCandidateId(candidateId.intValue())`
- **Problema:** Inconsistencia de tipos entre `CandidateNote.candidateId` (int) y el `id` del candidato (Long).
- **Impacto:** Riesgo de desbordamiento si candidateId supera `Integer.MAX_VALUE`.
- **Solución:** Cambiar `CandidateNote.candidateId` a `Long`.
