import '../entities/accounting_period.dart';
import '../repositories/accounting_repository.dart';

class CreateAccountingPeriodUseCase {
  final AccountingRepository repository;

  CreateAccountingPeriodUseCase(this.repository);

  Future<AccountingPeriod> call({
    required String periodCode,
    required String periodType,
    required String startDate,
    required String endDate,
    String? description,
  }) {
    return repository.createPeriod(
      periodCode: periodCode,
      periodType: periodType,
      startDate: startDate,
      endDate: endDate,
      description: description,
    );
  }
}
