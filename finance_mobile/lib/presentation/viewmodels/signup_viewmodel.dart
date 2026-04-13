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

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
