package com.financesystem.finance_api.bootstrap.tenant;

import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class TenantBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(TenantBootstrapService.class);
    private static final List<String> DEFAULT_OWNER_ADMIN_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.confirm",

            "accounts.create",
            "accounts.list",
            "accounts.view",
            "accounts.balance.read",
            "accounts.update",
            "accounts.approve",
            "accounts.activate",
            "accounts.block",
            "accounts.freeze",
            "accounts.close",
            "accounts.transactions.read",

            "access.permissions.read",
            "access.roles.read",
            "access.roles.create",
            "access.roles.detail",
            "access.roles.update",
            "access.roles.activate",
            "access.roles.deactivate",
            "access.users.roles.read",
            "access.users.roles.assign",

            "users.list",
            "users.create",
            "users.detail",
            "users.update",
            "users.activate",
            "users.deactivate",

            "transactions.read",
            "transactions.detail",
            "transactions.create.transfer",
            "transactions.create.deposit",
            "transactions.create.withdrawal",
            "transactions.create.payment",
            "transactions.reverse",
            "transactions.refund",
            "transactions.fee",
            "transactions.hold",
            "transactions.release",
            "transactions.adjust",
            "transactions.admin.read",
            "transactions.admin.export",
            "transactions.qr.create",
            "transactions.qr.confirm",

            "limits.read",
            "limits.detail",
            "limits.create",
            "limits.update",
            "limits.delete",
            "limits.evaluate",

            "accounting.journal.read",
            "accounting.journal.detail",
            "accounting.periods.read",
            "accounting.periods.create",
            "accounting.periods.close",

            "audit.events.read",
            "notifications.templates.read",
            "notifications.templates.detail",
            "notifications.deliveries.read",

            "fx.rates.read",
            "fx.rates.detail",
            "fx.rates.create",
            "fx.rates.update",
            "fx.rates.delete",
            "fx.fees.read",
            "fx.fees.detail",
            "fx.fees.create",
            "fx.fees.update",
            "fx.fees.delete",

            "backups.create",
            "backups.list",
            "backups.detail",
            "backups.download",
            "backups.restore"
    );

    private static final List<String> DEFAULT_ADMIN_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.confirm",

            "accounts.list",
            "accounts.view",
            "accounts.balance.read",
            "accounts.approve",
            "accounts.activate",
            "accounts.block",
            "accounts.freeze",
            "accounts.close",
            "accounts.transactions.read",

            "users.list",
            "users.create",
            "users.detail",
            "users.update",
            "users.activate",
            "users.deactivate",

            "transactions.read",
            "transactions.detail",
            "transactions.create.transfer",
            "transactions.create.deposit",
            "transactions.create.withdrawal",
            "transactions.create.payment",
            "transactions.reverse",
            "transactions.refund",
            "transactions.fee",
            "transactions.hold",
            "transactions.release",
            "transactions.adjust",
            "transactions.admin.read",
            "transactions.admin.export",
            "transactions.qr.create",
            "transactions.qr.confirm",

            "limits.read",
            "limits.detail",
            "limits.evaluate",

            "accounting.journal.read",
            "accounting.journal.detail",
            "accounting.periods.read",

            "audit.events.read",
            "notifications.templates.read",
            "notifications.templates.detail",
            "notifications.deliveries.read",

            "fx.rates.read",
            "fx.rates.detail",
            "fx.fees.read",
            "fx.fees.detail",

            "backups.create",
            "backups.list",
            "backups.detail",
            "backups.download",
            "backups.restore"
    );
    private static final List<String> DEFAULT_CLIENT_PERMISSION_CODES = List.of(
            "me.accounts.create",
            "me.accounts.list",
            "me.accounts.view",
            "me.accounts.balance.read",
            "me.accounts.update.alias",
            "me.accounts.transactions.read",
            "me.transactions.read",
            "me.transactions.detail",
            "me.transactions.transfer",
            "me.transactions.deposit",
            "me.transactions.withdrawal",
            "me.transactions.payment",
            "me.transactions.hold",
            "me.transactions.release",
            "me.transactions.qr.confirm"
    );
    private static final List<FxRateSeed> DEFAULT_FX_RATES = List.of(
            new FxRateSeed("BOB", "BOB", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("USD", "USD", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("EUR", "EUR", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("USDT", "USDT", new java.math.BigDecimal("1.00000000"), "Base currency to itself"),
            new FxRateSeed("BOB", "USD", new java.math.BigDecimal("0.14500000"), "Default BOB to USD rate"),
            new FxRateSeed("USD", "BOB", new java.math.BigDecimal("6.90000000"), "Default USD to BOB rate"),
            new FxRateSeed("BOB", "EUR", new java.math.BigDecimal("0.13300000"), "Default BOB to EUR rate"),
            new FxRateSeed("EUR", "BOB", new java.math.BigDecimal("7.51879699"), "Default EUR to BOB rate"),
            new FxRateSeed("BOB", "USDT", new java.math.BigDecimal("0.14500000"), "Default BOB to USDT rate"),
            new FxRateSeed("USDT", "BOB", new java.math.BigDecimal("6.90000000"), "Default USDT to BOB rate"),
            new FxRateSeed("USD", "EUR", new java.math.BigDecimal("0.92000000"), "Default USD to EUR rate"),
            new FxRateSeed("EUR", "USD", new java.math.BigDecimal("1.08695652"), "Default EUR to USD rate"),
            new FxRateSeed("USD", "USDT", new java.math.BigDecimal("1.00000000"), "Default USD to USDT rate"),
            new FxRateSeed("USDT", "USD", new java.math.BigDecimal("1.00000000"), "Default USDT to USD rate"),
            new FxRateSeed("EUR", "USDT", new java.math.BigDecimal("1.08695652"), "Default EUR to USDT rate"),
            new FxRateSeed("USDT", "EUR", new java.math.BigDecimal("0.92000000"), "Default USDT to EUR rate")
    );
    private static final List<OperationFeeSeed> DEFAULT_OPERATION_FEES = List.of(
            new OperationFeeSeed(FxOperationCode.TRANSFER, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default transfer fee"),
            new OperationFeeSeed(FxOperationCode.CONVERSION, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default conversion fee"),
            new OperationFeeSeed(FxOperationCode.DEPOSIT, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default deposit fee"),
            new OperationFeeSeed(FxOperationCode.WITHDRAWAL, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default withdrawal fee"),
            new OperationFeeSeed(FxOperationCode.PAYMENT, "NONE", java.math.BigDecimal.ZERO, "SEPARATE", "Default payment fee")
    );

    private final JdbcTemplate jdbcTemplate;

    public TenantBootstrapService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void initializeTenantData(String schemaName, String tenantName) {
        validateSchemaName(schemaName);

        logger.info("Initializing tenant bootstrap data for schema '{}'.", schemaName);

        seedDefaultRoles(schemaName);
        seedDefaultRolePermissions(schemaName, "OWNER_ADMIN", DEFAULT_OWNER_ADMIN_PERMISSION_CODES);
        seedDefaultRolePermissions(schemaName, "ADMIN", DEFAULT_ADMIN_PERMISSION_CODES);
        seedDefaultRolePermissions(schemaName, "USER", DEFAULT_CLIENT_PERMISSION_CODES);
        seedDefaultSettings(schemaName, tenantName);
        seedDefaultFxConfiguration(schemaName);

        logger.info("Tenant bootstrap data initialized successfully for schema '{}'.", schemaName);
    }

    private void seedDefaultRoles(String schemaName) {
        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('OWNER_ADMIN', 'Tenant owner administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('ADMIN', 'Default tenant administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('USER', 'Default tenant user role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));
    }

    private void seedDefaultRolePermissions(String schemaName, String roleName, List<String> permissionCodes) {
        for (String permissionCode : permissionCodes) {
            Integer permissionCount = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM public.system_permissions
                    WHERE code = ? AND active = true
                    """,
                    Integer.class,
                    permissionCode
            );

            if (permissionCount == null || permissionCount == 0) {
                throw new IllegalStateException(
                        "Default permission '" + permissionCode + "' is not available in public.system_permissions"
                );
            }

            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_role_permissions (role_id, permission_code, assigned_at)
                    SELECT r.id, ?, NOW()
                    FROM %s.tenant_roles r
                    JOIN public.system_permissions sp
                        ON sp.code = ? AND sp.active = true
                    WHERE r.name = ?
                    ON CONFLICT (role_id, permission_code) DO NOTHING
                    """.formatted(schemaName, schemaName),
                    permissionCode,
                    permissionCode,
                    roleName
            );
        }
    }

    private void seedDefaultSettings(String schemaName, String tenantName) {
        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.name', ?, NOW(), NOW())
                ON CONFLICT (setting_key) DO UPDATE SET
                    setting_value = EXCLUDED.setting_value,
                    updated_at = NOW()
                """.formatted(schemaName), tenantName);

        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.timezone', 'America/La_Paz', NOW(), NOW())
                ON CONFLICT (setting_key) DO NOTHING
                """.formatted(schemaName));

        jdbcTemplate.update("""
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.currency', 'BOB', NOW(), NOW())
                ON CONFLICT (setting_key) DO NOTHING
                """.formatted(schemaName));
    }

    private void seedDefaultFxConfiguration(String schemaName) {
        validateFxSeeds();
        assertFxTablesExist(schemaName);

        for (FxRateSeed rate : DEFAULT_FX_RATES) {
            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_fx_exchange_rates (
                        source_currency, target_currency, rate, active, description, created_at, updated_at
                    )
                    VALUES (?, ?, ?, true, ?, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uq_tenant_fx_exchange_rates_pair DO UPDATE SET
                        rate = EXCLUDED.rate,
                        active = true,
                        description = EXCLUDED.description,
                        updated_at = NOW()
                    """.formatted(schemaName),
                    rate.sourceCurrency(),
                    rate.targetCurrency(),
                    rate.rate(),
                    rate.description()
            );
        }

        for (OperationFeeSeed fee : DEFAULT_OPERATION_FEES) {
            jdbcTemplate.update(
                    """
                    INSERT INTO %s.tenant_operation_fees (
                        operation_code, fee_type, fee_value, calculation_mode, active, description, created_at, updated_at
                    )
                    VALUES (?, ?, ?, ?, true, ?, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uq_tenant_operation_fees_operation_code DO UPDATE SET
                        fee_type = EXCLUDED.fee_type,
                        fee_value = EXCLUDED.fee_value,
                        calculation_mode = EXCLUDED.calculation_mode,
                        active = true,
                        description = EXCLUDED.description,
                        updated_at = NOW()
                    """.formatted(schemaName),
                    fee.operationCode().name(),
                    fee.feeType(),
                    fee.feeValue(),
                    fee.calculationMode(),
                    fee.description()
            );
        }
    }

    private void assertFxTablesExist(String schemaName) {
        assertTableExists(schemaName, "tenant_fx_exchange_rates");
        assertTableExists(schemaName, "tenant_operation_fees");
    }

    private void assertTableExists(String schemaName, String tableName) {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = ? AND table_name = ?
                """,
                Integer.class,
                schemaName,
                tableName
        );

        if (tableCount == null || tableCount == 0) {
            throw new IllegalStateException(
                    "Required tenant table '" + schemaName + "." + tableName + "' does not exist. " +
                            "Tenant schema migration may not have completed successfully."
            );
        }
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Schema name contains invalid characters: " + schemaName);
        }
    }

    private void validateFxSeeds() {
        Set<String> ratePairs = new HashSet<>();
        for (FxRateSeed rate : DEFAULT_FX_RATES) {
            validateCurrencyCode(rate.sourceCurrency(), "source currency");
            validateCurrencyCode(rate.targetCurrency(), "target currency");

            if (rate.sourceCurrency().equals(rate.targetCurrency()) &&
                    rate.rate().compareTo(new java.math.BigDecimal("1.00000000")) != 0) {
                throw new IllegalStateException("Self exchange rates must be 1.00000000");
            }

            String pairKey = rate.sourceCurrency() + "->" + rate.targetCurrency();
            if (!ratePairs.add(pairKey)) {
                throw new IllegalStateException("Duplicate FX rate seed for pair " + pairKey);
            }
        }

        Set<String> feeCodes = new HashSet<>();
        for (OperationFeeSeed fee : DEFAULT_OPERATION_FEES) {
            if (fee.operationCode() == null) {
                throw new IllegalStateException("FX fee operation code must not be null");
            }

            if (!feeCodes.add(fee.operationCode().name())) {
                throw new IllegalStateException("Duplicate FX fee seed for operation " + fee.operationCode());
            }
        }
    }

    private void validateCurrencyCode(String currency, String fieldName) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalStateException("FX seed " + fieldName + " must not be blank");
        }

        try {
            com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode.valueOf(currency);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Unsupported FX seed currency: " + currency);
        }
    }

    private record FxRateSeed(
            String sourceCurrency,
            String targetCurrency,
            java.math.BigDecimal rate,
            String description
    ) {
    }

    private record OperationFeeSeed(
            FxOperationCode operationCode,
            String feeType,
            java.math.BigDecimal feeValue,
            String calculationMode,
            String description
    ) {
    }

}
