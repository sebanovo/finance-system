import 'package:finance_mobile/domain/entities/role.dart';
import '../repositories/role_repository.dart';

class CreateRoleParams {
  final String name;
  final String description;
  final List<String> permissionCodes;

  CreateRoleParams({
    required this.name,
    required this.description,
    required this.permissionCodes,
  });
}

class CreateRoleUseCase {
  final RoleRepository repository;
  CreateRoleUseCase(this.repository);

  Future<Role> call(CreateRoleParams params) async {
    return await repository.createRole(params);
  }
}
