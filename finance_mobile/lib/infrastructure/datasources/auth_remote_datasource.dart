import '../../../../core/network/api_client.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';
import '../models/signup_request.dart';

abstract class AuthRemoteDataSource {
  Future<LoginResponse> login(String email, String password, String tenantSlug);
  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  );
  Future<void> signup(SignupRequest request);
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

  @override
  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  ) async {
    apiClient.setTenant(tenantSlug);
    final body = {'newPassword': newPassword, 'token': token};
    final response = await apiClient.post('/api/auth/reset-password', body);
    if (response.statusCode == 200) {
      final Map<String, dynamic> data = response.data;
      if (data['success'] != true) {
        throw Exception(data['message'] ?? 'Error al restablecer');
      }
    } else if (response.statusCode == 400 || response.statusCode == 401) {
      final Map<String, dynamic> error = response.data;
      throw Exception(error['message'] ?? 'Error al restablecer');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<void> signup(SignupRequest request) async {
    // El endpoint es público, no necesita tenant ni token
    // Pero podemos usar apiClient sin setear tenant (se enviará solo Content-Type)
    final response = await apiClient.post(
      '/api/public/signup',
      request.toJson(),
    );
    if (response.statusCode != 200 && response.statusCode != 201) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al registrar');
    }
  }
}
