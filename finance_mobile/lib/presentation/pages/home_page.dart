import '../../presentation/widgets/change_password_dialog.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/home_viewmodel.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late HomeViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<HomeViewModel>();
    _viewModel.addListener(_onViewModelChanged);
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
        backgroundColor: isError ? Colors.red : Colors.green,
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Dashboard")),
      drawer: _buildDrawer(),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadData(),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Center(
                child: Text(
                  "Bienvenido al Dashboard",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
              ),
              const SizedBox(height: 16),
              Center(child: Image.asset("logo.png", width: 100)),
              const SizedBox(height: 24),
              _buildSubscriptionCard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDrawer() {
    return Drawer(
      child: ListView(
        children: [
          if (_viewModel.loadingUserInfo)
            const DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Center(
                child: CircularProgressIndicator(color: Colors.white),
              ),
            )
          else if (_viewModel.userInfo != null)
            UserAccountsDrawerHeader(
              decoration: const BoxDecoration(color: Colors.blue),
              accountName: Text(_viewModel.userInfo!.displayName),
              accountEmail: Text(_viewModel.userInfo!.subject),
              currentAccountPicture: CircleAvatar(
                backgroundColor: Colors.white,
                child: Text(
                  _viewModel.userInfo!.initial,
                  style: const TextStyle(fontSize: 24, color: Colors.blue),
                ),
              ),
              otherAccountsPictures: [
                Tooltip(
                  message: 'Roles: ${_viewModel.userInfo!.roles.join(', ')}',
                  child: const Icon(Icons.verified_user, color: Colors.white),
                ),
              ],
            ),
          ListTile(
            leading: const Icon(Icons.dashboard),
            title: const Text("Dashboard"),
            onTap: () => Navigator.pop(context),
          ),
          ListTile(
            leading: const Icon(Icons.people),
            title: const Text("Usuarios"),
            onTap: () {
              Navigator.pop(context);
              context.push('/users');
            },
          ),
          ListTile(
            leading: const Icon(Icons.security),
            title: const Text("Roles"),
            onTap: () {
              Navigator.pop(context);
              context.push('/roles');
            },
          ),
          ListTile(
            leading: const Icon(Icons.vpn_key),
            title: const Text("Permisos"),
            onTap: () {
              Navigator.pop(context);
              context.push('/permissions');
            },
          ),
          ListTile(
            leading: const Icon(Icons.lock_reset),
            title: const Text("Cambiar contraseña"),
            onTap: () {
              Navigator.pop(context);
              _showChangePasswordDialog();
            },
          ),
          ListTile(
            leading: const Icon(Icons.logout),
            title: const Text("Cerrar sesión"),
            onTap: () => _logout(),
          ),
        ],
      ),
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
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  sub.planName,
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
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
                  color: Colors.blue[50],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.science, color: Colors.blue),
                    const SizedBox(width: 8),
                    Text(
                      'Período de prueba · ${sub.remainingDays} días restantes',
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
                  style: TextStyle(color: Colors.red[700]),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 18, color: Colors.grey[600]),
          const SizedBox(width: 12),
          SizedBox(
            width: 100,
            child: Text(label, style: TextStyle(color: Colors.grey[700])),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
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
