import '../repositories/account_repository.dart';
import '../entities/transaction.dart';

class GetAccountTransactionsUseCase {
  final AccountRepository repository;

  GetAccountTransactionsUseCase(this.repository);

  Future<List<Transaction>> call(String accountId) {
    return repository.getAccountTransactions(accountId);
  }
}
