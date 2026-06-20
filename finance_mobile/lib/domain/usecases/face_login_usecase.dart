import 'package:finance_mobile/domain/entities/user.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';

class FaceLoginUseCase {
  final AuthRepository repository;

  FaceLoginUseCase(this.repository);

  Future<(User, String, String)> call(
    String email,
    String tenantSlug,
    String imagePath,
  ) {
    return repository.faceLogin(email, tenantSlug, imagePath);
  }
}
