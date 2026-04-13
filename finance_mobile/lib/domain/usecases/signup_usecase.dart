import '../entities/tenant_signup.dart';
import '../repositories/auth_repository.dart';

class SignupUseCase {
  final AuthRepository repository;

  SignupUseCase(this.repository);

  Future<void> call(TenantSignup signupData, String password) {
    return repository.signup(signupData, password);
  }
}
