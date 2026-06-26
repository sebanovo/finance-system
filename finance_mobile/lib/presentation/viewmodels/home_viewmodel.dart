import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../domain/entities/customer_dashboard.dart';
import '../../../domain/entities/tenant_summary.dart';
import '../../../domain/usecases/get_subscription_usecase.dart';
import '../../../domain/usecases/get_customer_dashboard_usecase.dart';
import '../../../domain/usecases/get_tenant_summary_usecase.dart';
import '../../../domain/usecases/get_user_info_usecase.dart';
import '../../../domain/usecases/change_password_usecase.dart';
import '../../../domain/usecases/logout_usecase.dart';
import '../../../domain/entities/subscription.dart';
import '../../../../domain/entities/user_info.dart';

class HomeViewModel extends ChangeNotifier {
  final GetSubscriptionUseCase getSubscriptionUseCase;
  final GetCustomerDashboardUseCase getCustomerDashboardUseCase;
  final GetTenantSummaryUseCase getTenantSummaryUseCase;
  final GetUserInfoUseCase getUserInfoUseCase;
  final ChangePasswordUseCase changePasswordUseCase;
  final LogoutUseCase logoutUseCase;
  final ApiClient apiClient;

  CustomerDashboard? _customerDashboard;
  TenantSummary? _tenantSummary;
  Subscription? _subscription;
  UserInfo? _userInfo;
  bool _loadingDashboard = true;
  bool _loadingSubscription = true;
  bool _loadingUserInfo = true;
  String? _dashboardErrorMessage;
  String? _errorMessage;

  HomeViewModel({
    required this.getSubscriptionUseCase,
    required this.getCustomerDashboardUseCase,
    required this.getTenantSummaryUseCase,
    required this.getUserInfoUseCase,
    required this.changePasswordUseCase,
    required this.logoutUseCase,
    required this.apiClient,
  });

  // Getters
  CustomerDashboard? get customerDashboard => _customerDashboard;
  TenantSummary? get tenantSummary => _tenantSummary;
  Subscription? get subscription => _subscription;
  UserInfo? get userInfo => _userInfo;
  bool get loadingDashboard => _loadingDashboard;
  bool get loadingSubscription => _loadingSubscription;
  bool get loadingUserInfo => _loadingUserInfo;
  String? get dashboardErrorMessage => _dashboardErrorMessage;
  String? get errorMessage => _errorMessage;
  bool get isOwnerAdmin =>
      (userInfo?.roles.contains('OWNER_ADMIN') ?? false) || apiClient.isOwnerAdmin;
  List<String> get permissions => apiClient.permissions;

  bool hasPermission(String code) => apiClient.hasPermission(code);

  bool hasAnyPermissionPrefix(String prefix) =>
      apiClient.hasAnyPermissionPrefix(prefix);
  bool get isClient => hasAnyPermissionPrefix('me.');

  bool get shouldAutoRegisterDevice => isClient && !isOwnerAdmin;

  Future<void> loadData() async {
    await Future.wait([
      loadSubscription(),
      loadUserInfo(),
    ]);

    if (isOwnerAdmin) {
      await loadTenantSummary();
      _customerDashboard = null;
      notifyListeners();
      return;
    }

    if (isClient) {
      await loadCustomerDashboard();
      _tenantSummary = null;
      return;
    }

    _customerDashboard = null;
    _tenantSummary = null;
    _loadingDashboard = false;
    _dashboardErrorMessage = null;
    notifyListeners();
  }

  Future<void> loadCustomerDashboard() async {
    _loadingDashboard = true;
    notifyListeners();
    try {
      _customerDashboard = await getCustomerDashboardUseCase();
      _dashboardErrorMessage = null;
    } catch (e) {
      _dashboardErrorMessage = e.toString();
      _customerDashboard = null;
    } finally {
      _loadingDashboard = false;
      notifyListeners();
    }
  }

  Future<void> loadTenantSummary() async {
    _loadingDashboard = true;
    notifyListeners();
    try {
      _tenantSummary = await getTenantSummaryUseCase();
      _dashboardErrorMessage = null;
    } catch (e) {
      _dashboardErrorMessage = e.toString();
      _tenantSummary = null;
    } finally {
      _loadingDashboard = false;
      notifyListeners();
    }
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
