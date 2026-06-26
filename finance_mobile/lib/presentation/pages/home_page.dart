import 'package:finance_mobile/core/observers/notification_counter.dart';
import 'package:finance_mobile/core/services/notification_service.dart';
import 'package:finance_mobile/presentation/viewmodels/notifications_viewmodel.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/home_viewmodel.dart';
import '../widgets/customer_dashboard_view.dart';
import '../widgets/change_password_dialog.dart';
import '../widgets/main_drawer.dart';
import '../widgets/subscription_card.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late HomeViewModel _viewModel;
  late NotificationsViewModel _notifViewModel;
  late final VoidCallback _notificationCounterListener;
  bool _deviceRegistrationRequested = false;

  @override
  void initState() {
    super.initState();
    _notificationCounterListener = () {
      if (mounted) {
        setState(() {});
      }
    };
    NotificationCounter().addListener(_notificationCounterListener);
    _viewModel = di.sl<HomeViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.addListener(_onUserInfoLoaded);

    _notifViewModel = di.sl<NotificationsViewModel>();
    _notifViewModel.addListener(_onNotifChanged);
    _setupFcmListeners();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      _viewModel.loadData();
      _notifViewModel.loadUnreadCount();
    });
  }

  // En HomePage, modifica el método _onNotifChanged
  void _onNotifChanged() {
    if (mounted) {
      // ✅ Forzar actualización del badge
      setState(() {});
    }
  }

  // Asegurar que el contador se recargue al volver a la pantalla
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
  }

  void _onUserInfoLoaded() {
    if (_viewModel.userInfo != null) {
      _notifViewModel.setIsClient(_viewModel.isClient);

      if (_viewModel.isClient && !_deviceRegistrationRequested) {
        _deviceRegistrationRequested = true;
        Future.microtask(() async {
          final success = await _notifViewModel.registerCurrentDevice();
          if (!mounted) return;
          if (success) {
            _showSnackBar(
              'Dispositivo de notificaciones registrado correctamente.',
              isError: false,
            );
          } else {
            _showSnackBar(
              'No se pudo registrar el dispositivo de notificaciones.',
            );
          }
        });
      }
    }
  }

  void _setupFcmListeners() {
    NotificationService.onMessage((message) {
      debugPrint('🔔 Notificación recibida: ${message.notification?.title}');
      _notifViewModel.loadUnreadCount();
    });

    NotificationService.onMessageOpenedApp((message) {
      debugPrint('🔔 Notificación abierta: ${message.notification?.title}');
      final actionUrl = message.data['actionUrl'];
      if (actionUrl != null && actionUrl.isNotEmpty && mounted) {
        context.push(actionUrl);
      }
    });
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    setState(() {});
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  Future<void> _logout() async {
    await _viewModel.logout();
  }

  void _showChangePasswordDialog() {
    showDialog(
      context: context,
      builder: (context) => ChangePasswordDialog(
        onChangePassword: (current, newPwd) async {
          await _viewModel.changePassword(current, newPwd);
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Dashboard"),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [_buildNotificationBadge()],
      ),
      drawer: MainDrawer(
        viewModel: _viewModel,
        notifViewModel: _notifViewModel,
        onChangePassword: _showChangePasswordDialog,
        onLogout: _logout,
      ),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadData(),
        color: const Color(0xFF2E7D32),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (_viewModel.isClient) ...[
                Text(
                  "Bienvenido al Dashboard",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                CustomerDashboardView(
                  dashboard: _viewModel.customerDashboard,
                  isLoading: _viewModel.loadingDashboard,
                  errorMessage: _viewModel.dashboardErrorMessage,
                  onOpenAccounts: () => context.push('/accounts'),
                  onOpenTransactions: () => context.push('/transactions'),
                  onOpenLoans: _viewModel.hasAnyPermissionPrefix('me.loans.')
                      ? () => context.push('/loans')
                      : null,
                  onOpenLimits: _viewModel.hasAnyPermissionPrefix('limits.')
                      ? () => context.push('/limits')
                      : null,
                  onOpenNotifications: () => context.push('/notifications'),
                  onOpenServicePayments: () =>
                      context.push('/service-payments'),
                ),
                const SizedBox(height: 16),
                SubscriptionCard(
                  subscription: _viewModel.subscription,
                  errorMessage: _viewModel.errorMessage,
                  isLoading: _viewModel.loadingSubscription,
                ),
              ] else ...[
                Text(
                  "Bienvenido al Dashboard",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                SubscriptionCard(
                  subscription: _viewModel.subscription,
                  errorMessage: _viewModel.errorMessage,
                  isLoading: _viewModel.loadingSubscription,
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildNotificationBadge() {
    final unreadCount = NotificationCounter().count;

    return Stack(
      children: [
        IconButton(
          icon: const Icon(Icons.notifications),
          onPressed: () => context.push('/notifications'),
        ),
        if (unreadCount > 0)
          Positioned(
            right: 4,
            top: 4,
            child: Container(
              padding: const EdgeInsets.all(2),
              decoration: const BoxDecoration(
                color: Colors.red,
                shape: BoxShape.circle,
              ),
              constraints: const BoxConstraints(minWidth: 12, minHeight: 12),
              child: Text(
                '$unreadCount',
                style: const TextStyle(color: Colors.white, fontSize: 8),
                textAlign: TextAlign.center,
              ),
            ),
          ),
      ],
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    _viewModel.removeListener(_onUserInfoLoaded);
    _notifViewModel.removeListener(_onNotifChanged);
    NotificationCounter().removeListener(_notificationCounterListener);
    super.dispose();
  }
}
