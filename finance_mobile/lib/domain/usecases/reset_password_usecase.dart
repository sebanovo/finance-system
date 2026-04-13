import '../repositories/auth_repository.dart';

class ResetPasswordUseCase {
  final AuthRepository repository;
  ResetPasswordUseCase(this.repository);

  Future<void> call(String tenantSlug, String token, String newPassword) {
    return repository.resetPassword(tenantSlug, token, newPassword);
  }
}
