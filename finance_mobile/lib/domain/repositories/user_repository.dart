import '../entities/user.dart';
import '../entities/role.dart';
import '../entities/user_info.dart';

abstract class UserRepository {
  Future<List<User>> getUsers();
  Future<List<Role>> getUserRoles(String userId);
  Future<List<Role>> getAvailableRoles();
  Future<void> assignRole(String userId, List<String> roleIds);
  Future<void> createUser(
    String email,
    String password,
    String firstName,
    String lastName,
  );
  Future<void> toggleUserStatus(String userId, bool currentlyActive);
  Future<UserInfo> getUserInfo();
}
