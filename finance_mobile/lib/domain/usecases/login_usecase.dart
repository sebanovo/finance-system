import '../repositories/auth_repository.dart';
import '../entities/user.dart';

class LoginUseCase {
  final AuthRepository repository;

  LoginUseCase(this.repository);

  Future<(User, String, String)> call(
    String email,
    String password,
    String tenantSlug,
  ) {
    return repository.login(email, password, tenantSlug);
  }
}
