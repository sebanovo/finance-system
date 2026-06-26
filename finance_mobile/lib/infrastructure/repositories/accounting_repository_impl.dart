import '../../domain/entities/accounting_period.dart';
import '../../domain/entities/journal_entry.dart';
import '../../domain/repositories/accounting_repository.dart';
import '../datasources/accounting_remote_datasource.dart';

class AccountingRepositoryImpl implements AccountingRepository {
  final AccountingRemoteDataSource remoteDataSource;

  AccountingRepositoryImpl(this.remoteDataSource);

  @override
  Future<AccountingPeriodsPage> listPeriods({int page = 0, int size = 20}) async {
    final result = await remoteDataSource.listPeriods(page: page, size: size);
    return result.toEntity();
  }

  @override
  Future<AccountingPeriod> createPeriod({
    required String periodCode,
    required String periodType,
    required String startDate,
    required String endDate,
    String? description,
  }) async {
    final result = await remoteDataSource.createPeriod({
      'periodCode': periodCode,
      'periodType': periodType,
      'startDate': startDate,
      'endDate': endDate,
      if (description != null && description.trim().isNotEmpty) 'description': description.trim(),
    });
    return result.toEntity();
  }

  @override
  Future<AccountingPeriod> closePeriod(String id, {String? reason}) async {
    final result = await remoteDataSource.closePeriod(
      id,
      {
        if (reason != null && reason.trim().isNotEmpty) 'reason': reason.trim(),
      },
    );
    return result.toEntity();
  }

  @override
  Future<JournalEntriesPage> listJournalEntries({int page = 0, int size = 20}) async {
    final result = await remoteDataSource.listJournalEntries(page: page, size: size);
    return result.toEntity();
  }

  @override
  Future<JournalEntry> getJournalEntryById(String id) async {
    final result = await remoteDataSource.getJournalEntryById(id);
    return result.toEntity();
  }
}
