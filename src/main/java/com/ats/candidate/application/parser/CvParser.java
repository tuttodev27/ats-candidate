package com.ats.candidate.application.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CvParser {

    public static class ParsedCv {
        public String firstName;
        public String lastName;
        public String email;
        public String phone;
        public String headline;
        public String summary;
        public String latestPosition;
        public List<ParsedEducation> educations = new ArrayList<>();
        public List<String> matchedHardSkills = new ArrayList<>();
        public List<String> matchedSoftSkills = new ArrayList<>();
    }

    public static class ParsedEducation {
        public String level;
        public String degree;
        public String institution;
        public LocalDate startDate;
        public LocalDate endDate;
    }

    public static ParsedCv parse(String text, List<String> hardSkillNames, List<String> softSkillNames) {
        ParsedCv cv = new ParsedCv();
        if (text == null || text.isBlank()) {
            return cv;
        }

        // 1. Email
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            cv.email = emailMatcher.group().trim();
        }

        // 2. Phone
        Pattern phonePattern = Pattern.compile("(?i)(?:tel[eé]fono|tel|celular|phone|m[oó]vil)?[:\\s\\-]*(\\+?[0-9\\s\\-()]{7,20})");
        Matcher phoneMatcher = phonePattern.matcher(text);
        if (phoneMatcher.find()) {
            String p = phoneMatcher.group(1).trim();
            long digitCount = p.chars().filter(Character::isDigit).count();
            if (digitCount >= 7) {
                cv.phone = p;
            }
        }

        // 3. First name & Last name
        Pattern firstNamePattern = Pattern.compile("(?i)(?:nombre|first\\s?name)[:\\s]+([^\\n\\r]+)");
        Matcher firstNameMatcher = firstNamePattern.matcher(text);
        if (firstNameMatcher.find()) {
            cv.firstName = firstNameMatcher.group(1).trim();
        }

        Pattern lastNamePattern = Pattern.compile("(?i)(?:apellido|last\\s?name)[:\\s]+([^\\n\\r]+)");
        Matcher lastNameMatcher = lastNamePattern.matcher(text);
        if (lastNameMatcher.find()) {
            cv.lastName = lastNameMatcher.group(1).trim();
        }

        // Fallback for name: first non-blank line of text if name is not found
        if (cv.firstName == null || cv.firstName.isBlank()) {
            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && trimmed.length() < 50 
                        && !trimmed.toLowerCase().contains("curriculum")
                        && !trimmed.toLowerCase().contains("resume")
                        && !trimmed.toLowerCase().contains("cv")
                        && !trimmed.toLowerCase().contains("page")
                        && !trimmed.toLowerCase().contains("contacto")) {
                    String[] words = trimmed.split("\\s+");
                    if (words.length >= 2) {
                        cv.firstName = words[0];
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < words.length; i++) {
                            sb.append(words[i]).append(" ");
                        }
                        cv.lastName = sb.toString().trim();
                    } else if (words.length == 1) {
                        cv.firstName = words[0];
                    }
                    break;
                }
            }
        }

        // 4. Headline
        Pattern headlinePattern = Pattern.compile("(?i)(?:headline|titular|t[ií]tulo profesional)[:\\s]+([^\\n\\r]+)");
        Matcher headlineMatcher = headlinePattern.matcher(text);
        if (headlineMatcher.find()) {
            cv.headline = headlineMatcher.group(1).trim();
        }

        // 5. Summary
        Pattern summaryPattern = Pattern.compile("(?i)(?:resumen|extracto|summary|perfil|sobre\\s?m[ií])[:\\s]+([\\s\\S]+?)(?=\\n\\n|\\n[a-zA-Z\\s]+:|\\z)");
        Matcher summaryMatcher = summaryPattern.matcher(text);
        if (summaryMatcher.find()) {
            cv.summary = summaryMatcher.group(1).trim();
        }

        // 6. Latest Position
        Pattern positionPattern = Pattern.compile("(?i)(?:cargo|puesto|posici[oó]n|latest\\s?position|[uú]ltimo\\s?cargo)[:\\s]+([^\\n\\r]+)");
        Matcher positionMatcher = positionPattern.matcher(text);
        if (positionMatcher.find()) {
            cv.latestPosition = positionMatcher.group(1).trim();
        }

        // 7. Skills Matching
        if (hardSkillNames != null) {
            for (String skillName : hardSkillNames) {
                if (containsSkill(text, skillName)) {
                    cv.matchedHardSkills.add(skillName);
                }
            }
        }
        if (softSkillNames != null) {
            for (String skillName : softSkillNames) {
                if (containsSkill(text, skillName)) {
                    cv.matchedSoftSkills.add(skillName);
                }
            }
        }

        // 8. Education Parsing
        Pattern eduLevelPattern = Pattern.compile("(?i)(?:nivel|level)[:\\s]+(no especificado|educaci[oó]n media|t[eé]cnico|universitario|mag[ií]ster|magister|doctorado)");
        Pattern eduDegreePattern = Pattern.compile("(?i)(?:t[ií]tulo|degree|carrera)[:\\s]+([^\\n\\r]+)");
        Pattern eduInstPattern = Pattern.compile("(?i)(?:instituci[oó]n|institucion|universidad|university|instituto)[:\\s]+([^\\n\\r]+)");
        Pattern eduStartPattern = Pattern.compile("(?i)(?:desde|start\\s?date|fecha\\s?inicio)[:\\s]+([0-9\\-\\/]{4,10})");
        Pattern eduEndPattern = Pattern.compile("(?i)(?:hasta|end\\s?date|fecha\\s?fin)[:\\s]+([0-9\\-\\/]{4,10})");

        Matcher eduLevelMatcher = eduLevelPattern.matcher(text);
        Matcher eduDegreeMatcher = eduDegreePattern.matcher(text);
        Matcher eduInstMatcher = eduInstPattern.matcher(text);
        Matcher eduStartMatcher = eduStartPattern.matcher(text);
        Matcher eduEndMatcher = eduEndPattern.matcher(text);

        boolean foundEdu = false;
        ParsedEducation edu = new ParsedEducation();
        if (eduLevelMatcher.find()) {
            edu.level = eduLevelMatcher.group(1).trim();
            foundEdu = true;
        }
        if (eduDegreeMatcher.find()) {
            edu.degree = eduDegreeMatcher.group(1).trim();
            foundEdu = true;
        }
        if (eduInstMatcher.find()) {
            edu.institution = eduInstMatcher.group(1).trim();
            foundEdu = true;
        }
        if (eduStartMatcher.find()) {
            edu.startDate = parseLocalDate(eduStartMatcher.group(1).trim(), false);
            foundEdu = true;
        }
        if (eduEndMatcher.find()) {
            edu.endDate = parseLocalDate(eduEndMatcher.group(1).trim(), true);
            foundEdu = true;
        }

        if (edu.level == null) {
            String lower = text.toLowerCase();
            if (lower.contains("doctorado") || lower.contains("phd")) {
                edu.level = "Doctorado";
            } else if (lower.contains("magister") || lower.contains("mágister") || lower.contains("master") || lower.contains("msc")) {
                edu.level = "Magister";
            } else if (lower.contains("universitario") || lower.contains("universidad") || lower.contains("university")) {
                edu.level = "Universitario";
            } else if (lower.contains("tecnico") || lower.contains("técnico") || lower.contains("instituto")) {
                edu.level = "Tecnico";
            } else if (lower.contains("educacion media") || lower.contains("secundaria") || lower.contains("bachillerato")) {
                edu.level = "Educacion media";
            }
            if (edu.level != null) foundEdu = true;
        }

        if (foundEdu) {
            cv.educations.add(edu);
        }

        return cv;
    }

    public static boolean containsSkill(String text, String skillName) {
        String escaped = Pattern.quote(skillName);
        String regex;
        if (skillName.matches("^[a-zA-Z0-9\\s]+$")) {
            regex = "\\b" + escaped + "\\b";
        } else {
            regex = "(?i)(?<=^|[^a-zA-Z0-9])" + escaped + "(?=$|[^a-zA-Z0-9])";
        }
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).find();
    }

    private static LocalDate parseLocalDate(String dateStr, boolean isEndDate) {
        if (dateStr == null || dateStr.isBlank()) return null;
        dateStr = dateStr.trim();
        try {
            // YYYY-MM-DD
            if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return LocalDate.parse(dateStr);
            }
            // DD/MM/YYYY or DD-MM-YYYY
            if (dateStr.matches("^\\d{2}[-/]\\d{2}[-/]\\d{4}$")) {
                String[] parts = dateStr.split("[-/]");
                return LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
            }
            // YYYY-MM
            if (dateStr.matches("^\\d{4}-\\d{2}$")) {
                String[] parts = dateStr.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                return isEndDate ? LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth()) : LocalDate.of(year, month, 1);
            }
            // MM/YYYY
            if (dateStr.matches("^\\d{2}[-/]\\d{4}$")) {
                String[] parts = dateStr.split("[-/]");
                int year = Integer.parseInt(parts[1]);
                int month = Integer.parseInt(parts[0]);
                return isEndDate ? LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth()) : LocalDate.of(year, month, 1);
            }
            // YYYY
            if (dateStr.matches("^\\d{4}$")) {
                int year = Integer.parseInt(dateStr);
                return isEndDate ? LocalDate.of(year, 12, 31) : LocalDate.of(year, 1, 1);
            }
        } catch (Exception ex) {
            // ignore parsing errors
        }
        return null;
    }
}
