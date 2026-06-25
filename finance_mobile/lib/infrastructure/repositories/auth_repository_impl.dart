import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/domain/entities/tenant_signup.dart';
import 'package:finance_mobile/domain/entities/user.dart';
import 'package:finance_mobile/domain/entities/user_info.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';
import 'package:finance_mobile/infrastructure/datasources/auth_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/models/signup_request.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;
  final ApiClient apiClient;

  AuthRepositoryImpl(this.remoteDataSource, this.apiClient);

  @override
  Future<(User, String, String)> login(
    String email,
    String password,
    String tenantSlug,
  ) async {
    final response = await remoteDataSource.login(email, password, tenantSlug);
    if (response.success && response.data != null) {
      final user = User(
        id: '', // borrar esto luego
        email: email,
        firstName: '',
        lastName: '',
        active: true,
      );
      return (user, response.data!.accessToken, response.data!.refreshToken);
    } else {
      throw Exception(response.message);
    }
  }

  @override
  Future<(User, String, String)> faceLogin(
    String email,
    String tenantSlug,
    String imagePath,
  ) async {
    final response = await remoteDataSource.faceLogin(email, tenantSlug, imagePath);
    if (response.success && response.data != null) {
      final user = User(
        id: '',
        email: email,
        firstName: '',
        lastName: '',
        active: true,
      );
      return (user, response.data!.accessToken, response.data!.refreshToken);
    } else {
      throw Exception(response.message);
    }
  }

  @override
  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  ) async {
    await remoteDataSource.resetPassword(tenantSlug, token, newPassword);
  }

  @override
  Future<void> activateAccount(String tenantSlug, String token) async {
    await remoteDataSource.activateAccount(tenantSlug, token);
  }

  @override
  Future<void> resendActivation(String email, String tenantSlug) async {
    await remoteDataSource.resendActivation(email, tenantSlug);
  }

  @override
  Future<void> signup(TenantSignup signupData, String password) async {
    final request = SignupRequest(
      companyName: signupData.companyName,
      tenantSlug: signupData.tenantSlug,
      adminEmail: signupData.adminEmail,
      password: password,
      firstName: signupData.firstName,
      lastName: signupData.lastName,
    );
    await remoteDataSource.signup(request);
  }

  @override
  Future<void> forgotPassword(String email, String tenantSlug) async {
    await remoteDataSource.forgotPassword(email, tenantSlug);
  }

  @override
  Future<void> changePassword(
    String currentPassword,
    String newPassword,
  ) async {
    await remoteDataSource.changePassword(currentPassword, newPassword);
  }

  @override
  Future<void> logout() async {
    try {
      await remoteDataSource.logout();
    } finally {
      apiClient.clearSession();
    }
  }

  @override
  Future<UserInfo> getProfile() async {
    return (await remoteDataSource.getProfile()).toEntity();
  }

  @override
  Future<UserInfo> updateProfile({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  }) async {
    return (await remoteDataSource.updateProfile(
      firstName: firstName,
      lastName: lastName,
      photoPath: photoPath,
      photoName: photoName,
    ))
        .toEntity();
  }

  @override
  Future<UserInfo> removeProfilePhoto() async {
    return (await remoteDataSource.removeProfilePhoto()).toEntity();
  }
}
