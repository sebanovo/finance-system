import 'dart:typed_data';

import '../entities/backup_record.dart';
import '../repositories/backups_repository.dart';

class RestoreBackupFromFileUseCase {
  final BackupsRepository repository;
  RestoreBackupFromFileUseCase(this.repository);

  Future<BackupRecord> call({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  }) {
    return repository.restoreBackupFromFile(
      fileBytes: fileBytes,
      fileName: fileName,
      confirmationText: confirmationText,
      reason: reason,
    );
  }
}
