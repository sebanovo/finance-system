package com.financesystem.finance_api.shared.service;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class TenantService {
    private final DataSource dataSource;

    public TenantService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTenant(String tenant, String adminName, String adminEmail, String encodedPassword) {
        if (tenant == null || !tenant.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Nombre de tenant inválido. Use solo letras, números y guiones bajos.");
        }

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "CREATE SCHEMA IF NOT EXISTS \"" + tenant + "\"";
            stmt.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Error creando el schema: " + e.getMessage(), e);
        }

        // Ejecuta migraciones Flyway contra el schema recién creado
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(tenant)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        // Inserta el usuario administrador
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET search_path TO \"" + tenant + "\"");
            String query = String.format(
                    "INSERT INTO users (name, email, password, role_id) SELECT '%s', '%s', '%s', id FROM roles WHERE name = 'ROLE_ADMIN'",
                    adminName, adminEmail, encodedPassword);

            stmt.execute(query);
        } catch (Exception e) {
            throw new RuntimeException("Error insertando el administrador: " + e.getMessage(), e);
        }
    }
}
