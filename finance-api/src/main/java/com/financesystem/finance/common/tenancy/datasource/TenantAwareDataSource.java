package com.financesystem.finance.common.tenancy.datasource;

import com.financesystem.finance.common.tenancy.TenancyProperties;
import com.financesystem.finance.common.tenancy.context.TenantContextHolder;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TenantAwareDataSource extends AbstractDataSource {

    private final DataSource delegate;
    private final TenancyProperties tenancyProperties;

    public TenantAwareDataSource(DataSource delegate, TenancyProperties tenancyProperties) {
        this.delegate = delegate;
        this.tenancyProperties = tenancyProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = delegate.getConnection();
        applySchema(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = delegate.getConnection(username, password);
        applySchema(connection);
        return connection;
    }

    private void applySchema(Connection connection) throws SQLException {
        String schemaName = TenantContextHolder.getCurrentSchemaOrDefault(tenancyProperties.getPublicSchema());

        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO " + schemaName + ", " + tenancyProperties.getPublicSchema());
        }
    }
}