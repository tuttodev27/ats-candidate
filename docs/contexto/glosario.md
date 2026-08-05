# Glosario

| Término | Definición |
|---|---|
| **ATS** | Applicant Tracking System. Sistema de seguimiento de postulantes. |
| **Postulante / Candidato** | Persona que aplica a una oferta laboral. Modelo `Candidate`. |
| **CV / Currículum Vitae** | Documento con la experiencia y formación del postulante. Se procesa vía parseo (Ollama o regex). |
| **Parseo de CV** | Extracción automatizada de datos estructurados (nombre, email, skills, educación) desde un CV no estructurado. |
| **Hard Skill** | Habilidad técnica (Java, PostgreSQL, Docker). Catálogo en `HardSkill`. |
| **Soft Skill** | Habilidad blanda (comunicación, liderazgo). Catálogo en `SoftSkill`. |
| **Nivel educativo** | `EducationLevel`: No especificado, Educación media, Técnico, Universitario, Magister, Doctorado. |
| **Nivel de idioma** | `LanguageLevel`: A1-C2 + NATIVE, según MCER. |
| **Rango de experiencia** | `ExperienceRange`: Sin experiencia, 0-1, 1-2, 3-5, 6-10, 10+ años. |
| **Código de país** | `CountryCode`: 13 países con prefijo telefónico e ISO. |
| **Adjunto / Attachment** | Archivo (PDF) asociado a un candidato. Se almacena localmente y se indexa su contenido. |
| **Ollama** | Servicio local de modelos de lenguaje. Se usa para parseo de CV con IA. |
| **JWT** | JSON Web Token para autenticación. Firma HMAC-SHA256 con clave simétrica. |
| **OAuth2 Resource Server** | Rol que valida tokens JWT. Configurado pero apuntando a clave local, no a un issuer externo. |
| **Puerto de entrada (in port)** | Interfaz en `domain/port/in/` que define casos de uso. |
| **Puerto de salida (out port)** | Interfaz en `domain/port/out/` que define contratos con infraestructura (repositorios, storage). |
| **Adaptador** | Implementación de un puerto de salida. Conecta el dominio con JPA, storage, etc. |
| **DTO** | Data Transfer Object. Usado en la capa web para requests/responses. |
| **Entity** | Clase anotada con `@Entity` que mapea a una tabla de base de datos. |
| **Catalogo** | Tabla de referencia: `EducationLevel`, `CountryCode`, `ExperienceRange`, `Language`, `LanguageLevel`, `HardSkill`, `SoftSkill`. Se cargan via `data.sql`. |
| **HMAC-SHA256** | Algoritmo de firma simétrica usado para firmar y validar JWTs. |
| **ddl-auto: update** | Estrategia de Hibernate que crea/actualiza tablas automáticamente según las entidades. |
| **Status** | Estado del candidato en el proceso de selección. `CandidateStatus`. |
| **State** | Estado interno del candidato. `CandidateState`. |
| **AuditEvent** | Registro de eventos de auditoría sobre candidatos. |
