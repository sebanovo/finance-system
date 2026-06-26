import 'package:finance_mobile/infrastructure/models/create_user_request.dart';

import '../../../domain/entities/user.dart';
import '../../../domain/entities/role.dart';
import '../../../domain/repositories/user_repository.dart';
import '../../../domain/entities/user_info.dart';
import '../datasources/user_remote_datasource.dart';

class UserRepositoryImpl implements UserRepository {
  final UserRemoteDataSource remoteDataSource;

  UserRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<User>> getUsers() async {
    final models = await remoteDataSource.getUsers();
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<List<Role>> getUserRoles(String userId) async {
    final models = await remoteDataSource.getUserRoles(userId);
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<List<Role>> getAvailableRoles() async {
    final models = await remoteDataSource.getAvailableRoles();
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<void> assignRole(String userId, List<String> roleIds) async {
    await remoteDataSource.assignRole(userId, roleIds);
  }

  @override
  Future<void> createUser(
    String email,
    String password,
    String firstName,
    String lastName,
  ) async {
    final request = CreateUserRequest(
      email: email,
      password: password,
      firstName: firstName,
      lastName: lastName,
    );
    await remoteDataSource.createUser(request);
  }

  @override
  Future<void> toggleUserStatus(String userId, bool currentlyActive) async {
    await remoteDataSource.toggleUserStatus(userId, currentlyActive);
  }

  @override
  Future<UserInfo> getUserInfo() async {
    final model = await remoteDataSource.getUserInfo();
    return model.toEntity();
  }
}
