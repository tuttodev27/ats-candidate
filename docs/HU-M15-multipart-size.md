# HU-M15 — Configurar límite de tamaño de archivos multipart

**Como** sistema
**Quiero** configurar un límite máximo para la subida de archivos CV
**Para** evitar abusos y problemas de memoria.

### Criterios de aceptación

1. Agregar `spring.servlet.multipart.max-file-size: 10MB` en `application.yaml`.
2. Agregar `spring.servlet.multipart.max-request-size: 12MB` en `application.yaml`.
3. Archivos mayores a 10MB devuelven 400 Bad Request con mensaje descriptivo.
4. El límite se documenta en OpenAPI en el endpoint de subida de CV.

**Archivo:** `src/main/resources/application.yaml`
