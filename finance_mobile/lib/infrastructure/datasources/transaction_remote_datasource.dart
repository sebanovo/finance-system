import '../../../../core/network/api_client.dart';
import '../models/transaction_model.dart';

abstract class TransactionRemoteDataSource {
  Future<List<TransactionModel>> getTransactions();
  Future<TransactionModel> getTransactionById(String id);
  Future<TransactionModel> createDeposit(Map<String, dynamic> request);
  Future<TransactionModel> createHold(Map<String, dynamic> request);
  Future<TransactionModel> createPayment(Map<String, dynamic> request);
  Future<TransactionModel> createRelease(Map<String, dynamic> request);
  Future<TransactionModel> createTransfer(Map<String, dynamic> request);
  Future<TransactionModel> createWithdrawal(Map<String, dynamic> request);
}

class TransactionRemoteDataSourceImpl implements TransactionRemoteDataSource {
  final ApiClient apiClient;

  TransactionRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<TransactionModel>> getTransactions() async {
    final response = await apiClient.get('/api/me/transactions');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => TransactionModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al obtener transacciones');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> getTransactionById(String id) async {
    final response = await apiClient.get('/api/me/transactions/$id');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al obtener transacción');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Transacción no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> createDeposit(Map<String, dynamic> request) async {
    try {
      final response = await apiClient.post(
        '/api/me/transactions/deposits',
        request,
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = response.data as Map<String, dynamic>;

        if (data['success'] == true) {
          return TransactionModel.fromJson(data['data']);
        } else {
          throw Exception(data['message'] ?? 'Error al crear depósito');
        }
      } else if (response.statusCode == 401) {
        throw Exception('Sesión expirada');
      } else {
        final error = response.data as Map<String, dynamic>;
        throw Exception(error['message'] ?? 'Error ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<TransactionModel> createHold(Map<String, dynamic> request) async {
    final response = await apiClient.post(
      '/api/me/transactions/holds',
      request,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear hold');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> createPayment(Map<String, dynamic> request) async {
    final response = await apiClient.post(
      '/api/me/transactions/payments',
      request,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear pago');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> createRelease(Map<String, dynamic> request) async {
    final response = await apiClient.post(
      '/api/me/transactions/releases',
      request,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear release');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> createTransfer(Map<String, dynamic> request) async {
    final response = await apiClient.post(
      '/api/me/transactions/transfers',
      request,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear transferencia');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }

  @override
  Future<TransactionModel> createWithdrawal(
    Map<String, dynamic> request,
  ) async {
    final response = await apiClient.post(
      '/api/me/transactions/withdrawals',
      request,
    );
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return TransactionModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear retiro');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }
}
