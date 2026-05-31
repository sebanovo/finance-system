package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuditEventsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(AuditEventReportFieldKey.AUDIT_EVENT_ID.name(), field(AuditEventReportFieldKey.AUDIT_EVENT_ID, "ID del evento", ReportFieldType.UUID, "ae.id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.ACTOR_SUBJECT.name(), field(AuditEventReportFieldKey.ACTOR_SUBJECT, "Actor", ReportFieldType.TEXT, "ae.actor_subject", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.ACTOR_ID.name(), field(AuditEventReportFieldKey.ACTOR_ID, "Actor ID", ReportFieldType.UUID, "ae.actor_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.ACTOR_EMAIL.name(), field(AuditEventReportFieldKey.ACTOR_EMAIL, "Correo del actor", ReportFieldType.TEXT, "ae.actor_email", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.TENANT_SLUG.name(), field(AuditEventReportFieldKey.TENANT_SLUG, "Tenant", ReportFieldType.TEXT, "ae.tenant_slug", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.EVENT_TYPE.name(), field(AuditEventReportFieldKey.EVENT_TYPE, "Tipo de evento", ReportFieldType.TEXT, "ae.event_type", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.RESOURCE_TYPE.name(), field(AuditEventReportFieldKey.RESOURCE_TYPE, "Tipo de recurso", ReportFieldType.TEXT, "ae.resource_type", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.RESOURCE_ID.name(), field(AuditEventReportFieldKey.RESOURCE_ID, "ID de recurso", ReportFieldType.TEXT, "ae.resource_id", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.EVENT_DETAILS.name(), field(AuditEventReportFieldKey.EVENT_DETAILS, "Detalles", ReportFieldType.TEXT, "ae.event_details", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.IP_ADDRESS.name(), field(AuditEventReportFieldKey.IP_ADDRESS, "IP", ReportFieldType.TEXT, "ae.ip_address::text", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.USER_AGENT.name(), field(AuditEventReportFieldKey.USER_AGENT, "User agent", ReportFieldType.TEXT, "ae.user_agent", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.REQUEST_ID.name(), field(AuditEventReportFieldKey.REQUEST_ID, "Request ID", ReportFieldType.TEXT, "ae.request_id", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.CORRELATION_ID.name(), field(AuditEventReportFieldKey.CORRELATION_ID, "Correlation ID", ReportFieldType.TEXT, "ae.correlation_id", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(AuditEventReportFieldKey.SOURCE.name(), field(AuditEventReportFieldKey.SOURCE, "Origen", ReportFieldType.TEXT, "ae.source", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.OUTCOME.name(), field(AuditEventReportFieldKey.OUTCOME, "Resultado", ReportFieldType.TEXT, "ae.outcome", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(AuditEventReportFieldKey.CREATED_AT.name(), field(AuditEventReportFieldKey.CREATED_AT, "Fecha de creación", ReportFieldType.DATETIME, "ae.created_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(AuditEventReportMetricKey.TOTAL_EVENTS.name(), metric(AuditEventReportMetricKey.TOTAL_EVENTS, "Total events", ReportFieldType.NUMBER, "COUNT(ae.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(AuditEventReportMetricKey.SUCCESS_EVENTS.name(), metric(AuditEventReportMetricKey.SUCCESS_EVENTS, "Success events", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE ae.outcome = 'SUCCESS')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(AuditEventReportMetricKey.FAILED_EVENTS.name(), metric(AuditEventReportMetricKey.FAILED_EVENTS, "Failed events", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE ae.outcome <> 'SUCCESS')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(AuditEventReportMetricKey.DISTINCT_ACTORS.name(), metric(AuditEventReportMetricKey.DISTINCT_ACTORS, "Distinct actors", ReportFieldType.NUMBER, "COUNT(DISTINCT ae.actor_subject)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(AuditEventReportMetricKey.DISTINCT_RESOURCES.name(), metric(AuditEventReportMetricKey.DISTINCT_RESOURCES, "Distinct resources", ReportFieldType.NUMBER, "COUNT(DISTINCT COALESCE(ae.resource_type, '') || ':' || COALESCE(ae.resource_id, ''))", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        AuditEventReportColumnKey.CREATED_AT,
                        AuditEventReportColumnKey.EVENT_TYPE,
                        AuditEventReportColumnKey.OUTCOME,
                        AuditEventReportColumnKey.ACTOR_SUBJECT,
                        AuditEventReportColumnKey.RESOURCE_TYPE,
                        AuditEventReportColumnKey.RESOURCE_ID,
                        AuditEventReportColumnKey.SOURCE,
                        AuditEventReportColumnKey.TENANT_SLUG
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, AuditEventReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        AuditEventReportGroupByKey.EVENT_TYPE,
                        AuditEventReportGroupByKey.RESOURCE_TYPE,
                        AuditEventReportGroupByKey.OUTCOME,
                        AuditEventReportGroupByKey.SOURCE
                ),
                List.of(
                        AuditEventReportMetricKey.TOTAL_EVENTS,
                        AuditEventReportMetricKey.SUCCESS_EVENTS,
                        AuditEventReportMetricKey.FAILED_EVENTS,
                        AuditEventReportMetricKey.DISTINCT_ACTORS,
                        AuditEventReportMetricKey.DISTINCT_RESOURCES
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, AuditEventReportMetricKey.TOTAL_EVENTS, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.AUDIT_EVENTS,
                "Eventos de auditoría",
                "Permite consultar y consolidar eventos de auditoría del sistema.",
                new ReportRoot("audit_event", "tenant_audit_events", "ae"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(5000, 16, 5, 5)
        );
    }

    private static ReportField field(Enum<?> key, String label, ReportFieldType type, String sqlExpression, List<ReportOperator> operators, List<Enum<?>> options, String relationKey, Set<ReportMode> allowedModes, boolean filterable, boolean columnable, boolean groupable, boolean sortable) {
        return new ReportField(key, label, type, sqlExpression, operators, options, relationKey, allowedModes, filterable, columnable, groupable, sortable);
    }

    private static ReportMetric metric(Enum<?> key, String label, ReportFieldType type, String sqlExpression, Set<ReportMode> allowedModes) {
        return new ReportMetric(key, label, type, sqlExpression, allowedModes);
    }
}
