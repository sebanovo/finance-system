import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';

class GetTransactionByIdUseCase {
  final TransactionRepository repository;

  GetTransactionByIdUseCase(this.repository);

  Future<Transaction> call(String id) => repository.getTransactionById(id);
}
