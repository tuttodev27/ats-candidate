# ATS Candidate

Microservicio de gestión de postulantes (candidatos) para el sistema ATS-worksync: perfiles, CVs, archivos adjuntos y parseo automático de CVs con IA (Ollama).

## Stack

- **Lenguaje:** Java 21
- **Framework / runtime:** Spring Boot 4.0.2, Gradle Wrapper (Gradle 9.4)
- **Base de datos:** PostgreSQL 16 (puerto 5434, base `ats_candidate`), Spring Data JPA/Hibernate, seed con `data.sql` (sin migraciones Flyway versionadas)
- **Tests:** JUnit 5 + Spring Boot Test, H2 en modo PostgreSQL, profile `test`
- **Otros:** MapStruct 1.6.3, Lombok, JJWT 0.12.6, Spring Security (OAuth2 resource server), Apache PDFBox 3.0.3, SpringDoc OpenAPI, Eureka Client, Ollama (parseo de CVs con IA local)

## Comandos

- `./gradlew bootRun` — arranca el servidor en local (puerto 8084)
- `./gradlew test` — ejecuta todos los tests (deben pasar antes de cada commit)
- `./gradlew test --tests "com.ats.candidate.XxxTest.method"` — ejecuta un test puntual
- `./gradlew build -x test` — compila para producción sin correr tests
- `./gradlew check` — build completo con tests
- No hay tarea de lint (Checkstyle/SpotBugs no están configurados)

## Estructura del proyecto

- `src/main/java/com/ats/candidate/domain/` — modelos de dominio, puertos (`port/in`, `port/out`), excepciones. No depende de `application` ni `infrastructure`.
- `src/main/java/com/ats/candidate/application/` — casos de uso (implementaciones de puertos de entrada) y parsing de CVs.
- `src/main/java/com/ats/candidate/infrastructure/` — adaptadores: `in/web` (controllers, DTOs, mappers, manejador global de excepciones), `out` (entidades JPA, repositorios, mappers, adapters, storage, Ollama), `config`.
- `src/test/java/com/ats/candidate/` — tests, espejo de la estructura main.
- `docs/contexto/` — arquitectura, convenciones, flujo de trabajo, glosario, decisiones y errores conocidos.
- `constitution/` — misión, roadmap y tech stack/convenciones (reglas estables del proyecto).
- `feature/` — una carpeta por feature numerada (`001-gestion-candidatos`, `002-catalogos`, `003-adjuntos`, `004-parsing-cv`), cada una con `spec.md`, `plan.md`, `tasks.md`.
- `src/main/resources/` — `application.yaml` y `data.sql` (carga de catálogos con `ON CONFLICT DO UPDATE`).

## Convenciones

- **Arquitectura hexagonal:** `domain/` no importa nada de `infrastructure/` ni `application/`; `application/` importa `domain/`; `infrastructure/` importa `domain/` y `application/`.
- **Paquete base:** `com.ats.candidate`.
- **Nombrado:** modelos de dominio sustantivos simples (`Candidate`); entidades JPA `XxxEntity`; repositorios `XxxJpaRepository`; adaptadores `XxxRepositoryAdapter`; mappers `XxxPersistenceMapper`/`XxxWebMapper`; DTOs `XxxRequest`/`XxxResponse`; puertos de entrada `XxxUseCase`; puertos de salida `XxxRepositoryPort`.
- **Inyección de dependencias:** por constructor (no `@Autowired` en campos).
- **Boilerplate:** Lombok obligatorio (`@Getter`, `@Setter`, `@Builder`, `@FieldDefaults`).
- **Mapeo:** MapStruct con `componentModel = "spring"`; un mapper por agregado.
- **Código sin comentarios inline:** el código debe ser autoexplicativo.
- **Validación:** validar toda entrada del usuario con anotaciones `jakarta.validation` en los DTOs.
- **Errores:** respuestas HTTP JSON centralizadas en `GlobalExceptionHandler`.
- **Tests:** ubicación espejo de main; profile `test` sin security; agrega o actualiza tests del código que cambies.
- **PR y commits:** formato de título `[ats-candidate] <mensaje>`; siempre corre `./gradlew check` antes de commitear.
- **/spec/** documentación para Spec driven development: ´constitution/´ (mision, tech,)

## No hagas

- No importes clases de otro microservicio — la comunicación entre servicios va por HTTP/eventos.
- No inviertas dependencias de capas (p. ej. `domain/` no debe usar clases de `infrastructure/`).
- No subas archivos `.env*` ni secrets (`JWT_SECRET` va por variable de entorno).
- No uses `@Autowired` en campos; inyecta por constructor.
- No agregues comentarios inline en el código.
- No instales dependencias o cambies versiones del stack sin avisar.

## Flujo de trabajo

- Antes de una tarea no trivial, propón un plan y espera mi OK.
- Una tarea a la vez; al terminar, dime qué cambiaste para que lo revise.
- Si no estás seguro al 80%, pregunta. No inventes.
- Toda feature nueva se crea como `feature/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código; `constitution/` manda y ningún plan de feature debe contradecirla.
- Al completar una feature, muévela a "Hecho" en `constitution/roadmap.md`.

## Documentación

- `README.md` — descripción, stack, inicio rápido y documentación de la API (Swagger/OpenAPI).
- `docs/contexto/arquitectura.md` — arquitectura del servicio.
- `docs/contexto/convenciones.md` — convenciones de código, BD, tests, estilo y git.
- `docs/contexto/flujo-de-trabajo.md` — comandos, variables de entorno y flujo del ciclo de un CV.
- `docs/contexto/glosario.md` — terminología del dominio.
- `docs/contexto/decisiones.md` — decisiones de diseño registradas.
- `docs/contexto/errores-conocidos.md` — errores y problemas conocidos.
- `constitution/mision.md` — misión y principios del proyecto.
- `constitution/roadmap.md` — estado de las features (hecho, siguiente, backlog).
- `constitution/tecnologia-y-convenciones.md` — tech stack y convenciones estables.
- `feature/` — specs, planes y tareas de cada feature.
