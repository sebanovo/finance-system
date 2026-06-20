import 'package:finance_mobile/domain/entities/user_info.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';

class UpdateProfileUseCase {
  final AuthRepository repository;

  UpdateProfileUseCase(this.repository);

  Future<UserInfo> call({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  }) {
    return repository.updateProfile(
      firstName: firstName,
      lastName: lastName,
      photoPath: photoPath,
      photoName: photoName,
    );
  }
}
