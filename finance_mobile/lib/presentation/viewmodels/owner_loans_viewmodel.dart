import 'package:flutter/material.dart';
import 'package:finance_mobile/domain/entities/account.dart';
import 'package:finance_mobile/domain/entities/user.dart';
import 'package:finance_mobile/domain/repositories/loan_repository.dart';
import 'package:finance_mobile/domain/usecases/get_tenant_accounts_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_users_usecase.dart';
import 'package:finance_mobile/infrastructure/models/loan_model.dart';

class OwnerLoansViewModel extends ChangeNotifier {
  final LoanRepository loanRepository;
  final GetUsersUseCase getUsersUseCase;
  final GetTenantAccountsUseCase getTenantAccountsUseCase;

  OwnerLoansViewModel({
    required this.loanRepository,
    required this.getUsersUseCase,
    required this.getTenantAccountsUseCase,
  });

  bool _isLoading = false;
  bool _isSubmitting = false;
  String? _errorMessage;
  String? _successMessage;
  List<LoanModel> _loans = [];
  List<User> _users = [];
  List<Account> _accounts = [];
  String? _expandedLoanId;
  List<LoanInstallmentModel> _schedule = [];

  bool get isLoading => _isLoading;
  bool get isSubmitting => _isSubmitting;
  String? get errorMessage => _errorMessage;
  String? get successMessage => _successMessage;
  List<LoanModel> get loans => _loans;
  List<User> get users => _users;
  List<Account> get accounts => _accounts;
  String? get expandedLoanId => _expandedLoanId;
  List<LoanInstallmentModel> get schedule => _schedule;

  Future<void> init() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await Future.wait([
        _loadUsers(),
        _loadAccounts(),
        _loadLoans(),
      ]);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> _loadUsers() async {
    try {
      _users = await getUsersUseCase();
    } catch (_) {
      _users = [];
    }
  }

  Future<void> _loadAccounts() async {
    try {
      _accounts = await getTenantAccountsUseCase();
    } catch (_) {
      _accounts = [];
    }
  }

  Future<void> _loadLoans() async {
    _loans = await loanRepository.getLoans(page: 0, size: 100);
  }

  Future<void> refresh() async {
    _isLoading = true;
    notifyListeners();
    try {
      await Future.wait([
        _loadUsers(),
        _loadAccounts(),
        _loadLoans(),
      ]);
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> refreshLoans() async {
    try {
      await _loadLoans();
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }

  Future<bool> requestLoan({
    required String userId,
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  }) async {
    _isSubmitting = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
    try {
      await loanRepository.requestLoan(
        userId: userId,
        accountId: accountId,
        principal: principal,
        annualInterestRate: annualInterestRate,
        termMonths: termMonths,
        interestMethod: interestMethod,
        purpose: purpose,
      );
      _successMessage = 'Préstamo creado (REQUESTED).';
      await refreshLoans();
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isSubmitting = false;
      notifyListeners();
    }
  }

  Future<void> toggleSchedule(String loanId) async {
    if (_expandedLoanId == loanId) {
      _expandedLoanId = null;
      _schedule = [];
      notifyListeners();
      return;
    }
    try {
      _schedule = await loanRepository.getLoanSchedule(loanId);
      _expandedLoanId = loanId;
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    }
    notifyListeners();
  }

  Future<bool> approveLoan(String loanId) async {
    return _runLoanAction(
      () => loanRepository.approveLoan(loanId),
      'Préstamo aprobado.',
      loanId,
    );
  }

  Future<bool> rejectLoan(String loanId, {String? reason}) async {
    return _runLoanAction(
      () => loanRepository.rejectLoan(loanId, reason: reason),
      'Préstamo rechazado.',
      loanId,
    );
  }

  Future<bool> disburseLoan(String loanId) async {
    return _runLoanAction(
      () => loanRepository.disburseLoan(loanId),
      'Préstamo desembolsado.',
      loanId,
    );
  }

  Future<bool> payLoan(String loanId, double amount) async {
    _isSubmitting = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
    try {
      await loanRepository.payLoan(loanId, amount);
      _successMessage = 'Pago registrado.';
      await refreshLoans();
      if (_expandedLoanId == loanId) {
        _schedule = await loanRepository.getLoanSchedule(loanId);
      }
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isSubmitting = false;
      notifyListeners();
    }
  }

  Future<bool> _runLoanAction(
    Future<LoanModel> Function() action,
    String successMessage,
    String loanId,
  ) async {
    _isSubmitting = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
    try {
      await action();
      _successMessage = successMessage;
      await refreshLoans();
      if (_expandedLoanId == loanId) {
        _schedule = await loanRepository.getLoanSchedule(loanId);
      }
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isSubmitting = false;
      notifyListeners();
    }
  }

  void clearMessages() {
    _errorMessage = null;
    _successMessage = null;
  }
}
