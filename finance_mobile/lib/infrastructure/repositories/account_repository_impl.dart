// lib/features/accounts/data/repositories/account_repository_impl.dart
import '../../../domain/entities/account.dart';
import '../../../domain/entities/account_balance.dart';
import '../../../domain/entities/transaction.dart';
import '../../../domain/repositories/account_repository.dart';
import '../datasources/account_remote_datasource.dart';
import '../models/create_account_request.dart';

class AccountRepositoryImpl implements AccountRepository {
  final AccountRemoteDataSource remoteDataSource;

  AccountRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<Account>> getAccounts() async {
    final models = await remoteDataSource.getAccounts();
    return models.map((model) => model.toEntity()).toList();
  }

  @override
  Future<Account> getAccountById(String accountId) async {
    final model = await remoteDataSource.getAccountById(accountId);
    return model.toEntity();
  }

  @override
  Future<AccountBalance> getAccountBalance(String accountId) async {
    final model = await remoteDataSource.getAccountBalance(accountId);
    return model.toEntity();
  }

  @override
  Future<Account> updateAccountAlias(
    String accountId,
    String customAlias,
  ) async {
    final model = await remoteDataSource.updateAccountAlias(
      accountId,
      customAlias,
    );
    return model.toEntity();
  }

  @override
  Future<List<Transaction>> getAccountTransactions(String accountId) async {
    final models = await remoteDataSource.getAccountTransactions(accountId);
    return models.map((model) => model.toEntity()).toList();
  }

  @override
  Future<Account> createAccount({
    required String accountName,
    required String accountType,
    required String currency,
    required String customAlias,
  }) async {
    final request = CreateAccountRequest(
      accountName: accountName,
      accountType: accountType,
      currency: currency,
      customAlias: customAlias,
    );
    final model = await remoteDataSource.createAccount(request);
    return model.toEntity();
  }
}
