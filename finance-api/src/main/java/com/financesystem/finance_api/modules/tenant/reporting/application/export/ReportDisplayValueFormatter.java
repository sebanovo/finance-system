package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ReportDisplayValueFormatter {

    private static final ZoneId BOLIVIA_ZONE = ZoneId.of("America/La_Paz");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern ISO_DATE_TIME_PATTERN = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?"
    );

    String formatCellValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
        if (value instanceof Timestamp timestamp) {
            return formatInstant(timestamp.toInstant());
        }
        if (value instanceof Instant instant) {
            return formatInstant(instant);
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return formatOffsetDateTime(offsetDateTime);
        }
        if (value instanceof ZonedDateTime zonedDateTime) {
            return formatZonedDateTime(zonedDateTime);
        }
        if (value instanceof LocalDateTime localDateTime) {
            return DATE_TIME_FORMATTER.format(localDateTime.atZone(BOLIVIA_ZONE));
        }
        if (value instanceof LocalDate localDate) {
            return DATE_FORMATTER.format(localDate);
        }
        if (value instanceof java.util.Date date) {
            return formatInstant(date.toInstant());
        }
        if (value instanceof JsonNode jsonNode) {
            return formatJsonNode(jsonNode);
        }
        if (value instanceof String stringValue) {
            return formatText(stringValue);
        }
        return String.valueOf(value);
    }

    String formatFilterText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        Matcher matcher = ISO_DATE_TIME_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(formatIsoLike(matcher.group())));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String formatText(String text) {
        String trimmed = text.trim();
        String formatted = formatIsoLike(trimmed);
        return formatted != null ? formatted : text;
    }

    private String formatJsonNode(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return "";
        }
        if (jsonNode.isTextual()) {
            return formatText(jsonNode.asText());
        }
        if (jsonNode.isNumber() || jsonNode.isBoolean()) {
            return jsonNode.asText();
        }
        return jsonNode.toString();
    }

    private String formatInstant(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant.atZone(BOLIVIA_ZONE));
    }

    private String formatOffsetDateTime(OffsetDateTime offsetDateTime) {
        return DATE_TIME_FORMATTER.format(offsetDateTime.atZoneSameInstant(BOLIVIA_ZONE));
    }

    private String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        return DATE_TIME_FORMATTER.format(zonedDateTime.withZoneSameInstant(BOLIVIA_ZONE));
    }

    private String formatIsoLike(String text) {
        try {
            return formatInstant(Instant.parse(text));
        } catch (Exception ignored) {
        }

        try {
            return formatOffsetDateTime(OffsetDateTime.parse(text));
        } catch (Exception ignored) {
        }

        try {
            return DATE_TIME_FORMATTER.format(LocalDateTime.parse(text).atZone(BOLIVIA_ZONE));
        } catch (Exception ignored) {
        }

        return null;
    }
}
