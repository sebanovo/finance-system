import 'package:finance_mobile/domain/entities/role.dart';
import 'package:finance_mobile/domain/usecases/create_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/update_role_usecase.dart';

abstract class RoleRepository {
  Future<List<Role>> getRoles();
  Future<Role> createRole(CreateRoleParams params);
  Future<Role> activateRole(String id);
  Future<Role> deactivateRole(String id);
  Future<Role> getRole(String id);
  Future<Role> updateRole(UpdateRoleParams params);
}
