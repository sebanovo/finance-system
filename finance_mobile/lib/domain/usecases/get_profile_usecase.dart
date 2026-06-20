import 'package:finance_mobile/domain/entities/user_info.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';

class GetProfileUseCase {
  final AuthRepository repository;

  GetProfileUseCase(this.repository);

  Future<UserInfo> call() => repository.getProfile();
}
