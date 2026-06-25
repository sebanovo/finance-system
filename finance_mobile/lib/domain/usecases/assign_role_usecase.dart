import '../repositories/user_repository.dart';

class AssignRoleUseCase {
  final UserRepository repository;
  AssignRoleUseCase(this.repository);

  Future<void> call(String userId, List<String> roleIds) =>
      repository.assignRole(userId, roleIds);
}
