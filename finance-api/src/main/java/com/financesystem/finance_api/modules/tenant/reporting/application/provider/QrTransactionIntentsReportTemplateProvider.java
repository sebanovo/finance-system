package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.qrtransactionintents.*;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.QrTransactionIntentStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class QrTransactionIntentsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(QrTransactionIntentReportFieldKey.INTENT_ID.name(), field(
                QrTransactionIntentReportFieldKey.INTENT_ID,
                "Intent ID",
                ReportFieldType.UUID,
                "i.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.STATUS.name(), field(
                QrTransactionIntentReportFieldKey.STATUS,
                "Status",
                ReportFieldType.ENUM,
                "i.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(QrTransactionIntentStatus.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.CHANNEL.name(), field(
                QrTransactionIntentReportFieldKey.CHANNEL,
                "Channel",
                ReportFieldType.ENUM,
                "i.channel",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionChannel.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.AMOUNT.name(), field(
                QrTransactionIntentReportFieldKey.AMOUNT,
                "Amount",
                ReportFieldType.MONEY,
                "i.amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.CURRENCY.name(), field(
                QrTransactionIntentReportFieldKey.CURRENCY,
                "Currency",
                ReportFieldType.ENUM,
                "i.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_ID.name(), field(
                QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_ID,
                "Target account ID",
                ReportFieldType.UUID,
                "i.target_account_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_NUMBER.name(), field(
                QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_NUMBER,
                "Target account number",
                ReportFieldType.TEXT,
                "ta.account_number",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "target_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_NAME.name(), field(
                QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_NAME,
                "Target account name",
                ReportFieldType.ENUM,
                "ta.account_name",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountName.values()),
                "target_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_DISPLAY_NAME.name(), field(
                QrTransactionIntentReportFieldKey.TARGET_ACCOUNT_DISPLAY_NAME,
                "Target account display name",
                ReportFieldType.TEXT,
                accountDisplayNameExpression("ta"),
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "target_account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.EXTERNAL_REFERENCE.name(), field(
                QrTransactionIntentReportFieldKey.EXTERNAL_REFERENCE,
                "External reference",
                ReportFieldType.TEXT,
                "i.external_reference",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.DESCRIPTION.name(), field(
                QrTransactionIntentReportFieldKey.DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "i.description",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.IDEMPOTENCY_KEY.name(), field(
                QrTransactionIntentReportFieldKey.IDEMPOTENCY_KEY,
                "Idempotency key",
                ReportFieldType.TEXT,
                "i.idempotency_key",
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.CONFIRMED_TRANSACTION_ID.name(), field(
                QrTransactionIntentReportFieldKey.CONFIRMED_TRANSACTION_ID,
                "Confirmed transaction ID",
                ReportFieldType.UUID,
                "i.confirmed_transaction_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.REQUESTED_BY_USER_ID.name(), field(
                QrTransactionIntentReportFieldKey.REQUESTED_BY_USER_ID,
                "Requested by user ID",
                ReportFieldType.UUID,
                "i.requested_by_user_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.REQUESTED_BY_USER_DISPLAY_NAME.name(), field(
                QrTransactionIntentReportFieldKey.REQUESTED_BY_USER_DISPLAY_NAME,
                "Requested by user",
                ReportFieldType.TEXT,
                userDisplayNameExpression("ru"),
                List.of(ReportOperator.EQ, ReportOperator.IN, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "requested_user",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.CONFIRMED_AT.name(), field(
                QrTransactionIntentReportFieldKey.CONFIRMED_AT,
                "Confirmed at",
                ReportFieldType.DATETIME,
                "i.confirmed_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.CREATED_AT.name(), field(
                QrTransactionIntentReportFieldKey.CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "i.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(QrTransactionIntentReportFieldKey.UPDATED_AT.name(), field(
                QrTransactionIntentReportFieldKey.UPDATED_AT,
                "Updated at",
                ReportFieldType.DATETIME,
                "i.updated_at",
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
        relations.put("target_account", new ReportRelation(
                "target_account",
                "LEFT JOIN tenant_accounts ta ON ta.id = i.target_account_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("requested_user", new ReportRelation(
                "requested_user",
                "LEFT JOIN tenant_users ru ON ru.id = i.requested_by_user_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(QrTransactionIntentReportMetricKey.TOTAL_INTENTS.name(), metric(
                QrTransactionIntentReportMetricKey.TOTAL_INTENTS,
                "Total intents",
                ReportFieldType.NUMBER,
                "COUNT(i.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.TOTAL_AMOUNT.name(), metric(
                QrTransactionIntentReportMetricKey.TOTAL_AMOUNT,
                "Total amount",
                ReportFieldType.MONEY,
                "COALESCE(SUM(i.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.AVERAGE_AMOUNT.name(), metric(
                QrTransactionIntentReportMetricKey.AVERAGE_AMOUNT,
                "Average amount",
                ReportFieldType.MONEY,
                "COALESCE(AVG(i.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.MAX_AMOUNT.name(), metric(
                QrTransactionIntentReportMetricKey.MAX_AMOUNT,
                "Max amount",
                ReportFieldType.MONEY,
                "COALESCE(MAX(i.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.MIN_AMOUNT.name(), metric(
                QrTransactionIntentReportMetricKey.MIN_AMOUNT,
                "Min amount",
                ReportFieldType.MONEY,
                "COALESCE(MIN(i.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.PENDING_INTENTS.name(), metric(
                QrTransactionIntentReportMetricKey.PENDING_INTENTS,
                "Pending intents",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE i.status = 'PENDING')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.CONFIRMED_INTENTS.name(), metric(
                QrTransactionIntentReportMetricKey.CONFIRMED_INTENTS,
                "Confirmed intents",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE i.status = 'CONFIRMED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.CANCELLED_INTENTS.name(), metric(
                QrTransactionIntentReportMetricKey.CANCELLED_INTENTS,
                "Cancelled intents",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE i.status = 'CANCELLED')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(QrTransactionIntentReportMetricKey.EXPIRED_INTENTS.name(), metric(
                QrTransactionIntentReportMetricKey.EXPIRED_INTENTS,
                "Expired intents",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE i.status = 'EXPIRED')",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        QrTransactionIntentReportColumnKey.INTENT_ID,
                        QrTransactionIntentReportColumnKey.STATUS,
                        QrTransactionIntentReportColumnKey.CHANNEL,
                        QrTransactionIntentReportColumnKey.AMOUNT,
                        QrTransactionIntentReportColumnKey.CURRENCY,
                        QrTransactionIntentReportColumnKey.TARGET_ACCOUNT_NUMBER,
                        QrTransactionIntentReportColumnKey.TARGET_ACCOUNT_DISPLAY_NAME,
                        QrTransactionIntentReportColumnKey.EXTERNAL_REFERENCE,
                        QrTransactionIntentReportColumnKey.REQUESTED_BY_USER_DISPLAY_NAME,
                        QrTransactionIntentReportColumnKey.CONFIRMED_AT,
                        QrTransactionIntentReportColumnKey.CREATED_AT
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        QrTransactionIntentReportFieldKey.CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(
                        QrTransactionIntentReportGroupByKey.STATUS,
                        QrTransactionIntentReportGroupByKey.CHANNEL,
                        QrTransactionIntentReportGroupByKey.CURRENCY,
                        QrTransactionIntentReportGroupByKey.TARGET_ACCOUNT_DISPLAY_NAME,
                        QrTransactionIntentReportGroupByKey.REQUESTED_BY_USER_DISPLAY_NAME
                ),
                List.of(
                        QrTransactionIntentReportMetricKey.TOTAL_INTENTS,
                        QrTransactionIntentReportMetricKey.TOTAL_AMOUNT,
                        QrTransactionIntentReportMetricKey.AVERAGE_AMOUNT,
                        QrTransactionIntentReportMetricKey.MAX_AMOUNT,
                        QrTransactionIntentReportMetricKey.MIN_AMOUNT,
                        QrTransactionIntentReportMetricKey.PENDING_INTENTS,
                        QrTransactionIntentReportMetricKey.CONFIRMED_INTENTS,
                        QrTransactionIntentReportMetricKey.CANCELLED_INTENTS,
                        QrTransactionIntentReportMetricKey.EXPIRED_INTENTS
                ),
                List.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE,
                        ReportVisualizationType.BAR_CHART,
                        ReportVisualizationType.PIE_CHART
                ),
                List.of(new ReportSort(
                        ReportSortTargetType.METRIC,
                        null,
                        QrTransactionIntentReportMetricKey.TOTAL_INTENTS,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.QR_TRANSACTION_INTENTS,
                "Reporte de intenciones de transacciones QR",
                "Permite generar reportes analíticos y gerenciales sobre intenciones de transacciones QR.",
                new ReportRoot("qr_transaction_intent", "tenant_qr_transaction_intents", "i"),
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
                new ReportLimits(4000, 17, 5, 9)
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
        return new ReportField(key, label, type, sqlExpression, operators, options, relationKey, allowedModes, filterable, columnable, groupable, sortable);
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

    private static String userDisplayNameExpression(String alias) {
        return "COALESCE(NULLIF(TRIM(COALESCE(" + alias + ".first_name, '') || ' ' || COALESCE(" + alias + ".last_name, '')), ''), " + alias + ".email)";
    }
}
