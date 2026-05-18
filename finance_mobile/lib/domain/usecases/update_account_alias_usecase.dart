import '../repositories/account_repository.dart';
import '../entities/account.dart';

class UpdateAccountAliasUseCase {
  final AccountRepository repository;

  UpdateAccountAliasUseCase(this.repository);

  Future<Account> call(String accountId, String customAlias) {
    return repository.updateAccountAlias(accountId, customAlias);
  }
}
