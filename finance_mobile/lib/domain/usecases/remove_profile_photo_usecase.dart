import 'package:finance_mobile/domain/entities/user_info.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';

class RemoveProfilePhotoUseCase {
  final AuthRepository repository;

  RemoveProfilePhotoUseCase(this.repository);

  Future<UserInfo> call() => repository.removeProfilePhoto();
}
