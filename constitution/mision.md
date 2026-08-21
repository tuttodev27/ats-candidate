# Misión

_Define la razón de ser del proyecto. Es la referencia que decide si una feature "encaja" o no._

## Qué construimos

ATS Candidate es el microservicio del sistema ATS (Applicant Tracking System) que gestiona a los postulantes: su perfil profesional completo, sus archivos adjuntos (CVs y portafolios) y el parseo automático de CVs con IA para extraer datos sin intervención manual.

Piezas principales del producto:

1. **Gestión de candidatos** — CRUD del perfil profesional: educación, experiencia, habilidades hard/soft, idiomas, certificaciones, disponibilidad y notas.
2. **Archivos adjuntos** — subida, almacenamiento y consulta de CVs y portafolios con storage en disco.
3. **Parsing de CVs** — extracción automática de datos del CV con Apache PDFBox complementada con IA (Ollama).

## Para quién

- Reclutadores que gestionan postulantes desde el frontend del ATS.
- Postulantes (indirectamente, a través de los datos capturados de su CV).
- Otros microservicios del sistema ATS, que consumen candidatos vía HTTP y descubren el servicio por Eureka.
- El equipo ATS y el autor (Pablo Gallegos).

## Principios

- **Arquitectura hexagonal** — `domain/` no depende de `application/` ni `infrastructure/`; la infraestructura debe ser intercambiable sin tocar el núcleo.
- **Un microservicio, un contexto** — el dominio es únicamente candidatos; los catálogos (idiomas, países, niveles de educación) viven aquí como datos de referencia.
- **Validación en la frontera** — toda entrada del usuario se valida con `jakarta.validation` en los DTOs antes de entrar al dominio.
- **Código autoexplicativo** — sin comentarios inline; los nombres de clases y métodos deben dejar clara su intención.
- **Parsing asistido por IA como mejora** — Ollama complementa la extracción de PDFBox; el resultado siempre se devuelve para que el usuario lo revise.

## Qué NO es

- No es el sistema ATS completo: no gestiona vacantes, postulaciones ni matching — eso vive en otros microservicios.
- No administra procesos de selección ni decisiones de contratación.
- No expone datos de otros dominios (empresas, vacantes, reclutadores).
- No sustituye la validación humana de los CVs parseados por IA.
