import '../entities/accounting_period.dart';
import '../repositories/accounting_repository.dart';

class CloseAccountingPeriodUseCase {
  final AccountingRepository repository;

  CloseAccountingPeriodUseCase(this.repository);

  Future<AccountingPeriod> call(String id, {String? reason}) {
    return repository.closePeriod(id, reason: reason);
  }
}
