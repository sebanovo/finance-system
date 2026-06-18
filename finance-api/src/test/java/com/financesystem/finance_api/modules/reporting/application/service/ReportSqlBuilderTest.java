package com.financesystem.finance_api.modules.reporting.application.service;

import com.financesystem.finance_api.modules.reporting.application.guard.AiSqlGuard;
import com.financesystem.finance_api.modules.reporting.application.guard.GuardedSql;
import com.financesystem.finance_api.modules.reporting.application.registry.ReportDefinitionRegistry;
import com.financesystem.finance_api.modules.reporting.domain.LimitKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportDefinition;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** The controlled rail must build parameterized SQL that the guard accepts. */
class ReportSqlBuilderTest {

    private final ReportSqlBuilder builder = new ReportSqlBuilder();
    private final AiSqlGuard guard = new AiSqlGuard();
    private final ReportDefinitionRegistry registry = new ReportDefinitionRegistry();

    @Test
    void buildsParameterizedMovementsQueryAndGuardAccepts() {
        ReportDefinition def = registry.find("TENANT_MOVEMENTS").orElseThrow();

        ReportSqlBuilder.BuiltSql built = builder.build(def, Map.of(
                "dateFrom", "2026-01-01",
                "status", "COMPLETED"
        ), null);

        assertEquals(2, built.bindParams().size());
        assertTrue(built.sql().contains("?"));
        assertTrue(built.sql().toUpperCase().contains("ORDER BY"));

        GuardedSql guarded = guard.guard(built.sql(), ReportScope.TENANT,
                ReportDefinitionRegistry.TENANT_VIEWS, "tenant_acme", 500);

        assertEquals(LimitKind.SYSTEM, guarded.limitKind());
        assertTrue(guarded.referencedViews().contains("reporting_tenant_movements"));
        assertTrue(guarded.safeSql().toUpperCase().endsWith("LIMIT 500"));
    }

    @Test
    void omitsBlankParamsAndProducesNoWhere() {
        ReportDefinition def = registry.find("TENANT_MOVEMENTS").orElseThrow();

        ReportSqlBuilder.BuiltSql built = builder.build(def, Map.of(), null);

        assertTrue(built.bindParams().isEmpty());
        assertFalse(built.sql().toUpperCase().contains("WHERE"));
    }

    @Test
    void rejectsDisallowedSortField() {
        ReportDefinition def = registry.find("TENANT_MOVEMENTS").orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> builder.build(def, Map.of(),
                new com.financesystem.finance_api.modules.reporting.domain.ReportSort(
                        "password_hash",
                        com.financesystem.finance_api.modules.reporting.domain.SortDirection.ASC)));
    }
}
