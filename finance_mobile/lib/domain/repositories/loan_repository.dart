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

  Future<List<LoanModel>> getLoans({int page = 0, int size = 50});
  Future<List<LoanInstallmentModel>> getLoanSchedule(String loanId);
  Future<LoanModel> requestLoan({
    required String userId,
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  });
  Future<LoanModel> approveLoan(String loanId);
  Future<LoanModel> rejectLoan(String loanId, {String? reason});
  Future<LoanModel> disburseLoan(String loanId);
  Future<LoanModel> payLoan(String loanId, double amount);
}
