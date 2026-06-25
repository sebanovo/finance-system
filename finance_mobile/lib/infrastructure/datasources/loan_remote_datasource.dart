import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/infrastructure/models/loan_model.dart';

abstract class LoanRemoteDataSource {
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

class LoanRemoteDataSourceImpl implements LoanRemoteDataSource {
  final ApiClient apiClient;

  LoanRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<LoanModel>> getMyLoans() async {
    final response = await apiClient.get('/api/me/loans');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((j) => LoanModel.fromJson(j as Map<String, dynamic>)).toList();
      }
      throw Exception(data['message'] ?? 'Error al obtener préstamos');
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<List<LoanInstallmentModel>> getMyLoanSchedule(String loanId) async {
    final response = await apiClient.get('/api/me/loans/$loanId/schedule');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((j) => LoanInstallmentModel.fromJson(j as Map<String, dynamic>)).toList();
      }
      throw Exception(data['message'] ?? 'Error al obtener cronograma');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<LoanModel> requestMyLoan({
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  }) async {
    final body = {
      'accountId': accountId,
      'principal': principal,
      'annualInterestRate': annualInterestRate,
      'termMonths': termMonths,
      'interestMethod': interestMethod,
      if (purpose != null && purpose.trim().isNotEmpty) 'purpose': purpose.trim(),
    };
    final response = await apiClient.post('/api/me/loans', body);
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return LoanModel.fromJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'Error al solicitar préstamo');
    }
    final data = response.data;
    if (data is Map<String, dynamic>) {
      throw Exception(data['message'] ?? 'Error al solicitar préstamo');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<void> payMyLoan(String loanId, double amount) async {
    final response = await apiClient.post('/api/me/loans/$loanId/payments', {'amount': amount});
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] != true) {
        throw Exception(data['message'] ?? 'Error al registrar pago');
      }
      return;
    }
    final data = response.data;
    if (data is Map<String, dynamic>) {
      throw Exception(data['message'] ?? 'Error al registrar pago');
    }
    throw Exception('Error ${response.statusCode}');
  }
}
