import '../repositories/auth_repository.dart';

class ForgotPasswordUseCase {
  final AuthRepository repository;

  ForgotPasswordUseCase(this.repository);

  Future<void> call(String email, String tenantSlug) {
    return repository.forgotPassword(email, tenantSlug);
  }
}
