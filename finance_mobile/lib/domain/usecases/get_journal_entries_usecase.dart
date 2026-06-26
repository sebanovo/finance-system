import '../entities/journal_entry.dart';
import '../repositories/accounting_repository.dart';

class GetJournalEntriesUseCase {
  final AccountingRepository repository;

  GetJournalEntriesUseCase(this.repository);

  Future<JournalEntriesPage> call({int page = 0, int size = 20}) {
    return repository.listJournalEntries(page: page, size: size);
  }
}
