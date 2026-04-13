import '../../../domain/entities/permission.dart';
import '../../../domain/repositories/permission_repository.dart';
import '../datasources/permission_remote_datasource.dart';

class PermissionRepositoryImpl implements PermissionRepository {
  final PermissionRemoteDataSource remoteDataSource;

  PermissionRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<Permission>> getPermissions() async {
    final models = await remoteDataSource.getPermissions();
    return models.map((model) => model.toEntity()).toList();
  }
}
