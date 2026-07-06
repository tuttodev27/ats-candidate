# HU-M08 — Agregar handler global para IllegalArgumentException e IllegalStateException

**Como** API
**Quiero** que las excepciones `IllegalArgumentException` e `IllegalStateException` tengan una respuesta estructurada
**Para** que el frontend reciba errores consistentes.

### Criterios de aceptación

1. Agregar `@ExceptionHandler(IllegalArgumentException.class)` en `GlobalExceptionHandler` → 400 Bad Request.
2. Agregar `@ExceptionHandler(IllegalStateException.class)` en `GlobalExceptionHandler` → 500 Internal Server Error.
3. El formato de error sigue la misma estructura que las excepciones existentes (timestamp, status, error, message, path).
4. Tests del handler.

**Archivo:** `infrastructure/in/web/exception/GlobalExceptionHandler.java`
