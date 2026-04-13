import 'package:finance_mobile/domain/entities/role.dart';

abstract class RoleRepository {
  Future<List<Role>> getRoles();
}
