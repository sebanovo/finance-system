import '../repositories/account_repository.dart';
import '../entities/account.dart';

class GetAccountByIdUseCase {
  final AccountRepository repository;

  GetAccountByIdUseCase(this.repository);

  Future<Account> call(String accountId) {
    return repository.getAccountById(accountId);
  }
}
