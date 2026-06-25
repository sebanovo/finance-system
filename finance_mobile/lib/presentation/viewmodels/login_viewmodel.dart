import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:finance_mobile/domain/usecases/face_login_usecase.dart';
import 'package:finance_mobile/domain/usecases/login_usecase.dart';

class LoginViewModel extends ChangeNotifier {
  final LoginUseCase loginUseCase;
  final FaceLoginUseCase faceLoginUseCase;
  final ApiClient apiClient;

  LoginViewModel({required this.loginUseCase, required this.faceLoginUseCase})
    : apiClient = di.sl<ApiClient>();

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
      final (_, accessToken, refreshToken) = await loginUseCase(
        email,
        password,
        tenantSlug,
      );

      await _persistSession(
        accessToken: accessToken,
        refreshToken: refreshToken,
        tenantSlug: tenantSlug,
      );

      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> faceLogin(
    String email,
    String tenantSlug,
    String imagePath,
  ) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final (_, accessToken, refreshToken) = await faceLoginUseCase(
        email,
        tenantSlug,
        imagePath,
      );

      await _persistSession(
        accessToken: accessToken,
        refreshToken: refreshToken,
        tenantSlug: tenantSlug,
      );

      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> _persistSession({
    required String accessToken,
    required String refreshToken,
    required String tenantSlug,
  }) async {
    apiClient.setSession(
      token: accessToken,
      tenantSlug: tenantSlug,
      refreshToken: refreshToken,
    );

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('accessToken', accessToken);
    await prefs.setString('refreshToken', refreshToken);
    await prefs.setString('tenantSlug', tenantSlug);
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
