import '../repositories/user_repository.dart';
import '../entities/role.dart';

class GetUserRolesUseCase {
  final UserRepository repository;
  GetUserRolesUseCase(this.repository);

  Future<List<Role>> call(String userId) => repository.getUserRoles(userId);
}
