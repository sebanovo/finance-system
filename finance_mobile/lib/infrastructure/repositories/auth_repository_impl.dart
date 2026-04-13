// lib/features/auth/data/repositories/auth_repository_impl.dart
import '../../../domain/entities/user.dart';
import '../../../domain/repositories/auth_repository.dart';
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
}
