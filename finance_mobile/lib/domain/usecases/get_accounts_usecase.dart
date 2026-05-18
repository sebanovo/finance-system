import '../repositories/account_repository.dart';
import '../entities/account.dart';

class GetAccountsUseCase {
  final AccountRepository repository;

  GetAccountsUseCase(this.repository);

  Future<List<Account>> call() => repository.getAccounts();
}
