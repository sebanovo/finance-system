package com.financesystem.finance_api.modules.governance.backups.infrastructure.engine;

import com.financesystem.finance_api.modules.governance.backups.application.config.BackupProperties;
import com.financesystem.finance_api.modules.governance.backups.infrastructure.storage.ChecksumService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PgBackupEngineAdapterTest {

    @Test
    void dumpFullDatabaseCommandExcludesBackupJobsTable() {
        BackupProperties properties = new BackupProperties();
        properties.setPgDumpPath("pg_dump");
        properties.setHost("db");
        properties.setPort("5432");
        properties.setUsername("finance_user");
        properties.setDatabase("finance_db");

        PgBackupEngineAdapter adapter = new PgBackupEngineAdapter(properties, new ChecksumService());

        var command = adapter.buildDumpFullDatabaseCommand(Path.of("/tmp/full-backup.dump"));

        assertTrue(command.contains("--exclude-table=public.backup_jobs"));
    }
}
