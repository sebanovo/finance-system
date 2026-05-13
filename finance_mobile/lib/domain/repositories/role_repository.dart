import 'package:finance_mobile/domain/entities/role.dart';
import 'package:finance_mobile/domain/usecases/create_role_usecase.dart';

abstract class RoleRepository {
  Future<List<Role>> getRoles();
  Future<Role> createRole(CreateRoleParams params);
}
