import '../entities/permission.dart';

abstract class PermissionRepository {
  Future<List<Permission>> getPermissions();
}
