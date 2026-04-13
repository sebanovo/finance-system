import 'package:finance_mobile/domain/entities/permission.dart';

import '../repositories/permission_repository.dart';

class GetPermissionsUsecase {
  final PermissionRepository repository;
  GetPermissionsUsecase(this.repository);

  Future<List<Permission>> call() async {
    return await repository.getPermissions();
  }
}
