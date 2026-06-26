import 'dart:typed_data';

import '../../domain/entities/backup_page.dart';
import '../../domain/entities/backup_record.dart';
import '../../domain/repositories/backups_repository.dart';
import '../datasources/backups_remote_datasource.dart';

class BackupsRepositoryImpl implements BackupsRepository {
  final BackupsRemoteDataSource remoteDataSource;

  BackupsRepositoryImpl(this.remoteDataSource);

  @override
  Future<BackupPage> getBackups({int page = 0, int size = 20}) async {
    final model = await remoteDataSource.getBackups(page: page, size: size);
    return model.toEntity();
  }

  @override
  Future<BackupRecord> createBackup({String? reason}) async {
    final model = await remoteDataSource.createBackup(reason: reason);
    return model.toEntity();
  }

  @override
  Future<BackupRecord> restoreBackupFromFile({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  }) async {
    final model = await remoteDataSource.restoreBackupFromFile(
      fileBytes: fileBytes,
      fileName: fileName,
      confirmationText: confirmationText,
      reason: reason,
    );
    return model.toEntity();
  }

  @override
  Future<Uint8List> downloadBackup(String backupId) {
    return remoteDataSource.downloadBackup(backupId);
  }
}
