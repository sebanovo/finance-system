import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/hold_request.dart';

class CreateHoldUseCase {
  final TransactionRepository repository;

  CreateHoldUseCase(this.repository);

  Future<Transaction> call(HoldRequest request) =>
      repository.createHold(request);
}
