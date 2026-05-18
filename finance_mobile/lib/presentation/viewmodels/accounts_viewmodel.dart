import 'package:flutter/material.dart';
import '../../../domain/usecases/get_accounts_usecase.dart';
import '../../../domain/usecases/create_account_usecase.dart';
import '../../../domain/entities/account.dart';

class AccountsViewModel extends ChangeNotifier {
  final GetAccountsUseCase getAccountsUseCase;
  final CreateAccountUseCase createAccountUseCase;

  List<Account> _accounts = [];
  bool _loading = false;
  bool _creating = false;
  String? _errorMessage;
  bool _accountCreated = false;

  AccountsViewModel({
    required this.getAccountsUseCase,
    required this.createAccountUseCase,
  });

  List<Account> get accounts => _accounts;
  bool get loading => _loading;
  bool get creating => _creating;
  String? get errorMessage => _errorMessage;
  bool get accountCreated => _accountCreated;

  void clearAccountCreated() {
    _accountCreated = false;
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

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
      await loadAccounts(); // Recargar lista después de crear
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }

  double get totalBalance {
    return _accounts.fold(0, (sum, account) => sum + account.totalBalance);
  }

  String get formattedTotalBalance {
    return '${totalBalance.toStringAsFixed(2)} ${_accounts.isNotEmpty ? _accounts.first.currency : 'BOB'}';
  }
}
