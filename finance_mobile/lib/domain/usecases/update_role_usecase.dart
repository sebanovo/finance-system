import '../entities/role.dart';
import '../repositories/role_repository.dart';

class UpdateRoleParams {
  final String id;
  final String name;
  final String description;
  final List<String> permissionCodes;

  UpdateRoleParams({
    required this.id,
    required this.name,
    required this.description,
    required this.permissionCodes,
  });
}

class UpdateRoleUseCase {
  final RoleRepository repository;

  UpdateRoleUseCase(this.repository);

  Future<Role> call(UpdateRoleParams params) {
    return repository.updateRole(params);
  }
}
