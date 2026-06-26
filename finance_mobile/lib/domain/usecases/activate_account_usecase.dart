import '../repositories/auth_repository.dart';

class ActivateAccountUseCase {
  final AuthRepository repository;
  ActivateAccountUseCase(this.repository);

  Future<void> call(String tenantSlug, String token) {
    return repository.activateAccount(tenantSlug, token);
  }
}
