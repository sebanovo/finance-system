import '../repositories/user_repository.dart';

class ToggleUserStatusUseCase {
  final UserRepository repository;

  ToggleUserStatusUseCase(this.repository);

  Future<void> call(String userId, bool currentlyActive) {
    return repository.toggleUserStatus(userId, currentlyActive);
  }
}
