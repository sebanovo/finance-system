import '../entities/user.dart';
import '../entities/tenant_signup.dart';

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

  Future<void> forgotPassword(String email, String tenantSlug);
  Future<void> changePassword(String currentPassword, String newPassword);
}
