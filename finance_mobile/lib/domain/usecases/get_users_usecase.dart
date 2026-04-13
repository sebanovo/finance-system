import '../repositories/user_repository.dart';
import '../entities/user.dart';

class GetUsersUseCase {
  final UserRepository repository;
  GetUsersUseCase(this.repository);

  Future<List<User>> call() => repository.getUsers();
}
