import '../../../../core/network/api_client.dart';
import '../models/account_model.dart';
import '../models/create_account_request.dart';

abstract class AccountRemoteDataSource {
  Future<List<AccountModel>> getAccounts();
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
