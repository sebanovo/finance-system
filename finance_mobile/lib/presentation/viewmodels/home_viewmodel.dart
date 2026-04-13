import 'package:flutter/material.dart';
import '../../../domain/usecases/get_subscription_usecase.dart';
import '../../../domain/usecases/get_user_info_usecase.dart';
import '../../../domain/usecases/change_password_usecase.dart';
import '../../../domain/usecases/logout_usecase.dart';
import '../../../domain/entities/subscription.dart';
import '../../../../domain/entities/user_info.dart';

class HomeViewModel extends ChangeNotifier {
  final GetSubscriptionUseCase getSubscriptionUseCase;
  final GetUserInfoUseCase getUserInfoUseCase;
  final ChangePasswordUseCase changePasswordUseCase;
  final LogoutUseCase logoutUseCase;

  Subscription? _subscription;
  UserInfo? _userInfo;
  bool _loadingSubscription = true;
  bool _loadingUserInfo = true;
  String? _errorMessage;

  HomeViewModel({
    required this.getSubscriptionUseCase,
    required this.getUserInfoUseCase,
    required this.changePasswordUseCase,
    required this.logoutUseCase,
  }) {
    loadData();
  }

  // Getters
  Subscription? get subscription => _subscription;
  UserInfo? get userInfo => _userInfo;
  bool get loadingSubscription => _loadingSubscription;
  bool get loadingUserInfo => _loadingUserInfo;
  String? get errorMessage => _errorMessage;

  Future<void> loadData() async {
    await Future.wait([loadSubscription(), loadUserInfo()]);
  }

  Future<void> loadSubscription() async {
    _loadingSubscription = true;
    notifyListeners();
    try {
      _subscription = await getSubscriptionUseCase();
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingSubscription = false;
      notifyListeners();
    }
  }

  Future<void> loadUserInfo() async {
    _loadingUserInfo = true;
    notifyListeners();
    try {
      _userInfo = await getUserInfoUseCase();
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingUserInfo = false;
      notifyListeners();
    }
  }

  Future<void> changePassword(
    String currentPassword,
    String newPassword,
  ) async {
    try {
      await changePasswordUseCase(currentPassword, newPassword);
    } catch (e) {
      rethrow;
    }
  }

  Future<void> logout() async {
    await logoutUseCase();
    // No navegamos aquí, lo hará la página después de escuchar el cambio o directamente
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
