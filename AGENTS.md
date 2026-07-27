# AGENTS.md — ats-candidate

## Dev environment tips
- Este repo es un microservicio independiente — no forma parte de un monorepo.
- El servicio se encuentra en la raíz del repo (directamente en `ats-postulant/`).
- Usa `./gradlew build -x test` para compilar sin correr tests.
- El nombre del servicio es `ats-candidate` (confirmado en `settings.gradle`).
- Puerto del servicio: **8084** (definido en `application.yaml`).
- Usa `./gradlew bootRun` para levantarlo localmente.
- Levanta su contenedor de Postgres con `docker compose up -d` antes de correr el servicio.
- La base de datos se llama `ats_candidate` y usa PostgreSQL 16 (puerto externo 5434).
- Si el servicio necesita a otro (ej. ats-candidate llama a ats-user), levanta ese otro repo por separado y confirma su puerto en su `application.yml`.
- El proyecto usa Flyway para migraciones de base de datos.
- Variables de entorno principales: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
- Para el parseo de CVs con IA se usa **Ollama** local. Variables: `OLLAMA_BASE_URL` (default `http://localhost:11434`), `OLLAMA_MODEL` (default `llama3`), `OLLAMA_TIMEOUT`, `OLLAMA_MAX_TEXT`.

## Testing instructions
- Corre `./gradlew test` para ejecutar todos los tests del servicio.
- Para un test puntual: `./gradlew test --tests "com.ats.candidate.ClassName.methodName"`.
- Los tests usan H2 como base de datos en memoria (`testRuntimeOnly 'com.h2database:h2'`).
- Corrige cualquier error de test o compilación hasta que el build quede en verde.
- Después de mover clases o cambiar paquetes, corre `./gradlew check` (incluye tests + Checkstyle/SpotBugs si están configurados).
- Agrega o actualiza tests para el código que cambies, aunque nadie lo pida explícitamente.

## Architecture conventions
- Respeta la arquitectura hexagonal dentro del servicio:
  - `domain` — modelos, puertos (interfaces), excepciones y servicios de dominio. No depende de `infrastructure` ni `application`.
  - `application` — casos de uso (implementaciones de puertos de entrada).
  - `infrastructure` — adaptadores REST controllers, repositorios JPA, mappers, DTOs, configuración de seguridad.
- Paquete base: `com.ats.candidate`
- Estructura de adaptadores de entrada: `infrastructure.in.web` (controllers, DTOs, mappers).
- Estructura de adaptadores de salida: `infrastructure.out` (repositories JPA, entities, mappers, adapters, storage).
- No importes clases de otro microservicio directamente — la comunicación entre servicios va por HTTP/eventos, no por dependencia de código.
- Usa MapStruct para mapeo entre entidades de dominio, persistence y web DTOs.
- Usa Lombok para reducir boilerplate.
- Las entidades JPA están en `infrastructure.out.entity`.
- Los repositorios JPA están en `infrastructure.out.repository`.
- Los puertos de salida (interfaces) están en `domain.port.out`.
- Los puertos de entrada se definen como interfaces en `domain` y se implementan en `application.service`.
- Este servicio maneja PDFs con Apache PDFBox para adjuntos de candidatos.
- El parseo de CVs usa Ollama (`infrastructure.out.ollama.OllamaClient`) con fallback a regex (`CvParser`).

## PR instructions
- Formato de título: `[ats-candidate] <Title>`
- Siempre corre `./gradlew check` antes de hacer commit.
