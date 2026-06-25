import 'package:flutter/material.dart';
import '../../domain/entities/account.dart';
import '../../domain/repositories/loan_repository.dart';
import '../../domain/usecases/get_accounts_usecase.dart';
import '../../infrastructure/models/loan_model.dart';

class LoansViewModel extends ChangeNotifier {
  final LoanRepository loanRepository;
  final GetAccountsUseCase getAccountsUseCase;

  LoansViewModel({
    required this.loanRepository,
    required this.getAccountsUseCase,
  });

  bool _isLoading = false;
  bool _isSubmitting = false;
  String? _errorMessage;
  String? _successMessage;
  List<LoanModel> _loans = [];
  List<Account> _accounts = [];
  String? _expandedLoanId;
  List<LoanInstallmentModel> _schedule = [];

  bool get isLoading => _isLoading;
  bool get isSubmitting => _isSubmitting;
  String? get errorMessage => _errorMessage;
  String? get successMessage => _successMessage;
  List<LoanModel> get loans => _loans;
  List<Account> get accounts => _accounts;
  String? get expandedLoanId => _expandedLoanId;
  List<LoanInstallmentModel> get schedule => _schedule;

  Future<void> init() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      _loans = await loanRepository.getMyLoans();
      try {
        _accounts = await getAccountsUseCase();
      } catch (_) {
        _accounts = [];
      }
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> refreshLoans() async {
    try {
      _loans = await loanRepository.getMyLoans();
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }

  Future<bool> requestLoan({
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
      await loanRepository.requestMyLoan(
        accountId: accountId,
        principal: principal,
        annualInterestRate: annualInterestRate,
        termMonths: termMonths,
        interestMethod: interestMethod,
        purpose: purpose,
      );
      _successMessage = 'Solicitud enviada. Un administrador la revisará.';
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
      _schedule = await loanRepository.getMyLoanSchedule(loanId);
      _expandedLoanId = loanId;
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }

  Future<bool> payLoan(String loanId, double amount) async {
    _isSubmitting = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
    try {
      await loanRepository.payMyLoan(loanId, amount);
      _successMessage = 'Pago registrado.';
      await refreshLoans();
      if (_expandedLoanId == loanId) {
        _schedule = await loanRepository.getMyLoanSchedule(loanId);
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
