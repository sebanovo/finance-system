import '../../../../domain/repositories/user_repository.dart';
import '../../../../domain/entities/user_info.dart';

class GetUserInfoUseCase {
  final UserRepository repository;
  GetUserInfoUseCase(this.repository);

  Future<UserInfo> call() => repository.getUserInfo();
}
