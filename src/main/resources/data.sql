INSERT INTO education_level (id, name, order_number, active)
VALUES
    (1, 'No especificado', 0, true),
    (2, 'Educacion media', 1, true),
    (3, 'Tecnico', 2, true),
    (4, 'Universitario', 3, true),
    (5, 'Magister', 4, true),
    (6, 'Doctorado', 5, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    order_number = EXCLUDED.order_number,
    active = EXCLUDED.active;

INSERT INTO language_level (id, code, name, order_number, active)
VALUES
    (1, 'A1', 'Basico inicial', 1, true),
    (2, 'A2', 'Basico', 2, true),
    (3, 'B1', 'Intermedio', 3, true),
    (4, 'B2', 'Intermedio alto', 4, true),
    (5, 'C1', 'Avanzado', 5, true),
    (6, 'C2', 'Nativo o bilingue', 6, true),
    (7, 'NATIVE', 'Nativo', 7, true)
ON CONFLICT (id) DO UPDATE SET
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    order_number = EXCLUDED.order_number,
    active = EXCLUDED.active;

INSERT INTO language (id, name, iso_code, active)
VALUES
    (1, 'Espanol', 'es', true),
    (2, 'Ingles', 'en', true),
    (3, 'Portugues', 'pt', true),
    (4, 'Frances', 'fr', true),
    (5, 'Aleman', 'de', true),
    (6, 'Italiano', 'it', true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    iso_code = EXCLUDED.iso_code,
    active = EXCLUDED.active;

INSERT INTO hard_skill (id, name, description, active, created_at, updated_at)
VALUES
    (1, 'Java', 'Lenguaje de programacion Java', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Spring Boot', 'Framework para aplicaciones Java', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Spring Security', 'Seguridad y autenticacion en aplicaciones Spring', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Spring Data JPA', 'Acceso a datos con JPA en Spring', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Hibernate', 'ORM para Java', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'PostgreSQL', 'Base de datos relacional PostgreSQL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'MySQL', 'Base de datos relacional MySQL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'MongoDB', 'Base de datos documental MongoDB', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'Docker', 'Contenedores y empaquetado de aplicaciones', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 'Kubernetes', 'Orquestacion de contenedores', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (11, 'Git', 'Control de versiones con Git', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (12, 'REST APIs', 'Diseno e implementacion de APIs REST', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (13, 'Microservices', 'Arquitectura basada en microservicios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (14, 'Kafka', 'Mensajeria y streaming de eventos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (15, 'RabbitMQ', 'Mensajeria asincrona', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (16, 'AWS', 'Servicios cloud de Amazon Web Services', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (17, 'Azure', 'Servicios cloud de Microsoft Azure', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (18, 'GCP', 'Servicios cloud de Google Cloud Platform', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (19, 'JUnit', 'Pruebas unitarias en Java', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (20, 'Mockito', 'Mocks para pruebas en Java', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (21, 'Maven', 'Gestion de dependencias y build con Maven', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (22, 'Gradle', 'Gestion de dependencias y build con Gradle', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (23, 'JavaScript', 'Lenguaje de programacion JavaScript', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (24, 'TypeScript', 'Lenguaje tipado basado en JavaScript', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (25, 'Angular', 'Framework frontend Angular', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (26, 'React', 'Biblioteca frontend React', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (27, 'Node.js', 'Runtime JavaScript para backend', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (28, 'Python', 'Lenguaje de programacion Python', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (29, 'SQL', 'Consulta y modelado en bases de datos relacionales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (30, 'CI/CD', 'Integracion y despliegue continuo', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO soft_skill (id, name, description, active, created_at, updated_at)
VALUES
    (1, 'Comunicacion', 'Capacidad para comunicar ideas de forma clara', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Trabajo en equipo', 'Colaboracion efectiva con otros integrantes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Liderazgo', 'Capacidad para guiar equipos e iniciativas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'Resolucion de problemas', 'Analisis y solucion de problemas complejos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Pensamiento analitico', 'Evaluacion de informacion para tomar decisiones', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'Adaptabilidad', 'Capacidad para responder a cambios de contexto', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'Proactividad', 'Iniciativa para anticiparse a necesidades o problemas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'Autonomia', 'Capacidad para trabajar con baja supervision', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'Mentoria', 'Acompanamiento y desarrollo de otros profesionales', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 'Orientacion al cliente', 'Foco en necesidades del cliente o usuario', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (11, 'Gestion del tiempo', 'Organizacion y priorizacion de tareas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (12, 'Colaboracion', 'Trabajo coordinado con distintas areas o equipos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;
