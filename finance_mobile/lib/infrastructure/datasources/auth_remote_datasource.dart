import '../../../../core/network/api_client.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';

abstract class AuthRemoteDataSource {
  Future<LoginResponse> login(String email, String password, String tenantSlug);
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
  final ApiClient apiClient;

  AuthRemoteDataSourceImpl(this.apiClient);

  @override
  Future<LoginResponse> login(
    String email,
    String password,
    String tenantSlug,
  ) async {
    // Temporal: establecemos el tenant para esta llamada (aunque el interceptor lo usará)
    apiClient.setTenant(tenantSlug);
    final request = LoginRequest(email: email, password: password);
    final response = await apiClient.post('/api/auth/login', request.toJson());

    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      return LoginResponse.fromJson(data);
    } else if (response.statusCode == 401) {
      throw Exception('Credenciales inválidas');
    } else {
      throw Exception(
        'Error ${response.statusCode}: ${response.statusMessage}',
      );
    }
  }
}
