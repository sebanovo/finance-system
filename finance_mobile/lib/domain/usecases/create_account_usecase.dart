import '../repositories/account_repository.dart';
import '../entities/account.dart';

class CreateAccountUseCase {
  final AccountRepository repository;

  CreateAccountUseCase(this.repository);

  Future<Account> call({
    required String accountName,
    required String accountType,
    required String currency,
    required String customAlias,
  }) {
    return repository.createAccount(
      accountName: accountName,
      accountType: accountType,
      currency: currency,
      customAlias: customAlias,
    );
  }
}
