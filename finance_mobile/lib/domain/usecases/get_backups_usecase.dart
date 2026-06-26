import '../entities/backup_page.dart';
import '../repositories/backups_repository.dart';

class GetBackupsUseCase {
  final BackupsRepository repository;
  GetBackupsUseCase(this.repository);

  Future<BackupPage> call({int page = 0, int size = 20}) {
    return repository.getBackups(page: page, size: size);
  }
}
