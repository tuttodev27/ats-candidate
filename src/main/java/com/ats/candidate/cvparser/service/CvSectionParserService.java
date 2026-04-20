package com.ats.candidate.cvparser.service;

import com.ats.candidate.cvparser.dto.ParsedAttachmentDto;
import com.ats.candidate.cvparser.dto.ParsedCertificationDto;
import com.ats.candidate.cvparser.dto.ParsedCvDto;
import com.ats.candidate.cvparser.dto.ParsedExperienceDto;
import com.ats.candidate.cvparser.dto.ParsedLanguageDto;
import com.ats.candidate.cvparser.dto.ParsedPersonalDataDto;
import com.ats.candidate.cvparser.dto.ParsedProfessionalProfileDto;
import com.ats.candidate.cvparser.dto.ParsedStudyDto;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvSectionParserService {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+");
	private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d[\\d\\s()-]{7,}\\d)");
	private static final Pattern YEARS_EXPERIENCE_PATTERN = Pattern.compile("(\\d+)\\+?\\s*(?:anos|año|anios|years)");
	private static final Pattern YEAR_PATTERN = Pattern.compile("(19|20)\\d{2}");
	private static final Pattern CERTIFICATION_PATTERN = Pattern.compile("(.+?)\\s*\\((\\d{4})\\)");
	private static final Pattern MONTH_RANGE_PATTERN = Pattern.compile(
		"(?i)([A-Za-zÁÉÍÓÚáéíóú]+)\\s+(\\d{4})\\s*[–-]\\s*([A-Za-zÁÉÍÓÚáéíóú]+|Actualidad|Actual|Present)\\s*(\\d{4})?"
	);

	private static final Set<String> SECTION_TITLES = Set.of(
		"perfil profesional",
		"habilidades",
		"experiencia laboral",
		"educacion",
		"educación",
		"certificaciones",
		"idiomas"
	);

	public ParsedCvDto parse(String rawText, String fileName, String contentType) {
		List<String> lines = Arrays.stream(rawText.split("\\R"))
			.map(String::trim)
			.filter(line -> !line.isBlank())
			.toList();

		Map<String, List<String>> sections = splitSections(lines);
		ParsedPersonalDataDto personalData = parsePersonalData(lines);
		List<ParsedExperienceDto> experiences = parseExperiences(sections.getOrDefault("experiencia laboral", List.of()));
		ParsedProfessionalProfileDto professionalProfile = parseProfessionalProfile(
			lines,
			sections.getOrDefault("perfil profesional", List.of()),
			experiences
		);

		return new ParsedCvDto(
			personalData,
			professionalProfile,
			parseStudies(sections.getOrDefault("educacion", List.of())),
			parseCertifications(sections.getOrDefault("certificaciones", List.of())),
			experiences,
			parseHardSkills(sections.getOrDefault("habilidades", List.of())),
			parseSoftSkills(sections.getOrDefault("habilidades", List.of())),
			parseLanguages(sections.getOrDefault("idiomas", List.of())),
			new ParsedAttachmentDto(fileName, contentType)
		);
	}

	private Map<String, List<String>> splitSections(List<String> lines) {
		Map<String, List<String>> sections = new LinkedHashMap<>();
		String currentSection = "header";
		sections.put(currentSection, new ArrayList<>());

		for (String line : lines) {
			String normalized = normalize(line);
			if (SECTION_TITLES.contains(normalized)) {
				currentSection = normalized.equals("educación") ? "educacion" : normalized;
				sections.putIfAbsent(currentSection, new ArrayList<>());
				continue;
			}
			sections.get(currentSection).add(line);
		}

		return sections;
	}

	private ParsedPersonalDataDto parsePersonalData(List<String> lines) {
		String fullName = lines.size() > 1 ? lines.get(1) : null;
		String email = findFirstMatch(lines, EMAIL_PATTERN);
		String phone = findFirstMatch(lines, PHONE_PATTERN);
		String location = lines.stream()
			.filter(line -> line.contains("Chile"))
			.findFirst()
			.orElse(null);

		List<String> handleCandidates = lines.stream()
			.filter(line -> !line.equals(fullName))
			.filter(line -> !line.equals(email))
			.filter(line -> !line.equals(phone))
			.filter(line -> !line.equals(location))
			.filter(line -> !SECTION_TITLES.contains(normalize(line)))
			.filter(line -> !line.startsWith("Last updated"))
			.filter(line -> !line.contains("|"))
			.toList();

		String linkedin = handleCandidates.size() > 1 ? handleCandidates.get(1) : null;
		String github = handleCandidates.size() > 2 ? handleCandidates.get(2) : null;

		String firstName = null;
		String lastName = null;
		if (fullName != null) {
			String[] parts = fullName.trim().split("\\s+");
			if (parts.length >= 2) {
				firstName = String.join(" ", Arrays.copyOf(parts, Math.max(1, parts.length - 2)));
				lastName = String.join(" ", Arrays.copyOfRange(parts, Math.max(1, parts.length - 2), parts.length));
			} else {
				firstName = fullName;
			}
		}

		return new ParsedPersonalDataDto(
			firstName,
			lastName,
			email,
			phone,
			null,
			null,
			location,
			linkedin,
			github
		);
	}

	private ParsedProfessionalProfileDto parseProfessionalProfile(
		List<String> lines,
		List<String> sectionLines,
		List<ParsedExperienceDto> experiences
	) {
		String headline = lines.size() > 2 ? lines.get(2) : null;
		String summary = String.join(" ", sectionLines).trim();
		Integer yearsExperience = extractYearsExperience(summary);
		String latestPosition = experiences.isEmpty() ? null : experiences.getFirst().jobTitle();

		return new ParsedProfessionalProfileDto(headline, latestPosition, yearsExperience, emptyToNull(summary));
	}

	private Integer extractYearsExperience(String summary) {
		if (summary == null) {
			return null;
		}
		Matcher matcher = YEARS_EXPERIENCE_PATTERN.matcher(normalize(summary));
		return matcher.find() ? Integer.parseInt(matcher.group(1)) : null;
	}

	private List<String> parseHardSkills(List<String> lines) {
		Set<String> skills = new LinkedHashSet<>();
		for (String line : lines) {
			if (!line.contains(":")) {
				continue;
			}
			String normalized = normalize(line);
			if (normalized.startsWith("lenguajes:")
				|| normalized.startsWith("frameworks:")
				|| normalized.startsWith("arquitectura:")
				|| normalized.startsWith("devops & cloud:")
				|| normalized.startsWith("bases de datos y mensajeria:")
				|| normalized.startsWith("testing & herramientas:")
				|| normalized.startsWith("observabilidad:")) {
				String values = line.substring(line.indexOf(':') + 1);
				Arrays.stream(values.split(","))
					.map(String::trim)
					.filter(value -> !value.isBlank())
					.forEach(skills::add);
			}
		}
		return List.copyOf(skills);
	}

	private List<String> parseSoftSkills(List<String> lines) {
		List<String> softSkillKeywords = List.of(
			"liderazgo",
			"comunicacion",
			"trabajo en equipo",
			"resolucion de problemas",
			"colaboracion",
			"mentoria"
		);
		Set<String> result = new LinkedHashSet<>();
		for (String line : lines) {
			String normalized = normalize(line);
			for (String keyword : softSkillKeywords) {
				if (normalized.contains(keyword)) {
					result.add(keyword);
				}
			}
		}
		return List.copyOf(result);
	}

	private List<ParsedLanguageDto> parseLanguages(List<String> lines) {
		List<ParsedLanguageDto> languages = new ArrayList<>();
		for (String line : lines) {
			String cleaned = line.replaceFirst("^•\\s*", "").trim();
			String[] parts = cleaned.split("\\s*-\\s*", 2);
			if (parts.length == 2) {
				languages.add(new ParsedLanguageDto(parts[0].trim(), parts[1].trim()));
			}
		}
		return languages;
	}

	private List<ParsedCertificationDto> parseCertifications(List<String> lines) {
		List<ParsedCertificationDto> certifications = new ArrayList<>();
		for (String line : lines) {
			String cleaned = line.replaceFirst("^•\\s*", "");
			for (String piece : cleaned.split(",")) {
				Matcher matcher = CERTIFICATION_PATTERN.matcher(piece.trim());
				if (matcher.find()) {
					certifications.add(new ParsedCertificationDto(
						matcher.group(1).trim(),
						Integer.parseInt(matcher.group(2))
					));
				}
			}
		}
		return certifications;
	}

	private List<ParsedStudyDto> parseStudies(List<String> lines) {
		List<ParsedStudyDto> studies = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (!line.contains(",")) {
				continue;
			}

			String[] header = line.split(",", 2);
			String institution = header[0].trim();
			String degree = header[1].trim();
			LocalDate startDate = null;
			LocalDate endDate = null;

			if (i + 1 < lines.size()) {
				Optional<DateRange> range = parseDateRange(lines.get(i + 1));
				if (range.isPresent()) {
					startDate = range.get().startDate();
					endDate = range.get().endDate();
					i++;
				}
			}

			studies.add(new ParsedStudyDto(
				inferEducationLevel(degree),
				degree,
				institution,
				startDate,
				endDate
			));
		}
		return studies;
	}

	private List<ParsedExperienceDto> parseExperiences(List<String> lines) {
		List<ParsedExperienceDto> experiences = new ArrayList<>();
		int index = 0;
		while (index < lines.size()) {
			String line = lines.get(index);
			if (!line.contains(",")) {
				index++;
				continue;
			}

			String[] employerInfo = line.split(",", 2);
			String companyName = employerInfo[0].trim();
			String jobTitle = employerInfo[1].trim();
			String client = safeGet(lines, index + 1);
			String project = safeGet(lines, index + 2);

			List<String> description = new ArrayList<>();
			List<String> technologies = new ArrayList<>();
			String location = null;
			LocalDate startDate = null;
			LocalDate endDate = null;
			boolean currentJob = false;

			index += 3;
			while (index < lines.size()) {
				String currentLine = lines.get(index);
				if (currentLine.contains(",") && !currentLine.startsWith("•")) {
					break;
				}

				if (currentLine.startsWith("•")) {
					String bullet = currentLine.replaceFirst("^•\\s*", "").trim();
					if (normalize(bullet).startsWith("tecnologias:")) {
						String techValues = bullet.substring(bullet.indexOf(':') + 1);
						Arrays.stream(techValues.split(","))
							.map(String::trim)
							.filter(value -> !value.isBlank())
							.forEach(technologies::add);
					} else {
						description.add(bullet);
					}
				} else if (currentLine.contains("Chile")) {
					location = currentLine.trim();
				} else {
					Optional<DateRange> range = parseDateRange(currentLine);
					if (range.isPresent()) {
						startDate = range.get().startDate();
						endDate = range.get().endDate();
						currentJob = range.get().current();
					}
				}
				index++;
			}

			experiences.add(new ParsedExperienceDto(
				companyName,
				client,
				project,
				jobTitle,
				location,
				startDate,
				endDate,
				currentJob,
				List.copyOf(description),
				List.copyOf(technologies)
			));
		}
		return experiences;
	}

	private Optional<DateRange> parseDateRange(String value) {
		Matcher matcher = MONTH_RANGE_PATTERN.matcher(value);
		if (!matcher.find()) {
			return Optional.empty();
		}

		String startMonthName = matcher.group(1);
		int startYear = Integer.parseInt(matcher.group(2));
		String endMonthName = matcher.group(3);
		String endYearValue = matcher.group(4);

		LocalDate startDate = YearMonth.of(startYear, monthFromSpanish(startMonthName)).atDay(1);
		if (isCurrentToken(endMonthName)) {
			return Optional.of(new DateRange(startDate, null, true));
		}

		int endYear = Integer.parseInt(endYearValue);
		LocalDate endDate = YearMonth.of(endYear, monthFromSpanish(endMonthName)).atEndOfMonth();
		return Optional.of(new DateRange(startDate, endDate, false));
	}

	private int monthFromSpanish(String rawMonth) {
		String month = normalize(rawMonth);
		return switch (month) {
			case "enero", "january", "jan" -> Month.JANUARY.getValue();
			case "febrero", "february", "feb" -> Month.FEBRUARY.getValue();
			case "marzo", "march", "mar" -> Month.MARCH.getValue();
			case "abril", "april", "apr" -> Month.APRIL.getValue();
			case "mayo", "may" -> Month.MAY.getValue();
			case "junio", "june", "jun" -> Month.JUNE.getValue();
			case "julio", "july", "jul" -> Month.JULY.getValue();
			case "agosto", "august", "aug" -> Month.AUGUST.getValue();
			case "septiembre", "setiembre", "september", "sept", "sep" -> Month.SEPTEMBER.getValue();
			case "octubre", "october", "oct" -> Month.OCTOBER.getValue();
			case "noviembre", "november", "nov" -> Month.NOVEMBER.getValue();
			case "diciembre", "december", "dec" -> Month.DECEMBER.getValue();
			default -> throw new IllegalArgumentException("Mes no soportado: " + rawMonth);
		};
	}

	private boolean isCurrentToken(String value) {
		String normalized = normalize(value);
		return normalized.equals("actualidad") || normalized.equals("actual") || normalized.equals("present");
	}

	private String inferEducationLevel(String degree) {
		String normalized = normalize(degree);
		if (normalized.contains("magister") || normalized.contains("master")) {
			return "Magister";
		}
		if (normalized.contains("doctor")) {
			return "Doctorado";
		}
		if (normalized.contains("tecnico")) {
			return "Tecnico";
		}
		if (normalized.contains("ingenieria") || normalized.contains("licenciatura")) {
			return "Universitario";
		}
		return "No especificado";
	}

	private String findFirstMatch(List<String> lines, Pattern pattern) {
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return null;
	}

	private String safeGet(List<String> lines, int index) {
		return index < lines.size() ? lines.get(index) : null;
	}

	private String normalize(String value) {
		String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "");
		return normalized.toLowerCase(Locale.ROOT).trim();
	}

	private String emptyToNull(String value) {
		return value == null || value.isBlank() ? null : value;
	}

	private record DateRange(LocalDate startDate, LocalDate endDate, boolean current) {
	}
}
