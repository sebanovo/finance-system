import 'package:finance_mobile/presentation/viewmodels/notifications_viewmodel.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../core/network/api_client.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../domain/usecases/login_usecase.dart';

class LoginViewModel extends ChangeNotifier {
  final LoginUseCase loginUseCase;
  final ApiClient apiClient;

  LoginViewModel({required this.loginUseCase}) : apiClient = di.sl<ApiClient>();

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  bool _isLoading = false;
  String? _errorMessage;

  // ✅ Validaciones movidas al ViewModel o a un servicio
  String? validateTenant(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El nombre del tenant es obligatorio';
    }
    final trimmed = value.trim().toLowerCase();
    final regex = RegExp(r'^[a-z0-9]+(-[a-z0-9]+)*$');
    if (!regex.hasMatch(trimmed)) {
      return 'Solo letras minúsculas, números y guiones medios (-)';
    }
    return null;
  }

  String? validateEmail(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El correo electrónico es obligatorio';
    }
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,}$');
    if (!emailRegex.hasMatch(value.trim())) {
      return 'Ingresa un correo válido';
    }
    return null;
  }

  String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'La contraseña es obligatoria';
    }
    if (value.length < 8) {
      return 'Debe tener al menos 8 caracteres';
    }
    return null;
  }

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

      // ✅ Registrar dispositivo en el backend
      final notifViewModel = di.sl<NotificationsViewModel>();
      await notifViewModel.registerCurrentDevice();

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
