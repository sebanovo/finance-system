import 'package:finance_mobile/infrastructure/models/signup_request.dart';

import '../../../domain/entities/user.dart';
import '../../../domain/repositories/auth_repository.dart';
import '../../../domain/entities/tenant_signup.dart';
import '../datasources/auth_remote_datasource.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;

  AuthRepositoryImpl(this.remoteDataSource);

  @override
  Future<(User, String, String)> login(
    String email,
    String password,
    String tenantSlug,
  ) async {
    final response = await remoteDataSource.login(email, password, tenantSlug);
    if (response.success && response.data != null) {
      final user = User(
        email: email,
        tenantSlug: tenantSlug,
        roles: [], // opcional: extraer roles del token si es necesario
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
}
