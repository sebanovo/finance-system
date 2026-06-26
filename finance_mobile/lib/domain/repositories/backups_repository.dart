import 'dart:typed_data';

import '../entities/backup_page.dart';
import '../entities/backup_record.dart';

abstract class BackupsRepository {
  Future<BackupPage> getBackups({int page, int size});
  Future<BackupRecord> createBackup({String? reason});
  Future<BackupRecord> restoreBackupFromFile({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  });
  Future<Uint8List> downloadBackup(String backupId);
}
