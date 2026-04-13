import 'package:flutter/material.dart';
import '../../../domain/usecases/reset_password_usecase.dart';

class ResetPasswordViewModel extends ChangeNotifier {
  final ResetPasswordUseCase resetPasswordUseCase;

  bool _isLoading = false;
  String? _errorMessage;

  ResetPasswordViewModel({required this.resetPasswordUseCase});

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  Future<bool> resetPassword(
    String tenantSlug,
    String token,
    String newPassword,
  ) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      await resetPasswordUseCase(tenantSlug, token, newPassword);
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
