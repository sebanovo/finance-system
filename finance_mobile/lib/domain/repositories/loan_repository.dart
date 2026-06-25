import 'package:finance_mobile/infrastructure/models/loan_model.dart';

abstract class LoanRepository {
  Future<List<LoanModel>> getMyLoans();
  Future<List<LoanInstallmentModel>> getMyLoanSchedule(String loanId);
  Future<LoanModel> requestMyLoan({
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  });
  Future<void> payMyLoan(String loanId, double amount);
}
