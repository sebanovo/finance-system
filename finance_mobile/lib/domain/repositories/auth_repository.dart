import '../entities/user.dart';

abstract class AuthRepository {
  Future<(User, String, String)> login(
    String email,
    String password,
    String tenantSlug,
  );

  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  );
}
