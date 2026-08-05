# Flujo de trabajo

## Build local

```bash
./gradlew build -x test        # compilar sin tests
./gradlew bootRun              # levantar servicio
./gradlew test                 # todos los tests
./gradlew test --tests "com.ats.candidate.XxxTest.method"
```

## Base de datos

```bash
docker compose up -d           # levanta PostgreSQL 16
```

Puerto local: 5434. Base: `ats_candidate`. Usuario: `ats-candidate`. Password: `postgres-candidate`.

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `JWT_SECRET` | `mi-secret-local-superseguro-worksync-2026-ats-usuarios` | Clave HMAC para JWT |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL de Ollama |
| `OLLAMA_MODEL` | `llama3` | Modelo de LLM |
| `OLLAMA_TIMEOUT` | `60` | Timeout en segundos |
| `OLLAMA_MAX_TEXT` | `12000` | Máx. caracteres del CV enviado a Ollama |
| `DB_URL` | `jdbc:postgresql://localhost:5434/ats_candidate` | JDBC URL |
| `DB_USERNAME` | `ats-candidate` | Usuario BD |
| `DB_PASSWORD` | `postgres-candidate` | Password BD |

## Ciclo de un CV

1. `POST /api/candidates/{id}/attachments` → sube PDF.
2. PDFBox extrae texto del PDF.
3. `AttachmentService` orquesta el parseo:
   - `AiCvParser.parse()` llama a Ollama (`/api/chat`).
   - Si Ollama no responde → `CvParser.parse()` con regex.
4. Resultado (`CandidateParseResult`) se persiste.
5. Se actualiza `Candidate` con datos extraídos (nombre, email, headline, skills).

## Autenticación

1. Cliente envía JWT en header `Authorization: Bearer <token>`.
2. `SecurityConfig` valida firma HMAC-SHA256 con `security.jwt.secret`.
3. `JwtAuthorityExtractor` extrae `permissions`, `authorities`, `roles` del token.
4. `SecurityFilterChain` aplica reglas por endpoint.

## Endpoints públicos

- `GET /actuator/health`
- `GET /swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`

## Carga de datos inicial

`data.sql` se ejecuta en cada startup (`spring.sql.init.mode: always`). Puebla 7 catálogos con `ON CONFLICT`:
- 6 education levels
- 13 country codes
- 6 experience ranges
- 7 language levels
- 6 languages
- 30 hard skills
- 12 soft skills

## Tests

- Perfil `test`. H2 en modo PostgreSQL.
- Security deshabilitado vía exclude de `SecurityAutoConfiguration`.
- 14 archivos de test: cubren servicios, controllers, mappers web, repositorio JPA, storage adapter.

## Docker

```bash
docker build -t ats-candidate .
docker run -p 8084:8084 ats-candidate  # requiere PostgreSQL accesible
```

`Dockerfile` usa `eclipse-temurin:21-jdk-alpine`. Copia el JAR desde `build/libs/`.

## Commit

Formato: `[ats-candidate] <mensaje>`.
