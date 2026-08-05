# Convenciones

## Código

- **Lenguaje:** Java 21.
- **Build:** Gradle 9.4 (Gradle Wrapper incluido).
- **Framework:** Spring Boot 4.0.2.
- **Paquete base:** `com.ats.candidate`.
- **Lombok:** Obligatorio para reducir boilerplate. Usar `@Getter`, `@Setter`, `@Builder`, `@FieldDefaults`.
- **MapStruct:** 1.6.3. Un mapper por agregado. Mappers web en `infrastructure/in/web/mapper/`. Mappers de persistencia en `infrastructure/out/mapper/`.
- **Arquitectura hexagonal:** `domain/` no importa nada de `infrastructure/` ni `application/`. `application/` importa `domain/`. `infrastructure/` importa `domain/` y `application/`.
- **Inyección de dependencias:** Por constructor (no `@Autowired` en campos).
- **DTOs:** Inmutables o con validación (`@NotBlank`, `@NotNull`, etc). En `infrastructure/in/web/dto/`.
- **Entidades JPA:** En `infrastructure/out/entity/`. Anotaciones `@Entity`, `@Table`, `@Id`, etc.
- **Repositorios JPA:** Extienden `JpaRepository`. En `infrastructure/out/repository/`.
- **Adaptadores:** Implementan puertos de salida (`domain/port/out/`). En `infrastructure/out/adapter/`.
- **Mapeo:** MapStruct con `componentModel = "spring"`.

## Base de datos

- **PostgreSQL 16**, base `ats_candidate`, puerto `5434`.
- **ddl-auto: update** (Hibernate genera el schema).
- **Seed data:** `data.sql` con `INSERT ... ON CONFLICT DO UPDATE`. Sin Flyway (aunque la dependencia flyway-core está declarada).
- **Sin migraciones versionadas.**

## Tests

- **JUnit 5 + Spring Boot Test.**
- **Base de datos H2** en modo PostgreSQL (`MODE=PostgreSQL`).
- **Profile:** `test` (usa `application-test.yaml`).
- **Ubicación:** `src/test/java/com/ats/candidate/`, espejo de la estructura main.
- **Sin security en tests:** `application-test.yaml` excluye `SecurityAutoConfiguration`.
- Cobertura actual: 14 archivos de test (7 controllers, 3 services, 1 web mapper, 1 repository JPA, 1 storage adapter, 1 application main).

## Estilo

- **Código sin comentarios** inline. El código debe ser autoexplicativo.
- **Respuestas HTTP:** JSON, con `GlobalExceptionHandler` para errores.
- **CORS:** Abierto (`allowedOriginPatterns: *`), métodos GET/POST/PUT/PATCH/DELETE/OPTIONS, credenciales permitidas.
- **Logs:** SLF4J + Logback.

## Git

- **Branch principal:** `develop`.
- **Convención de commits:** `[ats-candidate] <mensaje>`.
- **Remote:** `github.com/tuttodev27/ats-postulant`.
- **20+ commits** en el historial.

## Nombrado

- **Modelos de dominio:** sustantivos simples (`Candidate`, `Attachment`).
- **Entidades JPA:** mismo nombre + `Entity` (`CandidateEntity`, `AttachmentEntity`).
- **Repositorios JPA:** `XxxJpaRepository`.
- **Adaptadores (implementaciones de puertos):** `XxxRepositoryAdapter`.
- **Mappers de persistencia:** `XxxPersistenceMapper`.
- **Mappers web:** `XxxWebMapper`.
- **DTOs de request/response:** `XxxRequest`, `XxxResponse`.
- **Servicios:** `XxxService`.
- **Controladores:** `XxxController`.
- **Puertos de entrada (use cases):** `XxxUseCase`.
- **Puertos de salida (repository):** `XxxRepositoryPort`.
