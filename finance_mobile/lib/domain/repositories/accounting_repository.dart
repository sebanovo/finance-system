import '../entities/accounting_period.dart';
import '../entities/journal_entry.dart';

abstract class AccountingRepository {
  Future<AccountingPeriodsPage> listPeriods({int page = 0, int size = 20});

  Future<AccountingPeriod> createPeriod({
    required String periodCode,
    required String periodType,
    required String startDate,
    required String endDate,
    String? description,
  });

  Future<AccountingPeriod> closePeriod(String id, {String? reason});

  Future<JournalEntriesPage> listJournalEntries({int page = 0, int size = 20});

  Future<JournalEntry> getJournalEntryById(String id);
}
