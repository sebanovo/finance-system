import 'package:flutter/material.dart';
import '../../../../domain/entities/tenant_signup.dart';
import '../../../../domain/usecases/signup_usecase.dart';

class SignupViewModel extends ChangeNotifier {
  final SignupUseCase signupUseCase;

  bool _isLoading = false;
  String? _errorMessage;
  bool _success = false;

  SignupViewModel({required this.signupUseCase});

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  bool get success => _success;

  // ✅ Validaciones movidas al ViewModel
  String? validateCompanyName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El nombre de la empresa es obligatorio';
    }
    if (value.trim().length < 3) {
      return 'Debe tener al menos 3 caracteres';
    }
    return null;
  }

  String? validateTenantSlug(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El slug del tenant es obligatorio';
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

  String? validateFirstName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El nombre es obligatorio';
    }
    return null;
  }

  String? validateLastName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El apellido es obligatorio';
    }
    return null;
  }

  Future<bool> signup({
    required String companyName,
    required String tenantSlug,
    required String adminEmail,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    _isLoading = true;
    _errorMessage = null;
    _success = false;
    notifyListeners();

    final signupData = TenantSignup(
      companyName: companyName,
      tenantSlug: tenantSlug,
      adminEmail: adminEmail,
      firstName: firstName,
      lastName: lastName,
    );

    try {
      await signupUseCase(signupData, password);
      _success = true;
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void clearSuccess() {
    _success = false;
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
