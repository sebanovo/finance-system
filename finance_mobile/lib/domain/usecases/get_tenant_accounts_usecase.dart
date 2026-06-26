import '../entities/account.dart';
import '../repositories/account_repository.dart';

class GetTenantAccountsUseCase {
  final AccountRepository repository;

  GetTenantAccountsUseCase(this.repository);

  Future<List<Account>> call() => repository.getTenantAccounts();
}
