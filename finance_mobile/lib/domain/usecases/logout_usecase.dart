import 'package:shared_preferences/shared_preferences.dart';

import '../repositories/auth_repository.dart';

class LogoutUseCase {
  final AuthRepository repository;

  LogoutUseCase(this.repository);

  Future<void> call() async {
    try {
      await repository.logout();
    } catch (_) {
      // Si el backend no responde, igual limpiamos la sesión local.
    } finally {
      final prefs = await SharedPreferences.getInstance();
      await prefs.clear();
    }
  }
}
