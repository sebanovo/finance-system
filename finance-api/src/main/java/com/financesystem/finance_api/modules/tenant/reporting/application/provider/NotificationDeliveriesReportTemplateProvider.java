package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationChannel;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationDeliveryStatus;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdeliveries.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class NotificationDeliveriesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(NotificationDeliveryReportFieldKey.DELIVERY_ID.name(), field(NotificationDeliveryReportFieldKey.DELIVERY_ID, "Delivery ID", ReportFieldType.UUID, "d.id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.NOTIFICATION_ID.name(), field(NotificationDeliveryReportFieldKey.NOTIFICATION_ID, "Notification ID", ReportFieldType.UUID, "d.notification_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.USER_ID.name(), field(NotificationDeliveryReportFieldKey.USER_ID, "User ID", ReportFieldType.UUID, "n.user_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), "notification", Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.TYPE.name(), field(NotificationDeliveryReportFieldKey.TYPE, "Notification type", ReportFieldType.ENUM, "n.type", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationType.values()), "notification", Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.CATEGORY.name(), field(NotificationDeliveryReportFieldKey.CATEGORY, "Category", ReportFieldType.ENUM, "n.category", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory.values()), "notification", Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.PRIORITY.name(), field(NotificationDeliveryReportFieldKey.PRIORITY, "Priority", ReportFieldType.ENUM, "n.priority", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationPriority.values()), "notification", Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.DEVICE_ID.name(), field(NotificationDeliveryReportFieldKey.DEVICE_ID, "Device ID", ReportFieldType.UUID, "d.device_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.CHANNEL.name(), field(NotificationDeliveryReportFieldKey.CHANNEL, "Channel", ReportFieldType.ENUM, "d.channel", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationChannel.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.PROVIDER.name(), field(NotificationDeliveryReportFieldKey.PROVIDER, "Provider", ReportFieldType.TEXT, "d.provider", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.STATUS.name(), field(NotificationDeliveryReportFieldKey.STATUS, "Status", ReportFieldType.ENUM, "d.status", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationDeliveryStatus.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeliveryReportFieldKey.PROVIDER_MESSAGE_ID.name(), field(NotificationDeliveryReportFieldKey.PROVIDER_MESSAGE_ID, "Provider message ID", ReportFieldType.TEXT, "d.provider_message_id", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.ERROR_CODE.name(), field(NotificationDeliveryReportFieldKey.ERROR_CODE, "Error code", ReportFieldType.TEXT, "d.error_code", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.ERROR_MESSAGE.name(), field(NotificationDeliveryReportFieldKey.ERROR_MESSAGE, "Error message", ReportFieldType.TEXT, "d.error_message", List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.ATTEMPTED_AT.name(), field(NotificationDeliveryReportFieldKey.ATTEMPTED_AT, "Attempted at", ReportFieldType.DATETIME, "d.attempted_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.SENT_AT.name(), field(NotificationDeliveryReportFieldKey.SENT_AT, "Sent at", ReportFieldType.DATETIME, "d.sent_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.FAILED_AT.name(), field(NotificationDeliveryReportFieldKey.FAILED_AT, "Failed at", ReportFieldType.DATETIME, "d.failed_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.CREATED_AT.name(), field(NotificationDeliveryReportFieldKey.CREATED_AT, "Created at", ReportFieldType.DATETIME, "d.created_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeliveryReportFieldKey.UPDATED_AT.name(), field(NotificationDeliveryReportFieldKey.UPDATED_AT, "Updated at", ReportFieldType.DATETIME, "d.updated_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(NotificationDeliveryReportMetricKey.TOTAL_DELIVERIES.name(), metric(NotificationDeliveryReportMetricKey.TOTAL_DELIVERIES, "Total deliveries", ReportFieldType.NUMBER, "COUNT(d.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeliveryReportMetricKey.SENT_DELIVERIES.name(), metric(NotificationDeliveryReportMetricKey.SENT_DELIVERIES, "Sent deliveries", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE d.status = 'SENT')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeliveryReportMetricKey.FAILED_DELIVERIES.name(), metric(NotificationDeliveryReportMetricKey.FAILED_DELIVERIES, "Failed deliveries", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE d.status = 'FAILED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeliveryReportMetricKey.SKIPPED_DELIVERIES.name(), metric(NotificationDeliveryReportMetricKey.SKIPPED_DELIVERIES, "Skipped deliveries", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE d.status = 'SKIPPED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeliveryReportMetricKey.EXPIRED_DELIVERIES.name(), metric(NotificationDeliveryReportMetricKey.EXPIRED_DELIVERIES, "Expired deliveries", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE d.status = 'EXPIRED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeliveryReportMetricKey.DISTINCT_NOTIFICATIONS.name(), metric(NotificationDeliveryReportMetricKey.DISTINCT_NOTIFICATIONS, "Distinct notifications", ReportFieldType.NUMBER, "COUNT(DISTINCT d.notification_id)", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        NotificationDeliveryReportColumnKey.CREATED_AT,
                        NotificationDeliveryReportColumnKey.SENT_AT,
                        NotificationDeliveryReportColumnKey.FAILED_AT,
                        NotificationDeliveryReportColumnKey.TYPE,
                        NotificationDeliveryReportColumnKey.CATEGORY,
                        NotificationDeliveryReportColumnKey.PRIORITY,
                        NotificationDeliveryReportColumnKey.CHANNEL,
                        NotificationDeliveryReportColumnKey.PROVIDER,
                        NotificationDeliveryReportColumnKey.STATUS
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, NotificationDeliveryReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        NotificationDeliveryReportGroupByKey.TYPE,
                        NotificationDeliveryReportGroupByKey.CATEGORY,
                        NotificationDeliveryReportGroupByKey.PRIORITY,
                        NotificationDeliveryReportGroupByKey.CHANNEL,
                        NotificationDeliveryReportGroupByKey.PROVIDER,
                        NotificationDeliveryReportGroupByKey.STATUS
                ),
                List.of(
                        NotificationDeliveryReportMetricKey.TOTAL_DELIVERIES,
                        NotificationDeliveryReportMetricKey.SENT_DELIVERIES,
                        NotificationDeliveryReportMetricKey.FAILED_DELIVERIES,
                        NotificationDeliveryReportMetricKey.SKIPPED_DELIVERIES,
                        NotificationDeliveryReportMetricKey.EXPIRED_DELIVERIES,
                        NotificationDeliveryReportMetricKey.DISTINCT_NOTIFICATIONS
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, NotificationDeliveryReportMetricKey.TOTAL_DELIVERIES, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.NOTIFICATION_DELIVERIES,
                "Entregas de notificaciones",
                "Permite consultar y consolidar entregas de notificaciones por canal, proveedor y estado.",
                new ReportRoot("notification_delivery", "tenant_notification_deliveries", "d"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(5000, 18, 6, 6)
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
