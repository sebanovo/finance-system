import '../../../domain/entities/role.dart';
import '../../../domain/repositories/role_repository.dart';
import '../datasources/role_remote_datasource.dart';

class RoleRepositoryImpl implements RoleRepository {
  final RoleRemoteDataSource remoteDataSource;

  RoleRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<Role>> getRoles() async {
    final models = await remoteDataSource.getRoles();
    return models.map((model) => model.toEntity()).toList();
  }
}
