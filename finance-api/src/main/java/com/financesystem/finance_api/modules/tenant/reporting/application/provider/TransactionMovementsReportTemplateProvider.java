package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportFieldKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.TransactionMovementReportMetricKey;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionMovementType;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionStatus;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TransactionMovementsReportTemplateProvider implements ReportTemplateProvider {

    private static final ReportTemplate TEMPLATE = buildTemplate();

    @Override
    public ReportTemplate getTemplate() {
        return TEMPLATE;
    }

    private static ReportTemplate buildTemplate() {
        Map<String, ReportField> fields = new java.util.LinkedHashMap<>();
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_ID.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_ID,
                "Movement ID",
                ReportFieldType.UUID,
                "m.id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_ID.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_ID,
                "Transaction ID",
                ReportFieldType.UUID,
                "m.transaction_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_TYPE.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_TYPE,
                "Transaction type",
                ReportFieldType.ENUM,
                "t.type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionType.values()),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_STATUS.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_STATUS,
                "Transaction status",
                ReportFieldType.ENUM,
                "t.status",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionStatus.values()),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CHANNEL.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CHANNEL,
                "Transaction channel",
                ReportFieldType.ENUM,
                "t.channel",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionChannel.values()),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_AMOUNT.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_AMOUNT,
                "Transaction amount",
                ReportFieldType.MONEY,
                "t.amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CURRENCY.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CURRENCY,
                "Transaction currency",
                ReportFieldType.ENUM,
                "t.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CREATED_AT.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TRANSACTION_CREATED_AT,
                "Transaction created at",
                ReportFieldType.DATETIME,
                "t.created_at",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                "transaction",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_ID.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_ID,
                "Account ID",
                ReportFieldType.UUID,
                "m.account_id",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_NUMBER.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_NUMBER,
                "Account number",
                ReportFieldType.TEXT,
                "a.account_number",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_NAME.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_NAME,
                "Account name",
                ReportFieldType.ENUM,
                "a.account_name",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(AccountName.values()),
                "account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_DISPLAY_NAME.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_ACCOUNT_DISPLAY_NAME,
                "Account display name",
                ReportFieldType.TEXT,
                accountDisplayNameExpression("a"),
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                "account",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_TYPE.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_TYPE,
                "Movement type",
                ReportFieldType.ENUM,
                "m.movement_type",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(TransactionMovementType.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_AMOUNT.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_AMOUNT,
                "Amount",
                ReportFieldType.MONEY,
                "m.amount",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_CURRENCY.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_CURRENCY,
                "Currency",
                ReportFieldType.ENUM,
                "m.currency",
                List.of(ReportOperator.EQ, ReportOperator.IN),
                enumOptions(CurrencyCode.values()),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                true,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_BALANCE_BEFORE.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_BALANCE_BEFORE,
                "Balance before",
                ReportFieldType.MONEY,
                "m.balance_before",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_BALANCE_AFTER.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_BALANCE_AFTER,
                "Balance after",
                ReportFieldType.MONEY,
                "m.balance_after",
                List.of(ReportOperator.EQ, ReportOperator.GT, ReportOperator.GTE, ReportOperator.LT, ReportOperator.LTE, ReportOperator.BETWEEN),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_DESCRIPTION.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_DESCRIPTION,
                "Description",
                ReportFieldType.TEXT,
                "m.description",
                List.of(ReportOperator.EQ, ReportOperator.CONTAINS, ReportOperator.STARTS_WITH, ReportOperator.ENDS_WITH),
                List.of(),
                null,
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL),
                true,
                true,
                false,
                true
        ));
        fields.put(TransactionMovementReportFieldKey.MOVEMENT_CREATED_AT.name(), field(
                TransactionMovementReportFieldKey.MOVEMENT_CREATED_AT,
                "Created at",
                ReportFieldType.DATETIME,
                "m.created_at",
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
        relations.put("transaction", new ReportRelation(
                "transaction",
                "LEFT JOIN tenant_transactions t ON t.id = m.transaction_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));
        relations.put("account", new ReportRelation(
                "account",
                "LEFT JOIN tenant_accounts a ON a.id = m.account_id",
                Set.of(ReportMode.ANALYTIC, ReportMode.MANAGERIAL)
        ));

        Map<String, ReportMetric> metrics = new java.util.LinkedHashMap<>();
        metrics.put(TransactionMovementReportMetricKey.TOTAL_MOVEMENTS.name(), metric(
                TransactionMovementReportMetricKey.TOTAL_MOVEMENTS,
                "Total movements",
                ReportFieldType.NUMBER,
                "COUNT(m.id)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.TOTAL_AMOUNT.name(), metric(
                TransactionMovementReportMetricKey.TOTAL_AMOUNT,
                "Total amount",
                ReportFieldType.MONEY,
                "COALESCE(SUM(m.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.AVERAGE_AMOUNT.name(), metric(
                TransactionMovementReportMetricKey.AVERAGE_AMOUNT,
                "Average amount",
                ReportFieldType.MONEY,
                "COALESCE(AVG(m.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.MAX_AMOUNT.name(), metric(
                TransactionMovementReportMetricKey.MAX_AMOUNT,
                "Max amount",
                ReportFieldType.MONEY,
                "COALESCE(MAX(m.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.MIN_AMOUNT.name(), metric(
                TransactionMovementReportMetricKey.MIN_AMOUNT,
                "Min amount",
                ReportFieldType.MONEY,
                "COALESCE(MIN(m.amount), 0)",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.DEBIT_MOVEMENTS.name(), metric(
                TransactionMovementReportMetricKey.DEBIT_MOVEMENTS,
                "Debit movements",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE m.movement_type = 'DEBIT')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.CREDIT_MOVEMENTS.name(), metric(
                TransactionMovementReportMetricKey.CREDIT_MOVEMENTS,
                "Credit movements",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE m.movement_type = 'CREDIT')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.HOLD_MOVEMENTS.name(), metric(
                TransactionMovementReportMetricKey.HOLD_MOVEMENTS,
                "Hold movements",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE m.movement_type = 'HOLD')",
                Set.of(ReportMode.MANAGERIAL)
        ));
        metrics.put(TransactionMovementReportMetricKey.RELEASE_MOVEMENTS.name(), metric(
                TransactionMovementReportMetricKey.RELEASE_MOVEMENTS,
                "Release movements",
                ReportFieldType.NUMBER,
                "COUNT(*) FILTER (WHERE m.movement_type = 'RELEASE')",
                Set.of(ReportMode.MANAGERIAL)
        ));

        Map<ReportMode, ReportModeDefaults> defaults = new EnumMap<>(ReportMode.class);
        defaults.put(ReportMode.ANALYTIC, new ReportModeDefaults(
                List.of(
                        TransactionMovementReportColumnKey.MOVEMENT_CREATED_AT,
                        TransactionMovementReportColumnKey.MOVEMENT_TRANSACTION_ID,
                        TransactionMovementReportColumnKey.MOVEMENT_TRANSACTION_TYPE,
                        TransactionMovementReportColumnKey.MOVEMENT_TRANSACTION_STATUS,
                        TransactionMovementReportColumnKey.MOVEMENT_TRANSACTION_CHANNEL,
                        TransactionMovementReportColumnKey.MOVEMENT_TYPE,
                        TransactionMovementReportColumnKey.MOVEMENT_AMOUNT,
                        TransactionMovementReportColumnKey.MOVEMENT_CURRENCY,
                        TransactionMovementReportColumnKey.MOVEMENT_BALANCE_BEFORE,
                        TransactionMovementReportColumnKey.MOVEMENT_BALANCE_AFTER,
                        TransactionMovementReportColumnKey.MOVEMENT_ACCOUNT_NUMBER,
                        TransactionMovementReportColumnKey.MOVEMENT_ACCOUNT_DISPLAY_NAME,
                        TransactionMovementReportColumnKey.MOVEMENT_DESCRIPTION
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(new ReportSort(
                        ReportSortTargetType.FIELD,
                        TransactionMovementReportFieldKey.MOVEMENT_CREATED_AT,
                        null,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));
        defaults.put(ReportMode.MANAGERIAL, new ReportModeDefaults(
                List.of(),
                List.of(TransactionMovementReportGroupByKey.MOVEMENT_TYPE),
                List.of(
                        TransactionMovementReportMetricKey.TOTAL_MOVEMENTS,
                        TransactionMovementReportMetricKey.TOTAL_AMOUNT,
                        TransactionMovementReportMetricKey.CREDIT_MOVEMENTS,
                        TransactionMovementReportMetricKey.DEBIT_MOVEMENTS
                ),
                List.of(
                        ReportVisualizationType.SUMMARY_CARDS,
                        ReportVisualizationType.TABLE
                ),
                List.of(new ReportSort(
                        ReportSortTargetType.METRIC,
                        null,
                        TransactionMovementReportMetricKey.TOTAL_MOVEMENTS,
                        SortDirection.DESC
                )),
                List.of(ReportOutput.SCREEN)
        ));

        return new ReportTemplate(
                ReportType.TRANSACTION_MOVEMENTS,
                "Reporte de movimientos de transacciones",
                "Permite generar reportes analíticos y gerenciales sobre movimientos asociados a transacciones.",
                new ReportRoot("movement", "tenant_transaction_movements", "m"),
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
