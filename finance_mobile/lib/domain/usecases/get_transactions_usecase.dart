import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';

class GetTransactionsUseCase {
  final TransactionRepository repository;

  GetTransactionsUseCase(this.repository);

  Future<List<Transaction>> call() => repository.getTransactions();
}
