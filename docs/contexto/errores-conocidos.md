# Errores conocidos

## 1. Flyway declarado pero no usado

`flyway-core` y `flyway-database-postgresql` están en `build.gradle` como dependencias. Sin embargo la app usa `ddl-auto: update` + `data.sql`. Flyway nunca se ejecuta. Esto no causa error, pero es código muerto y confunde sobre la estrategia de migraciones.

**Impacto:** Bajo. Dependencias innecesarias en el classpath.

**Solución:** Remover las dependencias Flyway de `build.gradle` o implementar migraciones versionadas.

## 2. `data.sql` se re-ejecuta siempre

Con `spring.sql.init.mode: always` y `defer-datasource-initialization: true`, `data.sql` corre en cada inicio. Aunque las tablas de catálogo tienen `ON CONFLICT DO UPDATE`, las tablas transaccionales no tienen seed, pero si alguien agrega inserts sin `ON CONFLICT` en el futuro, se duplicarán filas en cada reinicio.

**Impacto:** Medio. Actualmente seguro por `ON CONFLICT`, pero frágil ante cambios.

**Solución:** Reemplazar con migraciones Flyway o cambiar a `mode: never` y poblar catálogos desde código con verificación de existencia.

## 3. `application-test.yaml` tiene `port: 8085` bajo `spring:`

En `src/test/resources/application-test.yaml`, la línea `port: 8085` está anidada bajo `spring:` en vez de `server:`. Esto hace que el setting sea ignorado. El test corre en el puerto por defecto (8080) si no se especifica server.port.

```yaml
spring:
  ...
  port: 8085          # ← DEBERÍA SER server.port: 8085
```

**Impacto:** Bajo. Tests funcionan igual porque no usan el puerto directamente.

**Solución:** Mover a `server.port: 8085`.

## 4. Adjuntos se pierden con `gradle clean`

`LocalAttachmentStorageAdapter` guarda archivos en `build/uploads/` por defecto. `build/` es el directorio de output de Gradle y se elimina con `clean`. Cualquier archivo subido se pierde.

**Impacto:** Alto en desarrollo local si se ejecuta `clean` entre reinicios.

**Solución:** Cambiar `app.attachments.storage-path` a una ruta fuera de `build/` (ej. `./data/uploads`).

## 5. Sin migración de schema

`ddl-auto: update` no deja rastro de cambios. No hay historial de modificaciones a la base de datos. En equipo, cada desarrollador puede tener schemas distintos. No hay rollback posible.

**Impacto:** Medio. Afecta colaboración y despliegues.

**Solución:** Implementar Flyway (la dependencia ya está declarada) o Liquibase.

## 6. Sin test de integración con seguridad

`application-test.yaml` excluye `SecurityAutoConfiguration`. Los tests de controllers no validan reglas de autorización, claims JWT, ni escenarios de token inválido.

**Impacto:** Medio. Las reglas de seguridad no están cubiertas por tests automatizados.

**Solución:** Agregar test slice `@WebMvcTest` con `@WithMockUser` o mock de JwtDecoder.

## 7. Eureka declarado pero deshabilitado

`spring-cloud-starter-netflix-eureka-client` está en dependencias, pero `eureka.client.enabled: false`. Si se habilita, requeriría un Eureka Server corriendo.

**Impacto:** Bajo. Código muerto.

**Solución:** Remover dependencia si no se usará Eureka, o implementar service discovery.

## 8. Sin test para `CvParser` (regex)

`AiCvParser` y `CvParser` no tienen tests unitarios dedicados. Solo se ejercitan indirectamente vía `AttachmentServiceTest`.

**Impacto:** Medio. El fallback regex no tiene cobertura directa.

## 9. `@EnableMethodSecurity` sin uso real

Aunque `@EnableMethodSecurity` está habilitado, no hay métodos anotados con `@PreAuthorize`. Las reglas están todas en `SecurityConfig`.

**Impacto:** Bajo.

## 10. JWT con clave secreta default hardcodeada

`application.yaml` contiene un default para `JWT_SECRET`. Si alguien despliega sin cambiar la variable, la clave es pública y cualquiera puede firmar tokens válidos.

**Impacto:** Alto en producción.

**Solución:** Exigir `JWT_SECRET` como variable obligatoria y no tener default en producción.
