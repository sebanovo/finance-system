// lib/features/home/domain/usecases/change_password_usecase.dart
import '../../../../domain/repositories/auth_repository.dart';

class ChangePasswordUseCase {
  final AuthRepository repository;
  ChangePasswordUseCase(this.repository);

  Future<void> call(String currentPassword, String newPassword) =>
      repository.changePassword(currentPassword, newPassword);
}
