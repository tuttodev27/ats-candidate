# Decisiones técnicas

## Por qué Spring Boot 4.0.2 + Java 21

Proyecto generado con Spring Initializr en 2026. Java 21 por soporte LTS y features como records, pattern matching, virtual threads.

## Por qué ddl-auto: update (y no Flyway)

A pesar de que `flyway-core` y `flyway-database-postgresql` están declarados en `build.gradle`, la app usa `ddl-auto: update` + `data.sql`. Decisión arquitectónica: no se implementaron migraciones versionadas. El seed de catálogos se maneja con `INSERT ... ON CONFLICT`.

**Consecuencia:** `data.sql` se re-ejecuta en cada inicio. `ON CONFLICT` previene duplicados en tablas de catálogo, pero las tablas transaccionales (candidatos, experiencias) no tienen seed, por lo que no hay riesgo de duplicación.

## Por qué OAuth2 resource server + JJWT manual

`spring-boot-starter-oauth2-resource-server` está en las dependencias pero la configuración JWT es completamente manual via `NimbusJwtDecoder` y `SecretKeySpec`. No usa `spring-security-oauth2` auto-configuration ni un issuer externo.

Decisión: simplicidad para desarrollo local. La clave HMAC está en `application.yaml` con default local. El `JwtAuthorityExtractor` permite migrar a un OAuth2 real sin cambiar la lógica de autoridades.

## Por qué CORS abierto

`allowedOriginPatterns: *` con `allowCredentials: true`. Decisión para desarrollo. En producción debería restringirse.

## Por qué Caffeine Cache

Ligero, en memoria, sin dependencia externa. Se usa para catálogos (education levels, country codes, etc.) que cambian con baja frecuencia. No hay configuración de TTL explícita visible.

## Por qué MapStruct 1.6.3

Generación de código en tiempo de compilación, cero overhead en runtime. Separación clara entre mappers web y de persistencia.

## Por qué PDFBox 3.0.3

Única dependencia para extracción de texto de PDFs. Apache 2.0 license. Sin alternativas evaluadas.

## Por qué Ollama para parseo de CV

Ejecución local, sin costo por API, privacidad de datos. El `AiCvParser` usa prompt que exige JSON y tiene fallback a `CvParser` (regex) si Ollama no está disponible o falla.

Modelo por defecto: `llama3`. Configurable via `ollama.model`.

## Por qué almacenamiento local en build/uploads/

Default en `build/uploads/` porque Spring Boot limpia `build/` en cada build. **Decisión deliberada para desarrollo:** los adjuntos se pierden al hacer `clean`. En producción debería apuntarse a un volumen persistente o S3.

## Por qué Eureka deshabilitado

`eureka.client.enabled: false`. El servicio está preparado para discovery pero no se usa actualmente. Se habilita cuando se despliegue con service discovery.

## Por qué `@EnableMethodSecurity`

Permite usar `@PreAuthorize` en métodos si se necesita, aunque actualmente las reglas están todas en `SecurityConfig` por endpoint.

## Por qué `spring-dotenv`

Carga variables de entorno desde `.env` en desarrollo. Evita hardcodear secrets.

## Por qué springdoc-openapi 2.8.13

Documentación de API generada automáticamente. Endpoints en `/swagger-ui.html` y `/v3/api-docs/**` son públicos.

## Por qué puerto 8084

Puerto definido en `application.yaml`. Sin conflicto con otros microservicios del ecosistema ATS.

## Por qué H2 en tests

Base en memoria, cero setup, recarga rápida. Modo PostgreSQL para compatibilidad de dialecto SQL.
