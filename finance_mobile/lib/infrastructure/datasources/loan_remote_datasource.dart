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

class LoanRemoteDataSourceImpl implements LoanRemoteDataSource {
  final ApiClient apiClient;

  LoanRemoteDataSourceImpl(this.apiClient);

  Map<String, dynamic> _asMap(dynamic value) {
    if (value is Map<String, dynamic>) return value;
    return <String, dynamic>{};
  }

  dynamic _payload(dynamic body) {
    if (body is Map<String, dynamic> && body['data'] != null) {
      return body['data'];
    }
    return body;
  }

  List<dynamic> _extractList(dynamic payload) {
    if (payload is List) return payload;
    if (payload is Map<String, dynamic>) {
      final content = payload['content'];
      if (content is List) return content;
      final data = payload['data'];
      if (data is List) return data;
    }
    return const [];
  }

  Map<String, dynamic> _extractLoanMap(dynamic payload) {
    if (payload is Map<String, dynamic>) {
      final nestedLoan = payload['loan'];
      if (nestedLoan is Map<String, dynamic>) {
        return nestedLoan;
      }
      final nestedData = payload['data'];
      if (nestedData is Map<String, dynamic> &&
          nestedData['loan'] is Map<String, dynamic>) {
        return nestedData['loan'] as Map<String, dynamic>;
      }
      return payload;
    }
    return <String, dynamic>{};
  }

  List<LoanModel> _parseLoanList(dynamic responseBody) {
    final body = _asMap(responseBody);
    final payload = _payload(body);
    final list = _extractList(payload);
    return list
        .whereType<Map<String, dynamic>>()
        .map(LoanModel.fromJson)
        .toList();
  }

  LoanModel _parseSingleLoan(dynamic responseBody) {
    final body = _asMap(responseBody);
    final payload = _payload(body);
    final loanMap = _extractLoanMap(payload);
    if (loanMap.isEmpty) {
      throw Exception(body['message'] ?? 'Error al procesar el préstamo');
    }
    return LoanModel.fromJson(loanMap);
  }

  Future<dynamic> _unwrapResponse(
    String path, {
    String method = 'GET',
    Map<String, dynamic>? body,
  }) async {
    switch (method) {
      case 'POST':
        return apiClient.post(path, body ?? {});
      case 'PATCH':
        return apiClient.patch(path, body ?? {});
      default:
        return apiClient.get(path);
    }
  }

  @override
  Future<List<LoanModel>> getMyLoans() async {
    final response = await _unwrapResponse('/api/me/loans');
    if (response.statusCode == 200) {
      final body = _asMap(response.data);
      if (body['success'] == true) {
        final list = _extractList(_payload(body));
        return list
            .whereType<Map<String, dynamic>>()
            .map(LoanModel.fromJson)
            .toList();
      }
      throw Exception(body['message'] ?? 'Error al obtener préstamos');
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<List<LoanInstallmentModel>> getMyLoanSchedule(String loanId) async {
    final response = await _unwrapResponse('/api/me/loans/$loanId/schedule');
    if (response.statusCode == 200) {
      final body = _asMap(response.data);
      if (body['success'] == true) {
        final list = _extractList(_payload(body));
        return list
            .whereType<Map<String, dynamic>>()
            .map(LoanInstallmentModel.fromJson)
            .toList();
      }
      throw Exception(body['message'] ?? 'Error al obtener cronograma');
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
      if (purpose != null && purpose.trim().isNotEmpty)
        'purpose': purpose.trim(),
    };
    final response = await _unwrapResponse(
      '/api/me/loans',
      method: 'POST',
      body: body,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al solicitar préstamo');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al solicitar préstamo');
  }

  @override
  Future<void> payMyLoan(String loanId, double amount) async {
    final response = await _unwrapResponse(
      '/api/me/loans/$loanId/payments',
      method: 'POST',
      body: {'amount': amount},
    );
    if (response.statusCode == 200) {
      final body = _asMap(response.data);
      if (body['success'] != true) {
        throw Exception(body['message'] ?? 'Error al registrar pago');
      }
      return;
    }
    final body = _asMap(response.data);
    throw Exception(body['message'] ?? 'Error al registrar pago');
  }

  @override
  Future<List<LoanModel>> getLoans({int page = 0, int size = 50}) async {
    final response = await apiClient.get('/api/loans?page=$page&size=$size');
    if (response.statusCode == 200) {
      final body = _asMap(response.data);
      if (body['success'] == true) {
        return _parseLoanList(body);
      }
      throw Exception(body['message'] ?? 'Error al obtener préstamos');
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<List<LoanInstallmentModel>> getLoanSchedule(String loanId) async {
    final response = await apiClient.get('/api/loans/$loanId/schedule');
    if (response.statusCode == 200) {
      final body = _asMap(response.data);
      if (body['success'] == true) {
        final list = _extractList(_payload(body));
        return list
            .whereType<Map<String, dynamic>>()
            .map(LoanInstallmentModel.fromJson)
            .toList();
      }
      throw Exception(body['message'] ?? 'Error al obtener cronograma');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<LoanModel> requestLoan({
    required String userId,
    required String accountId,
    required double principal,
    required double annualInterestRate,
    required int termMonths,
    required String interestMethod,
    String? purpose,
  }) async {
    final body = {
      'userId': userId,
      'accountId': accountId,
      'principal': principal,
      'annualInterestRate': annualInterestRate,
      'termMonths': termMonths,
      'interestMethod': interestMethod,
      if (purpose != null && purpose.trim().isNotEmpty)
        'purpose': purpose.trim(),
    };
    final response = await apiClient.post('/api/loans', body);
    if (response.statusCode == 200 || response.statusCode == 201) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al crear préstamo');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al crear préstamo');
  }

  @override
  Future<LoanModel> approveLoan(String loanId) async {
    final response = await apiClient.patch('/api/loans/$loanId/approve', {});
    if (response.statusCode == 200) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al aprobar préstamo');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al aprobar préstamo');
  }

  @override
  Future<LoanModel> rejectLoan(String loanId, {String? reason}) async {
    final response = await apiClient.patch(
      '/api/loans/$loanId/reject${reason != null && reason.trim().isNotEmpty ? '?reason=${Uri.encodeComponent(reason.trim())}' : ''}',
      {},
    );
    if (response.statusCode == 200) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al rechazar préstamo');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al rechazar préstamo');
  }

  @override
  Future<LoanModel> disburseLoan(String loanId) async {
    final response = await apiClient.post('/api/loans/$loanId/disburse', {});
    if (response.statusCode == 200) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al desembolsar préstamo');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al desembolsar préstamo');
  }

  @override
  Future<LoanModel> payLoan(String loanId, double amount) async {
    final response = await apiClient.post('/api/loans/$loanId/payments', {
      'amount': amount,
    });
    if (response.statusCode == 200) {
      final map = _asMap(response.data);
      if (map['success'] == true) {
        return _parseSingleLoan(map);
      }
      throw Exception(map['message'] ?? 'Error al registrar pago');
    }
    final map = _asMap(response.data);
    throw Exception(map['message'] ?? 'Error al registrar pago');
  }
}
