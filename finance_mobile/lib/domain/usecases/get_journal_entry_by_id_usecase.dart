import '../entities/journal_entry.dart';
import '../repositories/accounting_repository.dart';

class GetJournalEntryByIdUseCase {
  final AccountingRepository repository;

  GetJournalEntryByIdUseCase(this.repository);

  Future<JournalEntry> call(String id) {
    return repository.getJournalEntryById(id);
  }
}
