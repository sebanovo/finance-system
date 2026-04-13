import 'package:shared_preferences/shared_preferences.dart';

class LogoutUseCase {
  Future<void> call() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
  }
}
