package com.financesystem.finance_api.modules.reporting.application.guard;

import com.financesystem.finance_api.modules.reporting.domain.LimitKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AiSqlGuardTest {

    private final AiSqlGuard guard = new AiSqlGuard();

    private static final Set<String> TENANT_VIEWS = Set.of(
            "reporting_tenant_movements",
            "reporting_tenant_accounts_balances",
            "reporting_tenant_users_activity",
            "reporting_tenant_executive_overview"
    );
    private static final Set<String> PLATFORM_VIEWS = Set.of(
            "platform_overview",
            "tenant_movement_ranking"
    );

    private GuardedSql guardTenant(String sql) {
        return guard.guard(sql, ReportScope.TENANT, TENANT_VIEWS, "tenant_acme", 500);
    }

    private GuardedSql guardPlatform(String sql) {
        return guard.guard(sql, ReportScope.GLOBAL, PLATFORM_VIEWS, "reporting", 500);
    }

    // ----------------------------- happy paths -----------------------------

    @Test
    void allowsSimpleTenantSelectAndInjectsSystemLimit() {
        GuardedSql g = guardTenant("SELECT * FROM reporting_tenant_movements WHERE status = 'COMPLETED'");
        assertEquals(LimitKind.SYSTEM, g.limitKind());
        assertTrue(g.safeSql().toUpperCase().endsWith("LIMIT 500"));
        assertTrue(g.referencedViews().contains("reporting_tenant_movements"));
        assertEquals("tenant_acme", g.schemaUsed());
    }

    @Test
    void respectsUserLimit() {
        GuardedSql g = guardTenant("SELECT * FROM reporting_tenant_movements ORDER BY amount DESC LIMIT 10");
        assertEquals(LimitKind.USER, g.limitKind());
        assertFalse(g.safeSql().toUpperCase().endsWith("LIMIT 500"));
    }

    @Test
    void allowsCteWhenInnerTablesAreWhitelisted() {
        GuardedSql g = guardTenant(
                "WITH top AS (SELECT amount FROM reporting_tenant_movements) SELECT * FROM top");
        assertTrue(g.referencedViews().contains("reporting_tenant_movements"));
    }

    @Test
    void allowsJoinBetweenWhitelistedViews() {
        GuardedSql g = guardTenant(
                "SELECT a.account_id, m.amount FROM reporting_tenant_accounts_balances a "
                        + "JOIN reporting_tenant_movements m ON m.source_account_id = a.account_id");
        assertEquals(2, g.referencedViews().size());
    }

    @Test
    void allowsPlatformQueryAgainstReportingViews() {
        GuardedSql g = guardPlatform("SELECT tenant_slug, total_amount FROM tenant_movement_ranking");
        assertTrue(g.referencedViews().contains("tenant_movement_ranking"));
    }

    @Test
    void allowsReportingSchemaQualifiedNameInGlobalScope() {
        GuardedSql g = guardPlatform("SELECT * FROM reporting.platform_overview");
        assertTrue(g.referencedViews().contains("platform_overview"));
    }

    // ----------------------------- malicious prompts -----------------------------

    @Test
    void blocksInformationSchemaInCte() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "WITH x AS (SELECT table_name FROM information_schema.tables) SELECT * FROM x"));
    }

    @Test
    void blocksCrossTenantSchemaReference() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * FROM tenant_otro.tenant_transactions"));
    }

    @Test
    void blocksPgSleep() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT pg_sleep(10) FROM reporting_tenant_movements"));
    }

    @Test
    void blocksListingTablesViaPgCatalog() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * FROM pg_catalog.pg_tables"));
    }

    @Test
    void blocksSubqueryAgainstRawSensitiveTable() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * FROM reporting_tenant_movements WHERE requested_by_user_id IN "
                        + "(SELECT user_id FROM password_reset_tokens)"));
    }

    @Test
    void blocksUnqualifiedNonWhitelistedTable() {
        assertThrows(SqlGuardException.class, () -> guardTenant("SELECT * FROM tenant_users"));
    }

    @Test
    void blocksMultiStatement() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * FROM reporting_tenant_movements; DROP TABLE tenant_users"));
    }

    @Test
    void blocksNonSelect() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "UPDATE reporting_tenant_movements SET amount = 0"));
    }

    @Test
    void blocksSelectInto() {
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * INTO evil FROM reporting_tenant_movements"));
    }

    @Test
    void blocksSchemaQualifiedViewInTenantScopeEvenIfWhitelisted() {
        // Naming the view with a schema prefix is rejected in TENANT scope.
        assertThrows(SqlGuardException.class, () -> guardTenant(
                "SELECT * FROM tenant_acme.reporting_tenant_movements"));
    }

    @Test
    void blocksUnknownSchemaInGlobalScope() {
        assertThrows(SqlGuardException.class, () -> guardPlatform(
                "SELECT * FROM public.platform_tenants"));
    }

    @Test
    void blocksGarbageSql() {
        assertThrows(SqlGuardException.class, () -> guardTenant("not even sql"));
    }
}
