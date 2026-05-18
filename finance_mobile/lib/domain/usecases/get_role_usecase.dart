import '../entities/role.dart';
import '../repositories/role_repository.dart';

class GetRoleUseCase {
  final RoleRepository repository;

  GetRoleUseCase(this.repository);

  Future<Role> call(String id) {
    return repository.getRole(id);
  }
}
