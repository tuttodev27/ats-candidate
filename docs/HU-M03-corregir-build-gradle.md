# HU-M03 — Corregir dependencias de build.gradle

**Como** desarrollador
**Quiero** corregir las dependencias inválidas en build.gradle
**Para** que el proyecto compile sin errores y los tests funcionen correctamente.

### Criterios de aceptación

1. Reemplazar `spring-boot-starter-webmvc` por `spring-boot-starter-web`.
2. Reemplazar `spring-boot-starter-actuator-test` por `spring-boot-starter-test`.
3. Reemplazar `spring-boot-starter-data-jpa-test` por `spring-boot-starter-test`.
4. Reemplazar `spring-boot-starter-validation-test` por `spring-boot-starter-test`.
5. Reemplazar `spring-boot-starter-webmvc-test` por `spring-boot-starter-test`.
6. Eliminar el duplicado de `spring-boot-starter-oauth2-resource-server`.
7. Verificar que `./gradlew build` pase sin errores.

**Archivo:** `build.gradle`
