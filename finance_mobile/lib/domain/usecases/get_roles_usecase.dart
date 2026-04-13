import 'package:finance_mobile/domain/entities/role.dart';

import '../repositories/role_repository.dart';

class GetRolesUsecase {
  final RoleRepository repository;
  GetRolesUsecase(this.repository);

  Future<List<Role>> call() async {
    return await repository.getRoles();
  }
}
