import '../entities/accounting_period.dart';
import '../repositories/accounting_repository.dart';

class GetAccountingPeriodsUseCase {
  final AccountingRepository repository;

  GetAccountingPeriodsUseCase(this.repository);

  Future<AccountingPeriodsPage> call({int page = 0, int size = 20}) {
    return repository.listPeriods(page: page, size: size);
  }
}
