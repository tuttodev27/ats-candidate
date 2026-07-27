# ATS Candidate

Microservicio de gestion de postulantes (candidatos) para el sistema ATS (Applicant Tracking System).

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](#)

---

## Tabla de Contenidos

- [Descripcion General](#descripcion-general)
- [Stack Tecnologico](#stack-tecnologico)
- [Arquitectura](#arquitectura)
- [Prerrequisitos](#prerrequisitos)
- [Inicio Rapido](#inicio-rapido)
- [Base de Datos](#base-de-datos)
- [Swagger - OpenAPI](#swagger--openapi)
- [Docker](#docker)

---

## Descripcion General

**ATS Candidate** es un microservicio REST construido con Spring Boot 4 que gestiona:

- **Candidatos** con perfil profesional, educacion, experiencia laboral, habilidades (hard/soft), idiomas, certificaciones y disponibilidad
- **Archivos adjuntos** (CVs, portafolios) con storage en disco
- **Parsing de CVs** con Apache PDFBox para extraccion automatica de datos
- **Autenticacion** con JWT (JSON Web Tokens)
- **Eureka Client** para integracion con el registry de servicios

Sigue una **arquitectura hexagonal** (puertos y adaptadores) que separa la logica de dominio de la infraestructura.

---

## Stack Tecnologico

| Capa | Tecnologia | Version |
|------|-----------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.0.2 |
| Microservicios | Spring Cloud Netflix Eureka | 2025.1.1 |
| Seguridad | Spring Security + OAuth2 + JWT | JJWT 0.12.6 |
| Persistencia | Spring Data JPA + Hibernate | - |
| Base de datos | PostgreSQL | 16 |
| Migraciones | Flyway | - |
| Mapeo | MapStruct | 1.6.3 |
| Cache | Caffeine | - |
| API Docs | SpringDoc OpenAPI | 2.8.13 |
| Metricas | Micrometer + Prometheus | - |
| Validacion | Spring Validation | - |
| PDF | Apache PDFBox | 3.0.3 |
| Build | Gradle | Wrapper |
| Contenedores | Docker + Docker Compose | - |

---

## Arquitectura

El proyecto sigue el patron **Hexagonal (Puertos y Adaptadores)**:

```
com.ats.candidate
├── domain/                  # Capa de dominio (nucleo)
│   ├── model/               # Entidades de negocio
│   │   ├── Candidate.java
│   │   ├── CandidateEducation.java
│   │   ├── CandidateExperience.java
│   │   ├── CandidateHardSkill.java
│   │   ├── CandidateSoftSkill.java
│   │   ├── CandidateLanguage.java
│   │   ├── CandidateCertification.java
│   │   ├── CandidateAvailability.java
│   │   └── ...
│   ├── port/
│   │   ├── in/usecase/      # Puertos de entrada (Use Cases)
│   │   └── out/
│   │       ├── repository/  # Puertos de repositorio
│   │       └── storage/     # Puertos de almacenamiento
│   └── exception/           # Excepciones de negocio
│
├── application/             # Capa de aplicacion
│   ├── service/             # Implementacion de casos de uso
│   └── parser/              # Parsing de CVs
│
└── infrastructure/          # Capa de infraestructura
    ├── config/              # Configuracion (Security, CORS, etc.)
    └── in/
        └── web/
            ├── controller/  # Controladores REST
            ├── dto/         # Requests y Responses
            ├── exception/   # Manejador global de excepciones
            └── mapper/      # Mappers de entrada
    └── out/
        ├── adapter/         # Implementacion de puertos de repositorio
        ├── entity/          # Entidades JPA
        ├── mapper/          # Mappers MapStruct
        ├── repository/      # Interfaces JPA Repository
        └── storage/         # Adaptador de almacenamiento en disco
```

---

## Prerrequisitos

- **Java 21** (JDK)
- **Docker** y **Docker Compose**
- **Gradle** (o usar el wrapper `./gradlew` incluido)

---

## Inicio Rapido

### 1. Levantar la base de datos

```bash
docker compose up -d postgres
```

### 2. Ejecutar la aplicacion

```bash
./gradlew bootRun
```

La API estara disponible en: **http://localhost:8084**

### 3. Probar la aplicacion

```bash
curl http://localhost:8084/actuator/health
```

---

## Base de Datos

### Configuracion

| Parametro | Valor |
|-----------|-------|
| Puerto | 5434 |
| Base de datos | ats_candidate |
| Usuario | ats-candidate |

### Migraciones (Flyway)

Las migraciones se encuentran en `src/main/resources/db/migration/`.

El esquema de la base de datos se gestiona automaticamente con **Flyway** al iniciar la aplicacion.

---

## Swagger - OpenAPI

La documentacion interactiva de la API esta disponible en:

- **Swagger UI:** http://localhost:8084/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8084/v3/api-docs

Todos los endpoints estan documentados con anotaciones `@Operation` y `@ApiResponses` de Swagger. El esquema de seguridad Bearer JWT esta configurado automaticamente.

---

## Docker

### Docker Compose

Ejecutar la base de datos:

```bash
docker compose up -d
```

Servicios:
- **postgres** - PostgreSQL 16 en puerto `5434`

---

## Licencia

Proyecto privado - ATS Team (Pablo Gallegos)
