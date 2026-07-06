# HU-M17 — Configurar spring.jpa.open-in-view explícitamente

**Como** desarrollador
**Quiero** configurar explícitamente `spring.jpa.open-in-view: false`
**Para** evitar LazyInitializationException y forzar decisiones conscientes sobre carga de relaciones.

### Criterios de aceptación

1. Agregar `spring.jpa.open-in-view: false` en `application.yaml`.
2. Identificar y resolver cualquier `LazyInitializationException` que surja al deshabilitarlo.
3. Verificar que todos los datos necesarios se carguen dentro de la transacción correspondiente.
4. Tests que verifiquen que las relaciones se cargan correctamente.

**Archivo:** `src/main/resources/application.yaml`
