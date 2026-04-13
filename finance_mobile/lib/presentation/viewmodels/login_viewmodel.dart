import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../core/network/api_client.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../domain/usecases/login_usecase.dart';

class LoginViewModel extends ChangeNotifier {
  final LoginUseCase loginUseCase;
  final ApiClient apiClient;

  bool _isLoading = false;
  String? _errorMessage;

  LoginViewModel({required this.loginUseCase}) : apiClient = di.sl<ApiClient>();

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  Future<bool> login(String email, String password, String tenantSlug) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final (user, accessToken, refreshToken) = await loginUseCase(
        email,
        password,
        tenantSlug,
      );

      // Guardar en ApiClient (para peticiones futuras)
      apiClient.setToken(accessToken);
      apiClient.setTenant(tenantSlug);

      // Persistir en SharedPreferences para futuras sesiones
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('accessToken', accessToken);
      await prefs.setString('refreshToken', refreshToken);
      await prefs.setString('tenantSlug', tenantSlug);

      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
