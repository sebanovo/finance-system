package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notifications.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class NotificationsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(NotificationReportFieldKey.NOTIFICATION_ID.name(), field(NotificationReportFieldKey.NOTIFICATION_ID, "Notification ID", ReportFieldType.UUID, "n.id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.USER_ID.name(), field(NotificationReportFieldKey.USER_ID, "User ID", ReportFieldType.UUID, "n.user_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.TYPE.name(), field(NotificationReportFieldKey.TYPE, "Type", ReportFieldType.ENUM, "n.type", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationType.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.CATEGORY.name(), field(NotificationReportFieldKey.CATEGORY, "Category", ReportFieldType.ENUM, "n.category", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationCategory.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.PRIORITY.name(), field(NotificationReportFieldKey.PRIORITY, "Priority", ReportFieldType.ENUM, "n.priority", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationPriority.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.TITLE.name(), field(NotificationReportFieldKey.TITLE, "Title", ReportFieldType.TEXT, "n.title", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.BODY.name(), field(NotificationReportFieldKey.BODY, "Body", ReportFieldType.TEXT, "n.body", List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.DATA.name(), field(NotificationReportFieldKey.DATA, "Data", ReportFieldType.TEXT, "n.data::text", List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.IMAGE_URL.name(), field(NotificationReportFieldKey.IMAGE_URL, "Image URL", ReportFieldType.TEXT, "n.image_url", List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.ACTION_URL.name(), field(NotificationReportFieldKey.ACTION_URL, "Action URL", ReportFieldType.TEXT, "n.action_url", List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.READ_AT.name(), field(NotificationReportFieldKey.READ_AT, "Read at", ReportFieldType.DATETIME, "n.read_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.OPENED_AT.name(), field(NotificationReportFieldKey.OPENED_AT, "Opened at", ReportFieldType.DATETIME, "n.opened_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.ARCHIVED_AT.name(), field(NotificationReportFieldKey.ARCHIVED_AT, "Archived at", ReportFieldType.DATETIME, "n.archived_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.EXPIRES_AT.name(), field(NotificationReportFieldKey.EXPIRES_AT, "Expires at", ReportFieldType.DATETIME, "n.expires_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.CREATED_AT.name(), field(NotificationReportFieldKey.CREATED_AT, "Created at", ReportFieldType.DATETIME, "n.created_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.UPDATED_AT.name(), field(NotificationReportFieldKey.UPDATED_AT, "Updated at", ReportFieldType.DATETIME, "n.updated_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationReportFieldKey.IS_READ.name(), field(NotificationReportFieldKey.IS_READ, "Is read", ReportFieldType.BOOLEAN, "n.read_at IS NOT NULL", List.of(ReportOperator.EQ), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.IS_OPENED.name(), field(NotificationReportFieldKey.IS_OPENED, "Is opened", ReportFieldType.BOOLEAN, "n.opened_at IS NOT NULL", List.of(ReportOperator.EQ), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.IS_ARCHIVED.name(), field(NotificationReportFieldKey.IS_ARCHIVED, "Is archived", ReportFieldType.BOOLEAN, "n.archived_at IS NOT NULL", List.of(ReportOperator.EQ), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationReportFieldKey.IS_EXPIRED.name(), field(NotificationReportFieldKey.IS_EXPIRED, "Is expired", ReportFieldType.BOOLEAN, "n.expires_at IS NOT NULL AND n.expires_at < NOW()", List.of(ReportOperator.EQ), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(NotificationReportMetricKey.TOTAL_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.TOTAL_NOTIFICATIONS, "Total notifications", ReportFieldType.NUMBER, "COUNT(n.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationReportMetricKey.READ_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.READ_NOTIFICATIONS, "Read notifications", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE n.read_at IS NOT NULL)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationReportMetricKey.UNREAD_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.UNREAD_NOTIFICATIONS, "Unread notifications", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE n.read_at IS NULL)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationReportMetricKey.OPENED_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.OPENED_NOTIFICATIONS, "Opened notifications", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE n.opened_at IS NOT NULL)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationReportMetricKey.ARCHIVED_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.ARCHIVED_NOTIFICATIONS, "Archived notifications", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE n.archived_at IS NOT NULL)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationReportMetricKey.EXPIRED_NOTIFICATIONS.name(), metric(NotificationReportMetricKey.EXPIRED_NOTIFICATIONS, "Expired notifications", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE n.expires_at IS NOT NULL AND n.expires_at < NOW())", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        NotificationReportColumnKey.CREATED_AT,
                        NotificationReportColumnKey.TYPE,
                        NotificationReportColumnKey.CATEGORY,
                        NotificationReportColumnKey.PRIORITY,
                        NotificationReportColumnKey.TITLE,
                        NotificationReportColumnKey.USER_ID,
                        NotificationReportColumnKey.IS_READ,
                        NotificationReportColumnKey.READ_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, NotificationReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        NotificationReportGroupByKey.TYPE,
                        NotificationReportGroupByKey.CATEGORY,
                        NotificationReportGroupByKey.PRIORITY,
                        NotificationReportGroupByKey.IS_READ
                ),
                List.of(
                        NotificationReportMetricKey.TOTAL_NOTIFICATIONS,
                        NotificationReportMetricKey.READ_NOTIFICATIONS,
                        NotificationReportMetricKey.UNREAD_NOTIFICATIONS,
                        NotificationReportMetricKey.OPENED_NOTIFICATIONS,
                        NotificationReportMetricKey.ARCHIVED_NOTIFICATIONS,
                        NotificationReportMetricKey.EXPIRED_NOTIFICATIONS
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, NotificationReportMetricKey.TOTAL_NOTIFICATIONS, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.NOTIFICATIONS,
                "Notificaciones",
                "Permite consultar y consolidar notificaciones emitidas a los usuarios.",
                new ReportRoot("notification", "tenant_notifications", "n"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(5000, 20, 5, 6)
        );
    }

    private static ReportField field(Enum<?> key, String label, ReportFieldType type, String sqlExpression, List<ReportOperator> operators, List<Enum<?>> options, String relationKey, Set<ReportMode> allowedModes, boolean filterable, boolean columnable, boolean groupable, boolean sortable) {
        return new ReportField(key, label, type, sqlExpression, operators, options, relationKey, allowedModes, filterable, columnable, groupable, sortable);
    }

    private static ReportMetric metric(Enum<?> key, String label, ReportFieldType type, String sqlExpression, Set<ReportMode> allowedModes) {
        return new ReportMetric(key, label, type, sqlExpression, allowedModes);
    }

    private static List<Enum<?>> enumOptions(Enum<?>[] values) {
        return List.of(values);
    }
}
