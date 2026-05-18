import 'package:flutter/material.dart';
import '../../../domain/usecases/get_accounts_usecase.dart';
import '../../../domain/usecases/get_account_by_id_usecase.dart';
import '../../../domain/usecases/get_account_balance_usecase.dart';
import '../../../domain/usecases/update_account_alias_usecase.dart';
import '../../../domain/usecases/get_account_transactions_usecase.dart';
import '../../../domain/usecases/create_account_usecase.dart';
import '../../../domain/entities/account.dart';
import '../../../domain/entities/account_balance.dart';
import '../../../domain/entities/transaction.dart';

class AccountsViewModel extends ChangeNotifier {
  final GetAccountsUseCase getAccountsUseCase;
  final GetAccountByIdUseCase getAccountByIdUseCase;
  final GetAccountBalanceUseCase getAccountBalanceUseCase;
  final UpdateAccountAliasUseCase updateAccountAliasUseCase;
  final GetAccountTransactionsUseCase getAccountTransactionsUseCase;
  final CreateAccountUseCase createAccountUseCase;

  List<Account> _accounts = [];
  Account? _selectedAccount;
  AccountBalance? _accountBalance;
  List<Transaction> _transactions = [];
  bool _loading = false;
  bool _creating = false;
  bool _updating = false;
  String? _errorMessage;
  bool _accountCreated = false;
  bool _aliasUpdated = false;

  AccountsViewModel({
    required this.getAccountsUseCase,
    required this.getAccountByIdUseCase,
    required this.getAccountBalanceUseCase,
    required this.updateAccountAliasUseCase,
    required this.getAccountTransactionsUseCase,
    required this.createAccountUseCase,
  });

  // Getters
  List<Account> get accounts => _accounts;
  Account? get selectedAccount => _selectedAccount;
  AccountBalance? get accountBalance => _accountBalance;
  List<Transaction> get transactions => _transactions;
  bool get loading => _loading;
  bool get creating => _creating;
  bool get updating => _updating;
  String? get errorMessage => _errorMessage;
  bool get accountCreated => _accountCreated;
  bool get aliasUpdated => _aliasUpdated;

  void clearAccountCreated() {
    _accountCreated = false;
  }

  void clearAliasUpdated() {
    _aliasUpdated = false;
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  double get totalBalance {
    return _accounts.fold(0, (sum, account) => sum + account.totalBalance);
  }

  String get formattedTotalBalance {
    return '${totalBalance.toStringAsFixed(2)} ${_accounts.isNotEmpty ? _accounts.first.currency : 'BOB'}';
  }

  // Cargar todas las cuentas
  Future<void> loadAccounts() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _accounts = await getAccountsUseCase();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  // Cargar cuenta por ID
  Future<void> loadAccountById(String accountId) async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _selectedAccount = await getAccountByIdUseCase(accountId);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  // Cargar balance de una cuenta
  Future<void> loadAccountBalance(String accountId) async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _accountBalance = await getAccountBalanceUseCase(accountId);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  // Cargar transacciones de una cuenta
  Future<void> loadAccountTransactions(String accountId) async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _transactions = await getAccountTransactionsUseCase(accountId);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  // Actualizar alias de una cuenta
  Future<void> updateAlias(String accountId, String customAlias) async {
    _updating = true;
    _errorMessage = null;
    _aliasUpdated = false;
    notifyListeners();

    try {
      await updateAccountAliasUseCase(accountId, customAlias);
      _aliasUpdated = true;
      await loadAccounts(); // Recargar lista
      if (_selectedAccount?.id == accountId) {
        await loadAccountById(accountId);
      }
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _updating = false;
      notifyListeners();
    }
  }

  // Crear nueva cuenta
  Future<void> createAccount({
    required String accountName,
    required String accountType,
    required String currency,
    required String customAlias,
  }) async {
    _creating = true;
    _errorMessage = null;
    _accountCreated = false;
    notifyListeners();

    try {
      await createAccountUseCase(
        accountName: accountName,
        accountType: accountType,
        currency: currency,
        customAlias: customAlias,
      );
      _accountCreated = true;
      await loadAccounts();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }
}
