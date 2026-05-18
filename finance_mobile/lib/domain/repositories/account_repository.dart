import '../entities/account.dart';

abstract class AccountRepository {
  Future<List<Account>> getAccounts();
  Future<Account> createAccount({
    required String accountName,
    required String accountType,
    required String currency,
    required String customAlias,
  });
}
