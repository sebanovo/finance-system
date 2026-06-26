package com.financesystem.finance_api.modules.governance.backups.infrastructure.engine;

import com.financesystem.finance_api.modules.governance.backups.domain.model.BackupArtifact;
import java.nio.file.Path;

public interface BackupEngine {

    BackupArtifact dumpSchema(String schemaName, Path target);

    BackupArtifact dumpFullDatabase(Path target);

    String restoreSchema(String schemaName, Path source);

    String restoreFullDatabase(Path source);
}
