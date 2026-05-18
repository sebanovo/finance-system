import '../repositories/account_repository.dart';
import '../entities/account_balance.dart';

class GetAccountBalanceUseCase {
  final AccountRepository repository;

  GetAccountBalanceUseCase(this.repository);

  Future<AccountBalance> call(String accountId) {
    return repository.getAccountBalance(accountId);
  }
}
