# Tech stack y convenciones

_Cómo está construido el proyecto y las reglas que todo el código debe respetar. Es la referencia técnica que ningún plan de feature debería contradecir._

## Tecnologías

- **Lenguaje:** Java 21
- **Framework / runtime:** Spring Boot 4.0.2 + Spring Cloud Netflix Eureka 2025.1.1
- **Base de datos:** PostgreSQL 16 (puerto 5434, base `ats_candidate`) con Spring Data JPA/Hibernate; seed de catálogos con `data.sql`
- **Seguridad:** Spring Security (OAuth2 resource server) + JWT (JJWT 0.12.6)
- **Mapeo:** MapStruct 1.6.3 (con Lombok)
- **Parsing:** Apache PDFBox 3.0.3 + IA (Ollama)
- **API docs:** SpringDoc OpenAPI 2.8.13
- **Tests:** JUnit 5 + Spring Boot Test, H2 en modo PostgreSQL con profile `test`
- **Despliegue:** contenedor Docker (Dockerfile + docker-compose con el PostgreSQL local)

## Archivos / módulos clave

_Mapa breve de dónde vive cada cosa. Solo lo que un recién llegado necesita para orientarse._

- `src/main/java/com/ats/candidate/domain/` — modelos de dominio, puertos (`port/in`, `port/out`) y excepciones. No importa de `application/` ni `infrastructure/`.
- `src/main/java/com/ats/candidate/application/` — casos de uso (implementación de puertos de entrada) y parsing de CVs (`parser/`).
- `src/main/java/com/ats/candidate/infrastructure/in/web/` — controllers REST, DTOs, mappers de entrada y `GlobalExceptionHandler`.
- `src/main/java/com/ats/candidate/infrastructure/out/` — entidades JPA, repositorios, adaptadores, mappers, storage en disco y cliente Ollama.
- `src/main/java/com/ats/candidate/infrastructure/config/` — Security, JWT, Ollama y OpenAPI.
- `src/main/resources/` — `application.yaml` y `data.sql`.
- `docs/contexto/` — arquitectura, convenciones, flujo de trabajo, glosario, decisiones y errores conocidos.
- `constitution/` — misión, roadmap y este documento.
- `feature/` — una carpeta por feature numerada (`NNN-nombre-feature/` con `spec.md`, `plan.md`, `tasks.md`); hoy: `001-gestion-candidatos`, `002-catalogos`, `003-adjuntos`, `004-parsing-cv`.

## Comandos

- `./gradlew bootRun` — arranca el servidor en local (puerto 8084).
- `./gradlew test` — ejecuta los tests (deben pasar antes de cada commit).
- `./gradlew check` — build completo con tests.
- `./gradlew build -x test` — compila para producción sin correr tests.
- No hay tarea de lint (Checkstyle/SpotBugs no configurados).

## Modelo de datos / dominio

_Las entidades o estructuras centrales y sus campos/reglas. Documenta solo lo no obvio: invariantes, mecánicas especiales, qué campo controla qué._

- `Candidate` — raíz del agregado de postulante; incluye el perfil profesional (educación, experiencia, habilidades hard/soft, idiomas, certificaciones, disponibilidad y notas) como subagregados.
- `CandidateState` / `CandidateStatus` — máquina de estados que controla el ciclo de vida del candidato y de su perfil.
- `Attachment` / `StoredAttachment` / `AttachmentUpload` — adjuntos (CVs, portafolios); el contenido vive en disco vía `AttachmentStoragePort`.
- `CandidateParseResult` — resultado del parsing de un CV (PDFBox + Ollama), siempre sujeto a revisión humana.
- Catálogos — `CountryCode`, `EducationLevel`, `ExperienceRange`, `Language`, `LanguageLevel`, `HardSkill`, `SoftSkill`; los DTOs que los referencian deben validarse contra ellos.

## Convenciones

_Reglas de estilo y patrones a seguir. Nombres, organización, manejo de errores, validación, idioma del contenido, etc._

- **Nombrado:** modelos de dominio sustantivos simples (`Candidate`); entidades JPA `XxxEntity`; repositorios `XxxJpaRepository`; adaptadores `XxxRepositoryAdapter`; mappers `XxxPersistenceMapper`/`XxxWebMapper`; DTOs `XxxRequest`/`XxxResponse`; puertos de entrada `XxxUseCase`; puertos de salida `XxxRepositoryPort`.
- **Inyección de dependencias:** por constructor, nunca `@Autowired` en campos.
- **Boilerplate:** Lombok obligatorio (`@Getter`, `@Setter`, `@Builder`, `@FieldDefaults`).
- **Mapeo:** MapStruct con `componentModel = "spring"`; un mapper por agregado.
- **Tests:** ubicación espejo de main; profile `test` sin security; agregar o actualizar tests del código que cambie.
- **Errores:** respuestas HTTP JSON centralizadas en `GlobalExceptionHandler`.
- **Commits / PR:** título con formato `[ats-candidate] <mensaje>`; correr `./gradlew check` antes de commitear.
- **Código sin comentarios inline** — autoexplicativo.

## Estilo visual

_Solo si el proyecto tiene interfaz. Omite si no aplica._

- No aplica: es una API REST sin interfaz de usuario.

## Límites duros

_Lo que NUNCA se debe hacer. Reglas de seguridad, dependencias prohibidas, zonas congeladas._

- No importar clases de otro microservicio — la comunicación entre servicios va por HTTP/eventos.
- No invertir dependencias de capas (`domain/` no usa clases de `infrastructure/`).
- No subir archivos `.env*` ni secrets; `JWT_SECRET` va por variable de entorno.
- No usar `@Autowired` en campos; inyectar por constructor.
- No agregar comentarios inline en el código.
- No instalar dependencias ni cambiar versiones del stack sin avisar.
