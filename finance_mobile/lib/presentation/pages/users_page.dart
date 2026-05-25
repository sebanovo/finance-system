import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/users_viewmodel.dart';
import '../widgets/create_user_dialog.dart';
import '../widgets/edit_role_dialog.dart';
import '../widgets/user_list_item.dart';
import '../widgets/user_list_item_skeleton.dart';

class UsersPage extends StatefulWidget {
  const UsersPage({super.key});

  @override
  State<UsersPage> createState() => _UsersPageState();
}

class _UsersPageState extends State<UsersPage> {
  late UsersViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<UsersViewModel>();
    _viewModel.addListener(_onViewModelChanged);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null &&
        (_viewModel.errorMessage!.contains('Sesión expirada') ||
            _viewModel.errorMessage!.contains('401') ||
            _viewModel.errorMessage!.contains('No hay sesión activa'))) {
      _showSnackBar(
        'Tu sesión ha expirado. Por favor inicia sesión nuevamente.',
      );
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.go('/login');
      });
    } else {
      setState(() {});
    }
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  Future<void> _createUser({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    try {
      await _viewModel.createUser(
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
      );
      _showSnackBar('Usuario creado exitosamente', isError: false);
    } catch (e) {
      _showSnackBar('Error: $e');
    }
  }

  Future<void> _assignRole(String userId, String roleId) async {
    try {
      await _viewModel.assignRole(userId, roleId);
      _showSnackBar('Rol asignado correctamente', isError: false);
    } catch (e) {
      _showSnackBar('Error: $e');
    }
  }

  void _showCreateUserDialog() {
    showDialog(
      context: context,
      builder: (context) => CreateUserDialog(onCreateUser: _createUser),
    );
  }

  void _showEditRoleDialog(String userId, String? currentRoleId) {
    showDialog(
      context: context,
      builder: (context) => EditRoleDialog(
        userId: userId,
        currentRoleId: currentRoleId,
        availableRoles: _viewModel.availableRoles,
        onAssignRole: _assignRole,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // if (_viewModel.loadingRolesList) {
    //   return _buildLoadingScaffold();
    // }

    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBody(),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateUserDialog,
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        child: const Icon(Icons.add),
      ),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      title: const Text('Usuarios del Tenant'),
      backgroundColor: Colors.white,
      elevation: 0,
      foregroundColor: const Color(0xFF2E7D32),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading) {
      return ListView.builder(
        padding: const EdgeInsets.all(8),
        itemCount: 3,
        itemBuilder: (context, index) => const UserListItemSkeleton(),
      );
    }

    if (_viewModel.errorMessage != null) {
      return _buildErrorWidget();
    }

    if (_viewModel.users.isEmpty) {
      return const Center(child: Text('No hay usuarios registrados'));
    }

    return ListView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: _viewModel.users.length,
      itemBuilder: (context, index) {
        final user = _viewModel.users[index];
        final roles = _viewModel.rolesMap[user.id] ?? [];
        final loadingRoles = _viewModel.loadingRolesMap[user.id] ?? false;
        final currentRoleId = roles.isNotEmpty ? roles.first.id : null;

        return UserListItem(
          user: user,
          roles: roles,
          isLoadingRoles: loadingRoles,
          currentRoleId: currentRoleId,
          onEditRole: () => _showEditRoleDialog(user.id, currentRoleId),
        );
      },
    );
  }

  Widget _buildErrorWidget() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 64, color: Colors.red.shade300),
          const SizedBox(height: 16),
          Text(
            _viewModel.errorMessage!,
            style: const TextStyle(fontSize: 16),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => _viewModel.loadUsers(),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
            ),
            child: const Text('Reintentar'),
          ),
          const SizedBox(height: 12),
          TextButton(
            onPressed: () => context.go('/login'),
            child: const Text('Ir a inicio de sesión'),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
