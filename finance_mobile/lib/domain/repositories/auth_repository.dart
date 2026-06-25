import '../entities/user.dart';
import '../entities/tenant_signup.dart';
import '../entities/user_info.dart';

abstract class AuthRepository {
  Future<(User, String, String)> login(
    String email,
    String password,
    String tenantSlug,
  );

  Future<(User, String, String)> faceLogin(
    String email,
    String tenantSlug,
    String imagePath,
  );

  Future<void> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  );

  Future<void> activateAccount(String tenantSlug, String token);

  Future<void> resendActivation(String email, String tenantSlug);

  Future<void> signup(TenantSignup signupData, String password);

  Future<void> forgotPassword(String email, String tenantSlug);
  Future<void> changePassword(String currentPassword, String newPassword);
  Future<void> logout();

  Future<UserInfo> getProfile();
  Future<UserInfo> updateProfile({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  });
  Future<UserInfo> removeProfilePhoto();
}
