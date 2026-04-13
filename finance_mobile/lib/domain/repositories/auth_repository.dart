import '../entities/user.dart';

abstract class AuthRepository {
  Future<(User, String, String)> login(
    String email,
    String password,
    String tenantSlug,
  );
}
