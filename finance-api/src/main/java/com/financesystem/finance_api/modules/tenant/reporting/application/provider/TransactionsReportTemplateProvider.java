package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportMetricKey;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TransactionsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(TransactionReportFieldKey.TRANSACTION_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_ID,
                "ID de transacción",
                ReportFieldType.UUID,
                "t.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_TYPE.name(), field(
                TransactionReportFieldKey.TRANSACTION_TYPE,
                "Tipo",
                ReportFieldType.ENUM,
                "t.type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_STATUS.name(), field(
                TransactionReportFieldKey.TRANSACTION_STATUS,
                "Estado",
                ReportFieldType.ENUM,
                "t.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionStatus.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_CHANNEL.name(), field(
                TransactionReportFieldKey.TRANSACTION_CHANNEL,
                "Canal",
                ReportFieldType.ENUM,
                "t.channel",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionChannel.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_AMOUNT.name(), field(
                TransactionReportFieldKey.TRANSACTION_AMOUNT,
                "Monto",
                ReportFieldType.MONEY,
                "t.amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_CURRENCY.name(), field(
                TransactionReportFieldKey.TRANSACTION_CURRENCY,
                "Moneda",
                ReportFieldType.ENUM,
                "t.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_ID,
                "ID cuenta origen",
                ReportFieldType.UUID,
                "t.source_account_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_NUMBER.name(), field(
                TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_NUMBER,
                "Número cuenta origen",
                ReportFieldType.TEXT,
                "sa.account_number",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "source_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_DISPLAY_NAME.name(), field(
                TransactionReportFieldKey.TRANSACTION_SOURCE_ACCOUNT_DISPLAY_NAME,
                "Nombre cuenta origen",
                ReportFieldType.TEXT,
                accountDisplayNameExpression("sa"),
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "source_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_ID,
                "ID cuenta destino",
                ReportFieldType.UUID,
                "t.target_account_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_NUMBER.name(), field(
                TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_NUMBER,
                "Número cuenta destino",
                ReportFieldType.TEXT,
                "ta.account_number",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "target_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_DISPLAY_NAME.name(), field(
                TransactionReportFieldKey.TRANSACTION_TARGET_ACCOUNT_DISPLAY_NAME,
                "Nombre cuenta destino",
                ReportFieldType.TEXT,
                accountDisplayNameExpression("ta"),
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "target_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_EXTERNAL_REFERENCE.name(), field(
                TransactionReportFieldKey.TRANSACTION_EXTERNAL_REFERENCE,
                "Referencia externa",
                ReportFieldType.TEXT,
                "t.external_reference",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_IDEMPOTENCY_KEY.name(), field(
                TransactionReportFieldKey.TRANSACTION_IDEMPOTENCY_KEY,
                "Idempotency key",
                ReportFieldType.TEXT,
                "t.idempotency_key",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_DESCRIPTION.name(), field(
                TransactionReportFieldKey.TRANSACTION_DESCRIPTION,
                "Descripción",
                ReportFieldType.TEXT,
                "t.description",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_FAILURE_REASON.name(), field(
                TransactionReportFieldKey.TRANSACTION_FAILURE_REASON,
                "Motivo de falla",
                ReportFieldType.TEXT,
                "t.failure_reason",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_PARENT_TRANSACTION_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_PARENT_TRANSACTION_ID,
                "ID transacción padre",
                ReportFieldType.UUID,
                "t.parent_transaction_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_REVERSED_TRANSACTION_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_REVERSED_TRANSACTION_ID,
                "ID transacción revertida",
                ReportFieldType.UUID,
                "t.reversed_transaction_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_ID,
                "ID usuario solicitante",
                ReportFieldType.UUID,
                "t.requested_by_user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_EMAIL.name(), field(
                TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_EMAIL,
                "Correo usuario solicitante",
                ReportFieldType.TEXT,
                "ru.email",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "requested_user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_FULL_NAME.name(), field(
                TransactionReportFieldKey.TRANSACTION_REQUESTED_BY_USER_FULL_NAME,
                "Nombre usuario solicitante",
                ReportFieldType.TEXT,
                "concat_ws(' ', ru.first_name, ru.last_name)",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "requested_user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_ID.name(), field(
                TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_ID,
                "ID usuario aprobador",
                ReportFieldType.UUID,
                "t.approved_by_user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_EMAIL.name(), field(
                TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_EMAIL,
                "Correo usuario aprobador",
                ReportFieldType.TEXT,
                "au.email",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "approved_user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_FULL_NAME.name(), field(
                TransactionReportFieldKey.TRANSACTION_APPROVED_BY_USER_FULL_NAME,
                "Nombre usuario aprobador",
                ReportFieldType.TEXT,
                "concat_ws(' ', au.first_name, au.last_name)",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "approved_user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_PROCESSED_AT.name(), field(
                TransactionReportFieldKey.TRANSACTION_PROCESSED_AT,
                "Fecha de procesamiento",
                ReportFieldType.DATETIME,
                "t.processed_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_CREATED_AT.name(), field(
                TransactionReportFieldKey.TRANSACTION_CREATED_AT,
                "Fecha de creación",
                ReportFieldType.DATETIME,
                "t.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionReportFieldKey.TRANSACTION_UPDATED_AT.name(), field(
                TransactionReportFieldKey.TRANSACTION_UPDATED_AT,
                "Fecha de actualización",
                ReportFieldType.DATETIME,
                "t.updated_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));

        Map<String, ReportRelation> relations = new java.util.LinkedHashMap<>();
        relations.put("source_account", new ReportRelation(
                "source_account",
                "LEFT JOIN tenant_accounts sa ON sa.id = t.source_account_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("target_account", new ReportRelation(
                "target_account",
                "LEFT JOIN tenant_accounts ta ON ta.id = t.target_account_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("requested_user", new ReportRelation(
                "requested_user",
                "LEFT JOIN tenant_users ru ON ru.id = t.requested_by_user_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("approved_user", new ReportRelation(
                "approved_user",
                "LEFT JOIN tenant_users au ON au.id = t.approved_by_user_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(TransactionReportMetricKey.TOTAL_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.TOTAL_TRANSACTIONS,
                "Total transacciones",
                ReportFieldType.NUMBER,
                "COUNT(t.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.TOTAL_AMOUNT.name(), metric(
                TransactionReportMetricKey.TOTAL_AMOUNT,
                "Monto total",
                ReportFieldType.MONEY,
                "COALESCE(SUM(t.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.AVERAGE_AMOUNT.name(), metric(
                TransactionReportMetricKey.AVERAGE_AMOUNT,
                "Monto promedio",
                ReportFieldType.MONEY,
                "COALESCE(AVG(t.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.MAX_AMOUNT.name(), metric(
                TransactionReportMetricKey.MAX_AMOUNT,
                "Monto máximo",
                ReportFieldType.MONEY,
                "COALESCE(MAX(t.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.MIN_AMOUNT.name(), metric(
                TransactionReportMetricKey.MIN_AMOUNT,
                "Monto mínimo",
                ReportFieldType.MONEY,
                "COALESCE(MIN(t.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.COMPLETED_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.COMPLETED_TRANSACTIONS,
                "Transacciones completadas",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE t.status = 'COMPLETED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.FAILED_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.FAILED_TRANSACTIONS,
                "Transacciones fallidas",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE t.status = 'FAILED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.REVERSED_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.REVERSED_TRANSACTIONS,
                "Transacciones revertidas",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE t.status = 'REVERSED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.PENDING_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.PENDING_TRANSACTIONS,
                "Transacciones pendientes",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE t.status IN ('PENDING', 'PENDING_REVIEW', 'PROCESSING', 'AUTHORIZED'))",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionReportMetricKey.TRANSFER_TRANSACTIONS.name(), metric(
                TransactionReportMetricKey.TRANSFER_TRANSACTIONS,
                "Transferencias",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE t.type = 'TRANSFER')",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        TransactionReportColumnKey.TRANSACTION_CREATED_AT,
                        TransactionReportColumnKey.TRANSACTION_TYPE,
                        TransactionReportColumnKey.TRANSACTION_STATUS,
                        TransactionReportColumnKey.TRANSACTION_CHANNEL,
                        TransactionReportColumnKey.TRANSACTION_AMOUNT,
                        TransactionReportColumnKey.TRANSACTION_CURRENCY,
                        TransactionReportColumnKey.TRANSACTION_SOURCE_ACCOUNT_NUMBER,
                        TransactionReportColumnKey.TRANSACTION_SOURCE_ACCOUNT_DISPLAY_NAME,
                        TransactionReportColumnKey.TRANSACTION_TARGET_ACCOUNT_NUMBER,
                        TransactionReportColumnKey.TRANSACTION_TARGET_ACCOUNT_DISPLAY_NAME,
                        TransactionReportColumnKey.TRANSACTION_REQUESTED_BY_USER_FULL_NAME,
                        TransactionReportColumnKey.TRANSACTION_EXTERNAL_REFERENCE,
                        TransactionReportColumnKey.TRANSACTION_IDEMPOTENCY_KEY,
                        TransactionReportColumnKey.TRANSACTION_PROCESSED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        TransactionReportFieldKey.TRANSACTION_CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(TransactionReportGroupByKey.TRANSACTION_STATUS),
                List.of(
                        TransactionReportMetricKey.TOTAL_TRANSACTIONS,
                        TransactionReportMetricKey.TOTAL_AMOUNT,
                        TransactionReportMetricKey.COMPLETED_TRANSACTIONS,
                        TransactionReportMetricKey.FAILED_TRANSACTIONS,
                        TransactionReportMetricKey.REVERSED_TRANSACTIONS
                ),
                List.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE
                ),
                List.of(new ReportSort(
                        ReportSortTargetType.METRIC,
                        null,
                        TransactionReportMetricKey.TOTAL_TRANSACTIONS,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.TRANSACTIONS,
                "Reporte de transacciones",
                "Permite generar reportes analíticos y gerenciales sobre transacciones financieras.",
                new ReportRoot("transaction", "tenant_transactions", "t"),
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                fields,
                relations,
                metrics,
                defaults,
                Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX),
                Set.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE,
                        ReportVisualizationType.BAR_CHART,
                        ReportVisualizationType.PIE_CHART
                ),
                new ReportExportOptions(
                        new ReportPdfExportOptions("landscape", "A4", true, true, 8),
                        new ReportXlsxExportOptions(true, true, true, true)
                ),
                new ReportLimits(5000, 18, 5, 10)
        );
    }

    private static ReportField field(
            Enum<?> key,
            String label,
            ReportFieldType type,
            String sqlExpression,
            List<ReportOperator> operators,
            List<Enum<?>> options,
            String relationKey,
            Set<ReportMode> allowedModes,
            boolean filterable,
            boolean columnable,
            boolean groupable,
            boolean sortable
    ) {
        return new ReportField(
                key,
                label,
                type,
                sqlExpression,
                operators,
                options,
                relationKey,
                allowedModes,
                filterable,
                columnable,
                groupable,
                sortable
        );
    }

    private static ReportMetric metric(
            Enum<?> key,
            String label,
            ReportFieldType type,
            String sqlExpression,
            Set<ReportMode> allowedModes
    ) {
        return new ReportMetric(key, label, type, sqlExpression, allowedModes);
    }

    private static List<Enum<?>> enumOptions(Enum<?>[] values) {
        return List.of(values);
    }

    private static String accountDisplayNameExpression(String alias) {
        return "COALESCE(NULLIF(TRIM(" + alias + ".custom_alias), ''), " + accountNameCaseExpression(alias) + ", " + alias + ".account_number)";
    }

    private static String accountNameCaseExpression(String alias) {
        return "CASE " + alias + ".account_name " +
                "WHEN '" + AccountName.MAIN_WALLET.name() + "' THEN 'Wallet principal' " +
                "WHEN '" + AccountName.SAVINGS_ACCOUNT.name() + "' THEN 'Cuenta de ahorro' " +
                "WHEN '" + AccountName.CHECKING_ACCOUNT.name() + "' THEN 'Cuenta corriente' " +
                "WHEN '" + AccountName.CREDIT_CARD_ACCOUNT.name() + "' THEN 'Tarjeta de crédito' " +
                "WHEN '" + AccountName.PREPAID_CARD_ACCOUNT.name() + "' THEN 'Tarjeta prepago' " +
                "WHEN '" + AccountName.LOAN_ACCOUNT.name() + "' THEN 'Cuenta de préstamo' " +
                "WHEN '" + AccountName.BUSINESS_ACCOUNT.name() + "' THEN 'Cuenta empresarial' " +
                "WHEN '" + AccountName.SECONDARY_ACCOUNT.name() + "' THEN 'Cuenta secundaria' " +
                "ELSE " + alias + ".account_number END";
    }
}
