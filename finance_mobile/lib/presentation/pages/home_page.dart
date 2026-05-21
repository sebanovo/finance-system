import 'package:finance_mobile/core/services/notification_service.dart';
import 'package:finance_mobile/presentation/viewmodels/notifications_viewmodel.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/home_viewmodel.dart';
import '../../presentation/widgets/change_password_dialog.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late HomeViewModel _viewModel;
  late NotificationsViewModel _notifViewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<HomeViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _notifViewModel = di.sl<NotificationsViewModel>();
    _notifViewModel.addListener(_onNotifChanged);
    _viewModel.addListener(_onUserInfoLoaded);
    _notifViewModel.loadUnreadCount();
    _setupFcmListeners();
  }

  void _onUserInfoLoaded() {
    if (_viewModel.userInfo != null) {
      // ✅ Obtener el primer rol (o el más relevante)
      final userRole = _viewModel.userInfo!.roles.firstWhere(
        (r) => r == 'USER' || r == 'ADMIN' || r == 'OWNER_ADMIN',
        orElse: () => 'UNKNOWN',
      );
      _notifViewModel.setCurrentUserRole(userRole);

      // ✅ Solo registrar dispositivo si es USER
      if (userRole == 'USER') {
        _notifViewModel.registerCurrentDevice();
      }
    }
  }

  void _setupFcmListeners() {
    // Escuchar notificaciones en primer plano
    NotificationService.onMessage((message) {
      debugPrint('🔔 Notificación recibida: ${message.notification?.title}');

      // Actualizar contador de notificaciones no leídas
      _notifViewModel.loadUnreadCount();
    });

    // Escuchar cuando el usuario abre la notificación
    NotificationService.onMessageOpenedApp((message) {
      debugPrint('🔔 Notificación abierta: ${message.notification?.title}');
      final actionUrl = message.data['actionUrl'];
      if (actionUrl != null && actionUrl.isNotEmpty && mounted) {
        context.push(actionUrl);
      }
    });
  }

  void _onNotifChanged() {
    if (mounted) setState(() {});
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
    if (mounted) context.go('/login');
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

  // ✅ Método para verificar si el usuario tiene un rol específico
  // bool _hasRole(String role) {
  //   final roles = _viewModel.userInfo?.roles ?? [];
  //   return roles.contains(role);
  // }

  // ✅ Método para verificar si el usuario tiene al menos uno de los roles
  bool _hasAnyRole(List<String> roles) {
    final userRoles = _viewModel.userInfo?.roles ?? [];
    return roles.any((role) => userRoles.contains(role));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Dashboard"),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [
          Stack(
            children: [
              IconButton(
                icon: const Icon(Icons.notifications),
                onPressed: () => context.push('/notifications'),
              ),
              if (_notifViewModel.unreadCount > 0)
                Positioned(
                  right: 4,
                  top: 4,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: const BoxDecoration(
                      color: Colors.red,
                      shape: BoxShape.circle,
                    ),
                    constraints: const BoxConstraints(
                      minWidth: 12,
                      minHeight: 12,
                    ),
                    child: Text(
                      '${_notifViewModel.unreadCount}',
                      style: const TextStyle(color: Colors.white, fontSize: 8),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
            ],
          ),
        ],
      ),
      drawer: _buildDrawer(),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadData(),
        color: const Color(0xFF2E7D32),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                "Bienvenido al Dashboard",
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              _buildSubscriptionCard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDrawer() {
    return Drawer(
      child: Container(
        color: Colors.white,
        child: ListView(
          children: [
            if (_viewModel.loadingUserInfo)
              const DrawerHeader(
                decoration: BoxDecoration(color: Color(0xFF2E7D32)),
                child: Center(
                  child: CircularProgressIndicator(color: Colors.white),
                ),
              )
            else if (_viewModel.userInfo != null)
              UserAccountsDrawerHeader(
                decoration: const BoxDecoration(color: Color(0xFF2E7D32)),
                accountName: Text(
                  _viewModel.userInfo!.displayName,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                accountEmail: Text(_viewModel.userInfo!.email),
                currentAccountPicture: CircleAvatar(
                  backgroundColor: Colors.white,
                  child: Text(
                    _viewModel.userInfo!.initial,
                    style: const TextStyle(
                      fontSize: 24,
                      color: Color(0xFF2E7D32),
                    ),
                  ),
                ),
                otherAccountsPictures: [
                  Tooltip(
                    message: 'Roles: ${_viewModel.userInfo!.roles.join(', ')}',
                    child: const Icon(Icons.verified_user, color: Colors.white),
                  ),
                ],
              ),
            // ✅ Dashboard - visible para todos
            _buildDrawerItem(
              icon: Icons.dashboard,
              title: 'Dashboard',
              onTap: () => Navigator.pop(context),
            ),
            // ✅ Usuarios - solo para OWNER_ADMIN y ADMIN
            if (_hasAnyRole(['OWNER_ADMIN', 'ADMIN']))
              _buildDrawerItem(
                icon: Icons.people,
                title: 'Usuarios',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/users');
                },
              ),
            // ✅ Roles - solo para OWNER_ADMIN y ADMIN
            if (_hasAnyRole(['OWNER_ADMIN', 'ADMIN']))
              _buildDrawerItem(
                icon: Icons.security,
                title: 'Roles',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/roles');
                },
              ),
            // ✅ Permisos - solo para OWNER_ADMIN y ADMIN
            if (_hasAnyRole(['OWNER_ADMIN', 'ADMIN']))
              _buildDrawerItem(
                icon: Icons.vpn_key,
                title: 'Permisos',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/permissions');
                },
              ),
            if (_hasAnyRole(['USER']))
              _buildDrawerItem(
                icon: Icons.account_balance_wallet,
                title: 'Mis Cuentas',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/accounts');
                },
              ),

            if (_hasAnyRole(['USER']))
              _buildDrawerItem(
                icon: Icons.history,
                title: 'Movimientos',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/transactions');
                },
              ),
            // ✅ Cambiar contraseña - visible para todos
            _buildDrawerItem(
              icon: Icons.lock_reset,
              title: 'Cambiar contraseña',
              onTap: () {
                Navigator.pop(context);
                _showChangePasswordDialog();
              },
            ),
            if (_hasAnyRole(['USER']))
              _buildDrawerItem(
                icon: Icons.notifications,
                title: 'Mis notificaciones',
                onTap: () {
                  Navigator.pop(context);
                  context.push('/notifications');
                },
              ),
            _buildDrawerItem(
              icon: Icons.phone_android,
              title: 'Mis Dispositivos',
              onTap: () {
                Navigator.pop(context);
                context.push('/devices');
              },
            ),
            // ✅ Cerrar sesión - visible para todos
            _buildDrawerItem(
              icon: Icons.logout,
              title: 'Cerrar sesión',
              onTap: () => _logout(),
              isDestructive: true,
            ),
            if (_hasAnyRole(['USER']))
              ElevatedButton(
                onPressed: () async {
                  final notifViewModel = di.sl<NotificationsViewModel>();
                  await notifViewModel.registerCurrentDevice();
                },
                child: const Text('Registrar dispositivo'),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildDrawerItem({
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    bool isDestructive = false,
  }) {
    return ListTile(
      leading: Icon(
        icon,
        color: isDestructive ? Colors.red.shade700 : const Color(0xFF4CAF50),
      ),
      title: Text(
        title,
        style: TextStyle(
          color: isDestructive ? Colors.red.shade700 : Colors.black87,
        ),
      ),
      onTap: onTap,
      hoverColor: Colors.grey.shade50,
    );
  }

  Widget _buildSubscriptionCard() {
    if (_viewModel.loadingSubscription) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_viewModel.errorMessage != null) {
      return Card(
        color: Colors.red[50],
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Text(
            _viewModel.errorMessage!,
            style: const TextStyle(color: Colors.red),
          ),
        ),
      );
    }
    if (_viewModel.subscription == null) {
      return const Center(child: Text('No hay información de suscripción'));
    }
    final sub = _viewModel.subscription!;
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  sub.planName,
                  style: const TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF2E7D32),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: sub.status == 'ACTIVE'
                        ? Colors.green[100]
                        : Colors.orange[100],
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    sub.status,
                    style: TextStyle(
                      color: sub.status == 'ACTIVE'
                          ? Colors.green[700]
                          : Colors.orange[700],
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            if (sub.trial)
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: const Color(0xFFE8F5E9),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.science, color: Color(0xFF2E7D32)),
                    const SizedBox(width: 8),
                    Text(
                      'Período de prueba · ${sub.remainingDays} días restantes',
                      style: const TextStyle(fontWeight: FontWeight.w500),
                    ),
                  ],
                ),
              ),
            const SizedBox(height: 16),
            _infoRow(Icons.category, 'Tipo de plan', sub.planType),
            _infoRow(Icons.people, 'Máx. usuarios', '${sub.maxUsers}'),
            _infoRow(Icons.security, 'Máx. roles', '${sub.maxRoles}'),
            _infoRow(
              Icons.calendar_today,
              'Inicio',
              _formatDate(sub.startedAt),
            ),
            _infoRow(Icons.event_busy, 'Expira', _formatDate(sub.expiresAt)),
            if (sub.remainingDays < 0)
              Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Text(
                  'Suscripción vencida',
                  style: TextStyle(color: Colors.red.shade700),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, size: 20, color: const Color(0xFF4CAF50)),
          const SizedBox(width: 16),
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: const TextStyle(
                fontWeight: FontWeight.w500,
                color: Colors.grey,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
