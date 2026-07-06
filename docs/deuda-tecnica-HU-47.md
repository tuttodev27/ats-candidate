# Deuda Técnica — HU-47: Recruiter gestiona ficha del candidato

## 1. Mapeo de rol `RECRUITER` a permisos granulares

**Problema:** El `SecurityConfig` actual valida permisos como `CANDIDATE_READ`, `CANDIDATE_CREATE`, etc., no un rol `RECRUITER`. Si `ats-usuarios` emite un claim `role: "RECRUITER"`, el sistema no lo reconoce y responderá 403 Forbidden.

**Solución:** Implementar un `JwtAuthenticationConverter` que mapee el claim `role: "RECRUITER"` a las autoridades `CANDIDATE_READ`, `CANDIDATE_CREATE`, `CANDIDATE_UPDATE`, `CANDIDATE_DELETE`.

**Archivo:** `infrastructure/config/SecurityConfig.java`

---

## 2. Flujo contradictorio entre parseo automático y guardado

**Problema:** La HU indica que el recruiter completa la ficha y luego llama a `POST /api/candidates` para guardar (AC10), pero el parseo automático (HU-44) ya persiste los datos del candidato vía `candidateRepositoryPort.update()` dentro de `preFillCandidate()`. Esto significa que cuando el recruiter va a "guardar", el candidato ya existe y fue modificado. El flujo real es: crear candidato → subir CV (parsea y pre-llena) → editar vía `PUT /api/candidates/{id}`.

**Solución:** Alinear el flujo de la HU con la implementación real, o modificar el parseo para que no persista automáticamente y solo devuelva los datos extraídos al frontend.

**Archivos:** `application/service/AttachmentService.java`, documentación de la HU

---

## 3. Validación de secuencia de estados del candidato ✅ *RESUELTO*

**Problema:** La HU indica la transición `NEW → IN_REVIEW → INTERVIEW → SHORTLIST → REJECTED / HIRED` (AC15), pero no especifica si los saltos están permitidos (ej: `NEW → SHORTLIST` directo). El código actual no valida la secuencia, permitiendo cualquier transición.

**Solución:** Se implementó una máquina de estados vía `ALLOWED_TRANSITIONS` en `CandidateService` que:
- Permite avanzar hacia adelante en la secuencia.
- Permite saltar estados intermedios (ej: `NEW → INTERVIEW`).
- Marca `REJECTED` y `HIRED` como estados terminales (sin transiciones salientes).
- Rechaza transiciones hacia atrás con `InvalidCandidateStateException` → 400 Bad Request.

**Archivos:** `application/service/CandidateService.java`

---

## 4. HU-44 referenciada no coincide con el CSV

**Problema:** La HU-47 referencia HU-44 para el parseo automático, pero en `docs/trello-hu-37-47.csv` la HU-44 figura como "Subir CV del postulante", no como "Parseo automático del CV". Hay una historia distinta ocupando ese número.

**Solución:** Asignar un nuevo número (ej: HU-48) a la historia de parseo automático y actualizar la referencia en HU-47, o reemplazar la HU-44 existente.

---

# Deuda Técnica — HU-59: Parseo de CV con IA (sugerencia previa al registro)

## 1. Falta dependencia HTTP y configuración de LLM

**Problema:** El proyecto no tiene `WebClient` ni `RestTemplate` declarados en `build.gradle`. Tampoco existe configuración para API keys de Claude (URL, key, timeout, modelo). No se puede integrar con un LLM sin estas dependencias y propiedades.

**Solución:** Agregar `spring-boot-starter-webflux` (o `RestTemplate` bean) al `build.gradle`. Crear propiedades en `application.yaml`:
```yaml
app:
  ai:
    claude:
      api-url: https://api.anthropic.com/v1/messages
      api-key: ${CLAUDE_API_KEY}
      model: claude-sonnet-4-20250514
      max-tokens: 4096
      timeout: 30s
```

**Archivos:** `build.gradle`, `src/main/resources/application.yaml`

---

## 2. Seguridad: endpoint quedaría público

**Problema:** En `SecurityConfig`, el `.anyRequest().permitAll()` atraparía `POST /api/candidates/cv/parse`. Al no haber un matcher específico, el endpoint sería accesible sin autenticación.

**Solución:** Agregar matcher antes del `anyRequest()`:
```java
.requestMatchers(HttpMethod.POST, "/api/candidates/cv/parse").hasAuthority("RECRUITER_WRITE")
```

**Archivo:** `infrastructure/config/SecurityConfig.java`

---

## 3. HTTP 422 Unprocessable Entity no está manejado

**Problema:** La HU requiere responder 422 cuando el PDF no tiene texto legible (escaneado sin OCR). El `GlobalExceptionHandler` actualmente solo devuelve 400 para `AttachmentParsingException`. No hay una excepción ni handler para 422.

**Solución:** Crear una excepción `UnprocessablePdfException` y agregar su handler en `GlobalExceptionHandler`:
```java
@ExceptionHandler(UnprocessablePdfException.class)
public ResponseEntity<ErrorResponse> handleUnprocessablePdf(UnprocessablePdfException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(...);
}
```

**Archivos:** `domain/exception/UnprocessablePdfException.java`, `infrastructure/in/web/exception/GlobalExceptionHandler.java`

---

## 4. Fallback a regex subespecificado

**Problema:** La HU indica que si el LLM no está disponible se use el parseo por regex como fallback, pero:
- `CvParser.parse()` requiere `hardSkillNames` y `softSkillNames` del catálogo. Si el catálogo no está disponible, el fallback también falla.
- El `CvParser` no produce `confidenceScore`. Habría que definir qué valor asignar (ej: 0.5 fijo).
- No se define cómo diferenciar en la respuesta si el resultado vino del LLM o del regex.

**Solución:** Definir un `parserVersion` o `source` en `ParsedCvResponse` ("AI" vs "REGEX"). Para el `confidenceScore` del fallback, asignar un valor fijo (ej: 0.5) y documentarlo.

**Archivos:** Documentación de HU-59, `ParsedCvResponse` DTO

---

## 5. `confidenceScore` no existe en el modelo actual

**Problema:** El `CvParser.ParsedCv` no tiene campo `confidenceScore`. Se necesita un DTO nuevo (`ParsedCvResponse`) separado del modelo interno del parser, que incluya este campo junto con los datos extraídos.

**Solución:** Crear un nuevo record `ParsedCvResponse` en `infrastructure/in/web/dto/` con todos los campos extraídos más `confidenceScore` (Double) y `source` (String: "AI" | "REGEX").

**Archivo:** `infrastructure/in/web/dto/ParsedCvResponse.java`

---

## 6. Ubicación del endpoint no definida

**Problema:** El path `POST /api/candidates/cv/parse` no encaja en `AttachmentController` (mapeado a `/api/candidates/{candidateId}/attachments`). No hay un controlador existente que sirva este endpoint sin candidateId.

**Solución:** Evaluar dos opciones:
- **Opción A:** Crear un nuevo `CvParseController` con `@RequestMapping("/api/candidates/cv")`.
- **Opción B:** Colocarlo en `CandidateController` con una ruta específica.

**Archivo:** Nuevo controller o modificar `CandidateController.java`
