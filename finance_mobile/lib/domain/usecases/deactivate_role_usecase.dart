import '../entities/role.dart';
import '../repositories/role_repository.dart';

class DeactivateRoleUseCase {
  final RoleRepository repository;

  DeactivateRoleUseCase(this.repository);

  Future<Role> call(String id) {
    return repository.deactivateRole(id);
  }
}
