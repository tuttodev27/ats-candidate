package com.ats.candidate.application.parser;

import com.ats.candidate.infrastructure.config.OllamaProperties;
import com.ats.candidate.infrastructure.out.ollama.OllamaClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AiCvParser {

    private static final Logger log = LoggerFactory.getLogger(AiCvParser.class);

    private static final String SYSTEM_PROMPT = """
            Sos un extractor de datos de CVs (curriculums vitae).
            Analizá el texto provisto y extraé toda la información posible.
            Devolvé ÚNICAMENTE un JSON válido con la siguiente estructura, sin texto adicional:
            {
              "firstName": "string|null",
              "lastName": "string|null",
              "email": "string|null",
              "phone": "string|null",
              "headline": "string|null",
              "summary": "string|null",
              "latestPosition": "string|null",
              "educations": [
                {
                  "level": "string|null",
                  "degree": "string|null",
                  "institution": "string|null",
                  "startDate": "YYYY-MM-DD|null",
                  "endDate": "YYYY-MM-DD|null"
                }
              ],
              "matchedHardSkills": ["string"],
              "matchedSoftSkills": ["string"]
            }
            Reglas:
            - Si un campo no se puede extraer, usá null.
            - Para firstName/lastName: si el CV tiene un nombre completo en la primer línea, separalo en firstName y lastName.
            - Para phone: incluí el código de país si está presente.
            - Para educations: podés incluir múltiples entradas si hay más de una carrera/título listado.
            - Para matchedHardSkills/matchedSoftSkills: solo incluí skills que estén explícitamente mencionados en el texto del CV.
            - Fechas en formato YYYY-MM-DD. Si solo hay año, usá YYYY-01-01 para inicio o YYYY-12-31 para fin.
            - level debe ser uno de: "Doctorado", "Magister", "Universitario", "Tecnico", "Educacion media", o null.
            """;

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;
    private final OllamaProperties properties;

    public AiCvParser(OllamaClient ollamaClient, ObjectMapper objectMapper, OllamaProperties properties) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public CvParser.ParsedCv parse(String text, List<String> hardSkillNames, List<String> softSkillNames) {
        if (!ollamaClient.isAvailable()) {
            log.warn("Ollama is not available, cannot run AI-based CV parsing");
            return null;
        }

        String truncated = truncateText(text, properties.getMaxTextLength());
        String userMessage = buildUserMessage(truncated, hardSkillNames, softSkillNames);

        try {
            String response = ollamaClient.chat(SYSTEM_PROMPT, userMessage);
            return mapToParsedCv(response);
        } catch (Exception ex) {
            log.error("AI CV parsing failed: {}", ex.getMessage());
            return null;
        }
    }

    private String buildUserMessage(String cvText, List<String> hardSkillNames, List<String> softSkillNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TEXTO DEL CV ===\n");
        sb.append(cvText);
        sb.append("\n\n=== HARD SKILLS DISPONIBLES PARA MATCHEAR ===\n");
        sb.append(String.join(", ", hardSkillNames));
        sb.append("\n\n=== SOFT SKILLS DISPONIBLES PARA MATCHEAR ===\n");
        sb.append(String.join(", ", softSkillNames));
        return sb.toString();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    private CvParser.ParsedCv mapToParsedCv(String json) {
        CvParser.ParsedCv cv = new CvParser.ParsedCv();
        try {
            JsonNode root = objectMapper.readTree(json);

            cv.firstName = getNullableString(root, "firstName");
            cv.lastName = getNullableString(root, "lastName");
            cv.email = getNullableString(root, "email");
            cv.phone = getNullableString(root, "phone");
            cv.headline = getNullableString(root, "headline");
            cv.summary = getNullableString(root, "summary");
            cv.latestPosition = getNullableString(root, "latestPosition");

            JsonNode educationsNode = root.path("educations");
            if (educationsNode.isArray()) {
                for (JsonNode eduNode : educationsNode) {
                    CvParser.ParsedEducation edu = new CvParser.ParsedEducation();
                    edu.level = getNullableString(eduNode, "level");
                    edu.degree = getNullableString(eduNode, "degree");
                    edu.institution = getNullableString(eduNode, "institution");
                    edu.startDate = parseLocalDate(getNullableString(eduNode, "startDate"));
                    edu.endDate = parseLocalDate(getNullableString(eduNode, "endDate"));
                    cv.educations.add(edu);
                }
            }

            JsonNode hardSkillsNode = root.path("matchedHardSkills");
            if (hardSkillsNode.isArray()) {
                for (JsonNode skill : hardSkillsNode) {
                    cv.matchedHardSkills.add(skill.asText());
                }
            }

            JsonNode softSkillsNode = root.path("matchedSoftSkills");
            if (softSkillsNode.isArray()) {
                for (JsonNode skill : softSkillsNode) {
                    cv.matchedSoftSkills.add(skill.asText());
                }
            }

        } catch (Exception ex) {
            log.error("Failed to map AI response to ParsedCv: {}", ex.getMessage());
            throw new RuntimeException("Invalid AI response format", ex);
        }
        return cv;
    }

    private String getNullableString(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isNull() || value.isMissingNode()) {
            return null;
        }
        String text = value.asText();
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
