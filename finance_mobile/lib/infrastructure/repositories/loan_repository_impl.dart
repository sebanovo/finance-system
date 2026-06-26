import 'package:finance_mobile/domain/repositories/loan_repository.dart';
import 'package:finance_mobile/infrastructure/datasources/loan_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/models/loan_model.dart';

class LoanRepositoryImpl implements LoanRepository {
  final LoanRemoteDataSource remoteDataSource;

  LoanRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<LoanModel>> getMyLoans() => remoteDataSource.getMyLoans();

  @override
  Future<List<LoanInstallmentModel>> getMyLoanSchedule(String loanId) =>
      remoteDataSource.getMyLoanSchedule(loanId);

  @override
  Future<LoanModel> requestMyLoan({
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  }) =>
      remoteDataSource.requestMyLoan(
        accountId: accountId,
        principal: principal,
        annualInterestRate: annualInterestRate,
        termMonths: termMonths,
        interestMethod: interestMethod,
        purpose: purpose,
      );

  @override
  Future<void> payMyLoan(String loanId, double amount) =>
      remoteDataSource.payMyLoan(loanId, amount);

  @override
  Future<List<LoanModel>> getLoans({int page = 0, int size = 50}) =>
      remoteDataSource.getLoans(page: page, size: size);

  @override
  Future<List<LoanInstallmentModel>> getLoanSchedule(String loanId) =>
      remoteDataSource.getLoanSchedule(loanId);

  @override
  Future<LoanModel> requestLoan({
    required String userId,
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  }) =>
      remoteDataSource.requestLoan(
        userId: userId,
        accountId: accountId,
        principal: principal,
        annualInterestRate: annualInterestRate,
        termMonths: termMonths,
        interestMethod: interestMethod,
        purpose: purpose,
      );

  @override
  Future<LoanModel> approveLoan(String loanId) => remoteDataSource.approveLoan(loanId);

  @override
  Future<LoanModel> rejectLoan(String loanId, {String? reason}) =>
      remoteDataSource.rejectLoan(loanId, reason: reason);

  @override
  Future<LoanModel> disburseLoan(String loanId) => remoteDataSource.disburseLoan(loanId);

  @override
  Future<LoanModel> payLoan(String loanId, double amount) =>
      remoteDataSource.payLoan(loanId, amount);
}
