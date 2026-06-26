import 'package:flutter/material.dart';
import '../../../domain/usecases/activate_account_usecase.dart';

class ActivateAccountViewModel extends ChangeNotifier {
  final ActivateAccountUseCase activateAccountUseCase;

  bool _isLoading = false;
  String? _errorMessage;

  ActivateAccountViewModel({required this.activateAccountUseCase});

  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  Future<bool> activateAccount(String tenantSlug, String token) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      await activateAccountUseCase(tenantSlug, token);
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
