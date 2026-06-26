import '../entities/backup_record.dart';
import '../repositories/backups_repository.dart';

class CreateBackupUseCase {
  final BackupsRepository repository;
  CreateBackupUseCase(this.repository);

  Future<BackupRecord> call({String? reason}) {
    return repository.createBackup(reason: reason);
  }
}
