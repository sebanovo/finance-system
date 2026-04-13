import '../entities/user.dart';
import '../entities/tenant_signup.dart'; // opcional

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

  Future<void> signup(TenantSignup signupData, String password);
}
