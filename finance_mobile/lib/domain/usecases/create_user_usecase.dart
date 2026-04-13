import '../repositories/user_repository.dart';

class CreateUserUseCase {
  final UserRepository repository;
  CreateUserUseCase(this.repository);

  Future<void> call(
    String email,
    String password,
    String firstName,
    String lastName,
  ) => repository.createUser(email, password, firstName, lastName);
}
