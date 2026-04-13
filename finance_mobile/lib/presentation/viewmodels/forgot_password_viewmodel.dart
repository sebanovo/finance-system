import 'package:finance_mobile/domain/usecases/forgot_password_usecase.dart';
import 'package:flutter/material.dart';

class ForgotPasswordViewModel extends ChangeNotifier {
  final ForgotPasswordUseCase forgotPasswordUseCase;

  bool _isLoading = false;
  String? _errorMessage;
  bool _success = false;

  ForgotPasswordViewModel({required this.forgotPasswordUseCase});

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  bool get success => _success;

  Future<bool> sendResetEmail(String email, String tenantSlug) async {
    _isLoading = true;
    _errorMessage = null;
    _success = false;
    notifyListeners();

    try {
      await forgotPasswordUseCase(email, tenantSlug);
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
