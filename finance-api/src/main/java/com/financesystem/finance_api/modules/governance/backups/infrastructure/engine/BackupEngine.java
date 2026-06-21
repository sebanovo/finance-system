package com.financesystem.finance_api.modules.governance.backups.infrastructure.engine;

import com.financesystem.finance_api.modules.governance.backups.domain.model.BackupArtifact;
import com.financesystem.finance_api.modules.governance.backups.domain.model.BackupCommandResult;
import java.nio.file.Path;

public interface BackupEngine {

    BackupArtifact dumpSchema(String schemaName, Path target);

    BackupArtifact dumpFullDatabase(Path target);

    BackupCommandResult restoreSchema(String schemaName, Path source);

    BackupCommandResult restoreFullDatabase(Path source);
}
