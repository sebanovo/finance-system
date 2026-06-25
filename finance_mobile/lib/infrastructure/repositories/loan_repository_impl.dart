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
}
