import '../../../domain/entities/role.dart';
import '../../../domain/repositories/role_repository.dart';
import '../../../domain/usecases/create_role_usecase.dart';
import '../../../domain/usecases/update_role_usecase.dart';
import '../datasources/role_remote_datasource.dart';

class RoleRepositoryImpl implements RoleRepository {
  final RoleRemoteDataSource remoteDataSource;

  RoleRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<Role>> getRoles() async {
    final models = await remoteDataSource.getRoles();
    return models.map((model) => model.toEntity()).toList();
  }

  @override
  Future<Role> createRole(CreateRoleParams params) async {
    final model = await remoteDataSource.createRole(params);
    return model.toEntity();
  }

  @override
  Future<Role> activateRole(String id) async {
    final model = await remoteDataSource.activateRole(id);
    return model.toEntity();
  }

  @override
  Future<Role> deactivateRole(String id) async {
    final model = await remoteDataSource.deactivateRole(id);
    return model.toEntity();
  }

  @override
  Future<Role> getRole(String id) async {
    final model = await remoteDataSource.getRole(id);
    return model.toEntity();
  }

  @override
  Future<Role> updateRole(UpdateRoleParams params) async {
    final model = await remoteDataSource.updateRole(params);
    return model.toEntity();
  }
}
