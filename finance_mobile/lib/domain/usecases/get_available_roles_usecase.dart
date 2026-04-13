import '../repositories/user_repository.dart';
import '../entities/role.dart';

class GetAvailableRolesUseCase {
  final UserRepository repository;
  GetAvailableRolesUseCase(this.repository);

  Future<List<Role>> call() => repository.getAvailableRoles();
}
