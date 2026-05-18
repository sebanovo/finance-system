import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/withdrawal_request.dart';

class CreateWithdrawalUseCase {
  final TransactionRepository repository;

  CreateWithdrawalUseCase(this.repository);

  Future<Transaction> call(WithdrawalRequest request) =>
      repository.createWithdrawal(request);
}
