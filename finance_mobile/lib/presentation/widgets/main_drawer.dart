import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../viewmodels/home_viewmodel.dart';
import '../viewmodels/notifications_viewmodel.dart';

class MainDrawer extends StatelessWidget {
  final HomeViewModel viewModel;
  final NotificationsViewModel notifViewModel;
  final VoidCallback onChangePassword;
  final VoidCallback onLogout;

  const MainDrawer({
    super.key,
    required this.viewModel,
    required this.notifViewModel,
    required this.onChangePassword,
    required this.onLogout,
  });

  bool _hasAnyRole(List<String> roles) {
    final userRoles = viewModel.userInfo?.roles ?? [];
    return roles.any((role) => userRoles.contains(role));
  }

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: Container(
        color: Colors.white,
        child: ListView(
          children: [
            _buildDrawerHeader(),
            _buildDashboardItem(context),
            if (_hasAnyRole(['OWNER_ADMIN', 'ADMIN'])) ...[
              _buildDrawerItem(
                icon: Icons.people,
                title: 'Usuarios',
                onTap: () => context.push('/users'),
              ),
              _buildDrawerItem(
                icon: Icons.security,
                title: 'Roles',
                onTap: () => context.push('/roles'),
              ),
              _buildDrawerItem(
                icon: Icons.vpn_key,
                title: 'Permisos',
                onTap: () => context.push('/permissions'),
              ),
            ],
            if (_hasAnyRole(['USER'])) ...[
              _buildDrawerItem(
                icon: Icons.account_balance_wallet,
                title: 'Mis Cuentas',
                onTap: () => context.push('/accounts'),
              ),
              _buildDrawerItem(
                icon: Icons.history,
                title: 'Movimientos',
                onTap: () => context.push('/transactions'),
              ),
              _buildDrawerItem(
                icon: Icons.notifications,
                title: 'Mis notificaciones',
                onTap: () => context.push('/notifications'),
              ),
              _buildDrawerItem(
                icon: Icons.phone_android,
                title: 'Mis Dispositivos',
                onTap: () => context.push('/devices'),
              ),
            ],
            _buildDrawerItem(
              icon: Icons.lock_reset,
              title: 'Cambiar contraseña',
              onTap: onChangePassword,
            ),
            _buildDrawerItem(
              icon: Icons.logout,
              title: 'Cerrar sesión',
              onTap: onLogout,
              isDestructive: true,
            ),
            if (_hasAnyRole(['USER']))
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: ElevatedButton(
                  onPressed: () async {
                    await notifViewModel.registerCurrentDevice();
                  },
                  // style: ButtonStyle(textStyle: ),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green, // Button background
                    foregroundColor: Colors.white, // Text/Icon color
                    elevation: 5, // Shadow depth
                    padding: const EdgeInsets.symmetric(
                      horizontal: 30,
                      vertical: 15,
                    ),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(
                        10,
                      ), // Rounded corners
                    ),
                  ),
                  child: const Text('Registrar dispositivo'),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildDrawerHeader() {
    if (viewModel.loadingUserInfo) {
      return const DrawerHeader(
        decoration: BoxDecoration(color: Color(0xFF2E7D32)),
        child: Center(child: CircularProgressIndicator(color: Colors.white)),
      );
    }
    if (viewModel.userInfo != null) {
      return UserAccountsDrawerHeader(
        decoration: const BoxDecoration(color: Color(0xFF2E7D32)),
        accountName: Text(
          viewModel.userInfo!.displayName,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        accountEmail: Text(viewModel.userInfo!.email),
        currentAccountPicture: CircleAvatar(
          backgroundColor: Colors.white,
          child: Text(
            viewModel.userInfo!.initial,
            style: const TextStyle(fontSize: 24, color: Color(0xFF2E7D32)),
          ),
        ),
        otherAccountsPictures: [
          Tooltip(
            message: 'Roles: ${viewModel.userInfo!.roles.join(', ')}',
            child: const Icon(Icons.verified_user, color: Colors.white),
          ),
        ],
      );
    }
    return const SizedBox.shrink();
  }

  Widget _buildDashboardItem(BuildContext context) {
    return _buildDrawerItem(
      icon: Icons.dashboard,
      title: 'Dashboard',
      onTap: () => Navigator.pop(context),
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
}
