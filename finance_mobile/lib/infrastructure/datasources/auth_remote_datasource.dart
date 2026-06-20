import 'package:dio/dio.dart';
import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/infrastructure/models/forgot_password_request.dart';
import 'package:finance_mobile/infrastructure/models/login_request.dart';
import 'package:finance_mobile/infrastructure/models/login_response.dart';
import 'package:finance_mobile/infrastructure/models/signup_request.dart';
import 'package:finance_mobile/infrastructure/models/user_info_model.dart';

abstract class AuthRemoteDataSource {
  Future<LoginResponse> login(String email, String password, String tenantSlug);
  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  );
  Future<void> signup(SignupRequest request);
  Future<void> forgotPassword(String email, String tenantSlug);
  Future<void> changePassword(String currentPassword, String newPassword);
  Future<void> logout();
  Future<LoginResponse> faceLogin(String email, String tenantSlug, String imagePath);
  Future<UserInfoModel> getProfile();
  Future<UserInfoModel> updateProfile({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  });
  Future<UserInfoModel> removeProfilePhoto();
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

  @override
  Future<void> forgotPassword(String email, String tenantSlug) async {
    // Usamos ApiClient pero sin token, solo tenant en header
    apiClient.setTenant(tenantSlug);
    final request = ForgotPasswordRequest(email: email);
    final response = await apiClient.post(
      '/api/auth/forgot-password',
      request.toJson(),
    );
    if (response.statusCode != 200) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al enviar correo');
    }
    // Si es 200, asumimos éxito
    final data = response.data as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? 'Error desconocido');
    }
  }

  @override
  Future<void> changePassword(
    String currentPassword,
    String newPassword,
  ) async {
    final response = await apiClient.post('/api/auth/change-password', {
      'currentPassword': currentPassword,
      'newPassword': newPassword,
    });
    if (response.statusCode != 200) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al cambiar contraseña');
    }
    final data = response.data as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? 'Error desconocido');
    }
  }

  @override
  Future<void> logout() async {
    final response = await apiClient.post('/api/auth/logout', null);
    if (response.statusCode != 200 && response.statusCode != 204) {
      final data = response.data;
      if (data is Map<String, dynamic>) {
        throw Exception(data['message'] ?? 'Error al cerrar sesión');
      }
      throw Exception('Error al cerrar sesión');
    }
  }

  @override
  Future<LoginResponse> faceLogin(
    String email,
    String tenantSlug,
    String imagePath,
  ) async {
    apiClient.setTenant(tenantSlug);
    final formData = FormData.fromMap({
      'email': email,
      'image': await MultipartFile.fromFile(
        imagePath,
        filename: imagePath.split('/').isNotEmpty ? imagePath.split('/').last : 'face.jpg',
      ),
    });

    final response = await apiClient.post('/api/auth/face/login', formData);
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      return LoginResponse.fromJson(data);
    }
    if (response.statusCode == 401) {
      throw Exception('Credenciales faciales inválidas');
    }
    final data = response.data;
    if (data is Map<String, dynamic>) {
      throw Exception(data['message'] ?? 'Error al iniciar sesión con rostro');
    }
    throw Exception('Error al iniciar sesión con rostro');
  }

  @override
  Future<UserInfoModel> getProfile() async {
    final response = await apiClient.get('/api/auth/profile');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true && data['data'] is Map<String, dynamic>) {
        return UserInfoModel.fromMergedJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'Error al obtener perfil');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<UserInfoModel> updateProfile({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  }) async {
    final form = FormData.fromMap({});
    if (firstName != null && firstName.trim().isNotEmpty) {
      form.fields.add(MapEntry('firstName', firstName.trim()));
    }
    if (lastName != null && lastName.trim().isNotEmpty) {
      form.fields.add(MapEntry('lastName', lastName.trim()));
    }
    if (photoPath != null && photoPath.isNotEmpty) {
      form.files.add(
        MapEntry(
          'photo',
          await MultipartFile.fromFile(
            photoPath,
            filename: photoName ?? photoPath.split('/').last,
          ),
        ),
      );
    }

    final response = await apiClient.put('/api/auth/profile', form);
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true && data['data'] is Map<String, dynamic>) {
        return UserInfoModel.fromMergedJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'No se pudo actualizar el perfil');
    }
    final data = response.data;
    if (data is Map<String, dynamic>) {
      throw Exception(data['message'] ?? 'No se pudo actualizar el perfil');
    }
    throw Exception('No se pudo actualizar el perfil');
  }

  @override
  Future<UserInfoModel> removeProfilePhoto() async {
    final response = await apiClient.delete('/api/auth/profile/photo');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true && data['data'] is Map<String, dynamic>) {
        return UserInfoModel.fromMergedJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'No se pudo quitar la foto');
    }
    throw Exception('No se pudo quitar la foto');
  }
}
