package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ReportFileNameGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
            .withLocale(Locale.ROOT)
            .withZone(ZoneOffset.UTC);

    public String generate(ReportType reportType, ReportMode mode, ReportOutput output, Instant instant) {
        return reportType.name().toLowerCase(Locale.ROOT)
                + "-"
                + mode.name().toLowerCase(Locale.ROOT)
                + "-"
                + FORMATTER.format(instant)
                + "."
                + extension(output);
    }

    private String extension(ReportOutput output) {
        return switch (output) {
            case SCREEN -> "txt";
            case PDF -> "pdf";
            case XLSX -> "xlsx";
        };
    }
}
