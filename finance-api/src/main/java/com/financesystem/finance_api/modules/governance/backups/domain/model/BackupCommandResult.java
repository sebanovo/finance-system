package com.financesystem.finance_api.modules.governance.backups.domain.model;

public record BackupCommandResult(
        boolean success,
        boolean warnings,
        int exitCode,
        String output
) {
    public static BackupCommandResult success(int exitCode, String output) {
        return new BackupCommandResult(true, false, exitCode, output);
    }

    public static BackupCommandResult warning(int exitCode, String output) {
        return new BackupCommandResult(true, true, exitCode, output);
    }
}
