import '../entities/journal_entry.dart';
import '../repositories/accounting_repository.dart';

class GetJournalEntryUseCase {
  final AccountingRepository repository;
  GetJournalEntryUseCase(this.repository);

  Future<JournalEntry> call(String id) => repository.getJournalEntryById(id);
}
