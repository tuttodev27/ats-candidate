# HU-M01 — Proteger endpoints con anyRequest().authenticated()

**Como** equipo de seguridad
**Quiero** reemplazar `.anyRequest().permitAll()` por `.anyRequest().authenticated()`
**Para** que todos los endpoints no matcheados explícitamente requieran autenticación.

### Criterios de aceptación

1. Se reemplaza `.anyRequest().permitAll()` por `.anyRequest().authenticated()` en `SecurityConfig.java`.
2. Se agregan matchers explícitos para endpoints públicos: `/actuator/health`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` (permitAll).
3. Se agrega matcher para `GET /api/country-codes`, `GET /api/education-levels`, `GET /api/experience-ranges`, `GET /api/languages`, `GET /api/language-levels`, `GET /api/candidates/statuses` como públicos.
4. Se agrega matcher para `POST /api/candidates/{candidateId}/attachments/{attachmentId}/parse` con `RECRUITER_WRITE`.
5. Tests que verifiquen que endpoints sin token respondan 401.

**Archivo:** `infrastructure/config/SecurityConfig.java`
