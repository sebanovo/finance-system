import '../entities/account_lookup.dart';
import '../repositories/account_repository.dart';

class GetAccountByNumberUseCase {
  final AccountRepository repository;

  GetAccountByNumberUseCase(this.repository);

  Future<AccountLookup> call(String accountNumber) {
    return repository.getAccountByNumber(accountNumber);
  }
}
