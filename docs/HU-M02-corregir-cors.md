# HU-M02 — Corregir configuración CORS

**Como** equipo de seguridad
**Quiero** restringir la configuración CORS para no permitir credenciales con orígenes comodín
**Para** evitar vulnerabilidades de CSRF con orígenes no confiables.

### Criterios de aceptación

1. Se elimina `setAllowCredentials(true)` si se mantiene `setAllowedOriginPatterns(List.of("*"))`.
2. O se listan explícitamente los orígenes permitidos en producción (ej: `http://localhost:5173`, `http://localhost:3000`).
3. Se mantiene compatibilidad con entornos de desarrollo.
4. Tests de integración que verifiquen el header CORS en la respuesta.

**Archivo:** `infrastructure/config/SecurityConfig.java`
