import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/deposit_request.dart';

class CreateDepositUseCase {
  final TransactionRepository repository;

  CreateDepositUseCase(this.repository);

  Future<Transaction> call(DepositRequest request) =>
      repository.createDeposit(request);
}
