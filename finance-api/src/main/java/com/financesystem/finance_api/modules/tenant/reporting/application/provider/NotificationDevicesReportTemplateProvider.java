package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationDeviceStatus;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPlatform;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.notificationdevices.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class NotificationDevicesReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(NotificationDeviceReportFieldKey.DEVICE_RECORD_ID.name(), field(NotificationDeviceReportFieldKey.DEVICE_RECORD_ID, "Device record ID", ReportFieldType.UUID, "nd.id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeviceReportFieldKey.USER_ID.name(), field(NotificationDeviceReportFieldKey.USER_ID, "User ID", ReportFieldType.UUID, "nd.user_id", List.of(ReportOperator.EQ, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.DEVICE_ID.name(), field(NotificationDeviceReportFieldKey.DEVICE_ID, "Device ID", ReportFieldType.TEXT, "nd.device_id", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.PLATFORM.name(), field(NotificationDeviceReportFieldKey.PLATFORM, "Platform", ReportFieldType.ENUM, "nd.platform", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationPlatform.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.DEVICE_NAME.name(), field(NotificationDeviceReportFieldKey.DEVICE_NAME, "Device name", ReportFieldType.TEXT, "nd.device_name", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.APP_VERSION.name(), field(NotificationDeviceReportFieldKey.APP_VERSION, "App version", ReportFieldType.TEXT, "nd.app_version", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.OS_VERSION.name(), field(NotificationDeviceReportFieldKey.OS_VERSION, "OS version", ReportFieldType.TEXT, "nd.os_version", List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.STATUS.name(), field(NotificationDeviceReportFieldKey.STATUS, "Status", ReportFieldType.ENUM, "nd.status", List.of(ReportOperator.EQ, ReportOperator.IN), enumOptions(NotificationDeviceStatus.values()), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, true, true));
        fields.put(NotificationDeviceReportFieldKey.LAST_SEEN_AT.name(), field(NotificationDeviceReportFieldKey.LAST_SEEN_AT, "Last seen at", ReportFieldType.DATETIME, "nd.last_seen_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeviceReportFieldKey.CREATED_AT.name(), field(NotificationDeviceReportFieldKey.CREATED_AT, "Created at", ReportFieldType.DATETIME, "nd.created_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));
        fields.put(NotificationDeviceReportFieldKey.UPDATED_AT.name(), field(NotificationDeviceReportFieldKey.UPDATED_AT, "Updated at", ReportFieldType.DATETIME, "nd.updated_at", List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN, ReportOperator.IN), List.of(), null, Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL), true, true, false, true));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(NotificationDeviceReportMetricKey.TOTAL_DEVICES.name(), metric(NotificationDeviceReportMetricKey.TOTAL_DEVICES, "Total devices", ReportFieldType.NUMBER, "COUNT(nd.id)", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeviceReportMetricKey.ACTIVE_DEVICES.name(), metric(NotificationDeviceReportMetricKey.ACTIVE_DEVICES, "Active devices", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE nd.status = 'ACTIVE')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeviceReportMetricKey.INACTIVE_DEVICES.name(), metric(NotificationDeviceReportMetricKey.INACTIVE_DEVICES, "Inactive devices", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE nd.status = 'INACTIVE')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeviceReportMetricKey.EXPIRED_DEVICES.name(), metric(NotificationDeviceReportMetricKey.EXPIRED_DEVICES, "Expired devices", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE nd.status = 'EXPIRED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeviceReportMetricKey.REVOKED_DEVICES.name(), metric(NotificationDeviceReportMetricKey.REVOKED_DEVICES, "Revoked devices", ReportFieldType.NUMBER, "COUNT(*) FILTER (WHERE nd.status = 'REVOKED')", Set.of(ReportMode.MANAGERIAL)));
        metrics.put(NotificationDeviceReportMetricKey.DISTINCT_USERS.name(), metric(NotificationDeviceReportMetricKey.DISTINCT_USERS, "Distinct users", ReportFieldType.NUMBER, "COUNT(DISTINCT nd.user_id)", Set.of(ReportMode.MANAGERIAL)));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        NotificationDeviceReportColumnKey.CREATED_AT,
                        NotificationDeviceReportColumnKey.USER_ID,
                        NotificationDeviceReportColumnKey.DEVICE_ID,
                        NotificationDeviceReportColumnKey.PLATFORM,
                        NotificationDeviceReportColumnKey.DEVICE_NAME,
                        NotificationDeviceReportColumnKey.APP_VERSION,
                        NotificationDeviceReportColumnKey.OS_VERSION,
                        NotificationDeviceReportColumnKey.STATUS,
                        NotificationDeviceReportColumnKey.LAST_SEEN_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(ReportSortTargetType.FIELD, NotificationDeviceReportFieldKey.CREATED_AT, null, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        NotificationDeviceReportGroupByKey.PLATFORM,
                        NotificationDeviceReportGroupByKey.STATUS
                ),
                List.of(
                        NotificationDeviceReportMetricKey.TOTAL_DEVICES,
                        NotificationDeviceReportMetricKey.ACTIVE_DEVICES,
                        NotificationDeviceReportMetricKey.INACTIVE_DEVICES,
                        NotificationDeviceReportMetricKey.EXPIRED_DEVICES,
                        NotificationDeviceReportMetricKey.REVOKED_DEVICES,
                        NotificationDeviceReportMetricKey.DISTINCT_USERS
                ),
                List.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                List.of(new ReportSort(ReportSortTargetType.METRIC, null, NotificationDeviceReportMetricKey.TOTAL_DEVICES, SortDirection.DESC)),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.NOTIFICATION_DEVICES,
                "Dispositivos de notificación",
                "Permite consultar y consolidar dispositivos registrados para notificaciones push.",
                new ReportRoot("notification_device", "tenant_notification_devices", "nd"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                Map.of(),
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(ReportVisualizationType.SUMMARY_CARDS, ReportVisualizationType.TABLE, ReportVisualizationType.BAR_CHART, ReportVisualizationType.PIE_CHART),
                new ReportExportOptions(new ReportPdfExportOptions("landscape", "A4", true, true, 8), new ReportXlsxExportOptions(true, true, true, true)),
                new ReportLimits(5000, 16, 4, 6)
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
