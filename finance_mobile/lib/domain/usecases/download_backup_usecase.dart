import 'dart:typed_data';

import '../repositories/backups_repository.dart';

class DownloadBackupUseCase {
  final BackupsRepository repository;
  DownloadBackupUseCase(this.repository);

  Future<Uint8List> call(String backupId) {
    return repository.downloadBackup(backupId);
  }
}
