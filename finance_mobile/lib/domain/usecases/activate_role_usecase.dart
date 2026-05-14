import '../entities/role.dart';
import '../repositories/role_repository.dart';

class ActivateRoleUseCase {
  final RoleRepository repository;

  ActivateRoleUseCase(this.repository);

  Future<Role> call(String id) {
    return repository.activateRole(id);
  }
}
