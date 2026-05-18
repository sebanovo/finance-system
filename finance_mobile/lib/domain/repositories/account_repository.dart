import '../entities/account.dart';
import '../entities/account_balance.dart';
import '../entities/transaction.dart';

abstract class AccountRepository {
  Future<List<Account>> getAccounts();
  Future<Account> getAccountById(String accountId);
  Future<AccountBalance> getAccountBalance(String accountId);
  Future<Account> updateAccountAlias(String accountId, String customAlias);
  Future<List<Transaction>> getAccountTransactions(String accountId);
  Future<Account> createAccount({
    required String accountName,
    required String accountType,
    required String currency,
    required String customAlias,
  });
}
