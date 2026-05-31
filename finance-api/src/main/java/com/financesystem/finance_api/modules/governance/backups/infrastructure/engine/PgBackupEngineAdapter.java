package com.financesystem.finance_api.modules.governance.backups.infrastructure.engine;

import com.financesystem.finance_api.modules.governance.backups.application.config.BackupProperties;
import com.financesystem.finance_api.modules.governance.backups.domain.exception.BackupExecutionException;
import com.financesystem.finance_api.modules.governance.backups.domain.model.BackupArtifact;
import com.financesystem.finance_api.modules.governance.backups.infrastructure.storage.ChecksumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class PgBackupEngineAdapter implements BackupEngine {

    private static final Logger log = LoggerFactory.getLogger(PgBackupEngineAdapter.class);
    private final BackupProperties properties;
    private final ChecksumService checksumService;

    public PgBackupEngineAdapter(BackupProperties properties, ChecksumService checksumService) {
        this.properties = properties;
        this.checksumService = checksumService;
    }

    @Override
    public BackupArtifact dumpSchema(String schemaName, Path target) {
        validateSqlIdentifier(schemaName, "schemaName");
        run(List.of(properties.getPgDumpPath(), "-h", properties.getHost(), "-p", properties.getPort(), "-U", properties.getUsername(), "-d", properties.getDatabase(), "-n", schemaName, "-Fc", "-f", target.toString()), "pg_dump schema " + schemaName);
        return artifact(target);
    }

    @Override
    public BackupArtifact dumpFullDatabase(Path target) {
        run(List.of(properties.getPgDumpPath(), "-h", properties.getHost(), "-p", properties.getPort(), "-U", properties.getUsername(), "-d", properties.getDatabase(), "-Fc", "-f", target.toString()), "pg_dump full database");
        return artifact(target);
    }

    @Override
    public void restoreSchema(String schemaName, Path source) {
        validateSqlIdentifier(schemaName, "schemaName");
        if (!Files.exists(source)) {
            throw new BackupExecutionException("Backup artifact does not exist: " + source);
        }
        executePsql("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE;");
        //executePsql("CREATE SCHEMA IF NOT EXISTS " + schemaName + ";"); // El Dump creara el esquema
        run(List.of(
            properties.getPgRestorePath(), 
            "-h", properties.getHost(), 
            "-p", properties.getPort(), 
            "-U", properties.getUsername(), 
            "-d", properties.getDatabase(), 
            "--no-owner", "--no-privileges", 
            source.toString()), 
            "pg_restore schema " + schemaName);
    }

    @Override
    public void restoreFullDatabase(Path source) {
        if (!Files.exists(source)) {
            throw new BackupExecutionException("Backup artifact does not exist: " + source);
        }
        run(List.of(properties.getPgRestorePath(), "-h", properties.getHost(), "-p", properties.getPort(), "-U", properties.getUsername(), "-d", properties.getDatabase(), "--clean", "--if-exists", "--no-owner", "--no-privileges", source.toString()), "pg_restore full database");
    }

    private void executePsql(String sql) {
        run(List.of(properties.getPsqlPath(), "-h", properties.getHost(), "-p", properties.getPort(), "-U", properties.getUsername(), "-d", properties.getDatabase(), "-v", "ON_ERROR_STOP=1", "-c", sql), "psql command");
    }

    private BackupArtifact artifact(Path target) {
        try {
            return new BackupArtifact(target, Files.size(target), checksumService.sha256(target));
        } catch (Exception e) {
            throw new BackupExecutionException("Unable to calculate artifact metadata", e);
        }
    }

    private void run(List<String> command, String description) {
        try {
            log.info("Executing backup command: {}", description);
            ProcessBuilder pb = new ProcessBuilder(new ArrayList<>(command));
            pb.environment().put("PGPASSWORD", properties.getPassword());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            StringBuilder out = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    out.append(line).append(System.lineSeparator());
                }
            }
            int code = p.waitFor();
            if (code != 0) {
                throw new BackupExecutionException("Command failed: " + description + ". Exit code: " + code + ". Output: " + out);
            }
        } catch (BackupExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new BackupExecutionException("Unable to execute command: " + description, e);
        }
    }

    private void validateSqlIdentifier(String v, String f) {
        if (v == null || v.isBlank() || !v.matches("^[a-zA-Z0-9_]+$")) {
            throw new BackupExecutionException(f + " is invalid: " + v);
    
        }}
}
