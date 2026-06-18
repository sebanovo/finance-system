package com.financesystem.finance_api.common.tenancy.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Read-only datasources used exclusively by the reporting executors.
 *
 * <p>Two pools, two roles (defense in depth):
 * <ul>
 *   <li>{@code tenantReadOnlyDataSource} → role {@code finance_tenant_readonly}
 *       (SELECT only on tenant {@code reporting_*} views).</li>
 *   <li>{@code platformReadOnlyDataSource} → role {@code finance_platform_readonly}
 *       (SELECT only on {@code reporting.*} cross-tenant views).</li>
 * </ul>
 *
 * <p>The {@code search_path} is NOT set here; each execution fixes it per
 * transaction with {@code SET LOCAL} (Fase 2). Pools are lazy
 * ({@code initializationFailTimeout = -1}, {@code minimumIdle = 0}) so the
 * application still boots if the read-only roles do not exist yet (e.g. before
 * the V9 migration runs on a fresh database).
 */
@Configuration
public class ReadOnlyDataSourceConfig {

    @Bean(name = "tenantReadOnlyDataSource")
    public DataSource tenantReadOnlyDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${TENANT_READONLY_DB_USER:finance_tenant_readonly}") String username,
            @Value("${TENANT_READONLY_DB_PASSWORD:tenant_ro_pass}") String password
    ) {
        return buildReadOnlyPool(url, username, password, "reporting-tenant-readonly-pool");
    }

    @Bean(name = "platformReadOnlyDataSource")
    public DataSource platformReadOnlyDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${PLATFORM_READONLY_DB_USER:finance_platform_readonly}") String username,
            @Value("${PLATFORM_READONLY_DB_PASSWORD:platform_ro_pass}") String password
    ) {
        return buildReadOnlyPool(url, username, password, "reporting-platform-readonly-pool");
    }

    private HikariDataSource buildReadOnlyPool(String url, String username, String password, String poolName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setPoolName(poolName);
        ds.setReadOnly(true);
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(0);
        // Lazy: do not fail application startup if the role is not provisioned yet.
        ds.setInitializationFailTimeout(-1);
        return ds;
    }
}
