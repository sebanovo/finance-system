package com.financesystem.finance_api.modules.tenant.reporting.application.header;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportHeaderResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOperator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReportHeaderBuilder {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;

    public ReportHeaderBuilder(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
    }

    public ReportHeaderResponse build(
            String title,
            ReportMode mode,
            Instant generatedAt,
            List<String> appliedFilters,
            List<String> selectedColumns,
            List<String> selectedMetrics,
            List<String> selectedGroupBy
    ) {
        return new ReportHeaderResponse(
                title,
                mode,
                modeLabel(mode),
                generatedAt,
                resolveGeneratedBy(),
                appliedFilters == null ? List.of() : List.copyOf(appliedFilters),
                selectedColumns == null ? List.of() : List.copyOf(selectedColumns),
                selectedMetrics == null ? List.of() : List.copyOf(selectedMetrics),
                selectedGroupBy == null ? List.of() : List.copyOf(selectedGroupBy)
        );
    }

    public List<String> summarizeFilters(List<? extends Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        for (Object filter : filters) {
            if (filter == null) {
                continue;
            }
            result.add(filter.toString());
        }
        return result;
    }

    public String describeFilter(String label, ReportOperator operator, JsonNode value, JsonNode to) {
        StringBuilder builder = new StringBuilder(label).append(": ").append(operatorLabel(operator)).append(" ");
        if (value != null) {
            builder.append(formatValue(value));
        }
        if (to != null && !to.isNull()) {
            builder.append(" y ").append(formatValue(to));
        }
        return builder.toString();
    }

    private String formatValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return "null";
        }
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            node.forEach(item -> values.add(formatValue(item)));
            return values.toString();
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isBoolean() || node.isNumber()) {
            return node.asText();
        }
        return node.toString();
    }

    private String resolveGeneratedBy() {
        String displayName = securityContextFacade.getCurrentDisplayName();
        if (StringUtils.hasText(displayName)) {
            return displayName;
        }

        String subject = securityContextFacade.getCurrentSubject();
        String resolvedFromSubject = resolveTenantUserName(subject);
        if (StringUtils.hasText(resolvedFromSubject)) {
            return resolvedFromSubject;
        }

        return StringUtils.hasText(subject) ? subject : "SYSTEM";
    }

    private String resolveTenantUserName(String subject) {
        if (!StringUtils.hasText(subject)) {
            return null;
        }

        UUID userId;
        try {
            userId = UUID.fromString(subject.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }

        return tenantUserRepository.findById(userId)
                .map(this::fullName)
                .filter(StringUtils::hasText)
                .orElse(null);
    }

    private String fullName(TenantUser tenantUser) {
        if (tenantUser == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(tenantUser.firstName())) {
            builder.append(tenantUser.firstName().trim());
        }
        if (StringUtils.hasText(tenantUser.lastName())) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(tenantUser.lastName().trim());
        }

        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }

    private String modeLabel(ReportMode mode) {
        return switch (mode) {
            case ANALYTIC -> "Analítico";
            case MANAGERIAL -> "Gerencial";
        };
    }

    private String operatorLabel(ReportOperator operator) {
        if (operator == null) {
            return "";
        }

        return switch (operator) {
            case EQ -> "es";
            case IN -> "está en";
            case CONTAINS -> "contiene";
            case STARTS_WITH -> "empieza con";
            case ENDS_WITH -> "termina con";
            case GT -> "es mayor que";
            case GTE -> "es mayor o igual que";
            case LT -> "es menor que";
            case LTE -> "es menor o igual que";
            case BETWEEN -> "está entre";
        };
    }
}
