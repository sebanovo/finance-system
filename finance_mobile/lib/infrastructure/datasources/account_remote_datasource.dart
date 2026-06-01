import '../../../../core/network/api_client.dart';
import '../models/account_model.dart';
import '../models/account_lookup_model.dart';
import '../models/account_balance_model.dart';
import '../models/transaction_model.dart';
import '../models/create_account_request.dart';

abstract class AccountRemoteDataSource {
  Future<List<AccountModel>> getAccounts();
  Future<AccountModel> getAccountById(String accountId);
  Future<AccountLookupModel> getAccountByNumber(String accountNumber);
  Future<AccountBalanceModel> getAccountBalance(String accountId);
  Future<AccountModel> updateAccountAlias(String accountId, String customAlias);
  Future<List<TransactionModel>> getAccountTransactions(String accountId);
  Future<AccountModel> createAccount(CreateAccountRequest request);
}

class AccountRemoteDataSourceImpl implements AccountRemoteDataSource {
  final ApiClient apiClient;

  AccountRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<AccountModel>> getAccounts() async {
    final response = await apiClient.get('/api/me/accounts');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => AccountModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al obtener cuentas');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<AccountModel> getAccountById(String accountId) async {
    final response = await apiClient.get('/api/me/accounts/$accountId');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return AccountModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al obtener cuenta');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Cuenta no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<AccountLookupModel> getAccountByNumber(String accountNumber) async {
    final response = await apiClient.get(
      '/api/me/accounts/lookup/${Uri.encodeComponent(accountNumber)}',
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return AccountLookupModel.fromJson(data['data'] as Map<String, dynamic>);
      } else {
        throw Exception(data['message'] ?? 'Error al buscar cuenta');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Cuenta no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<AccountBalanceModel> getAccountBalance(String accountId) async {
    final response = await apiClient.get('/api/me/accounts/$accountId/balance');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return AccountBalanceModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al obtener saldo');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Cuenta no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<AccountModel> updateAccountAlias(
    String accountId,
    String customAlias,
  ) async {
    final response = await apiClient.patch(
      '/api/me/accounts/$accountId/alias',
      {'customAlias': customAlias},
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return AccountModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al actualizar alias');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Cuenta no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<List<TransactionModel>> getAccountTransactions(
    String accountId,
  ) async {
    final response = await apiClient.get(
      '/api/me/accounts/$accountId/transactions',
    );
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
    } else if (response.statusCode == 404) {
      throw Exception('Cuenta no encontrada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<AccountModel> createAccount(CreateAccountRequest request) async {
    final response = await apiClient.post('/api/me/accounts', request.toJson());
    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return AccountModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al crear cuenta');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }
}
