import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/di/injection_container.dart' as di;
import '../viewmodels/roles_viewmodel.dart';
import '../viewmodels/permissions_viewmodel.dart';
import '../../domain/usecases/create_role_usecase.dart';
import '../../domain/usecases/update_role_usecase.dart';
import '../../domain/entities/role.dart';
import '../widgets/role_card.dart';
import '../widgets/role_form_sheet.dart';
import '../widgets/role_skeleton.dart';

class RolesPage extends StatefulWidget {
  const RolesPage({super.key});

  @override
  State<RolesPage> createState() => _RolesPageState();
}

class _RolesPageState extends State<RolesPage> {
  late RolesViewModel _viewModel;
  late PermissionsViewModel _permissionsViewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<RolesViewModel>();
    _permissionsViewModel = di.sl<PermissionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadRoles();
    _permissionsViewModel.loadPermissions();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.roleCreated) {
      _viewModel.clearRoleCreated();
      _showSnackBar('Rol creado exitosamente', isError: false);
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

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  void _openCreateRoleDialog() {
    if (_permissionsViewModel.permissions.isEmpty) {
      _showSnackBar('Cargando permisos, intenta nuevamente...');
      return;
    }

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) => RoleFormSheet(
        permissionsViewModel: _permissionsViewModel,
        onSubmit: (name, desc, perms) async {
          await _viewModel.createRole(
            CreateRoleParams(
              name: name,
              description: desc,
              permissionCodes: perms,
            ),
          );
          if (mounted && _viewModel.errorMessage == null) {
            Navigator.of(context).pop();
          }
        },
      ),
    );
  }

  void _openEditRoleDialog(Role role) {
    if (_permissionsViewModel.permissions.isEmpty) {
      _showSnackBar('Cargando permisos, intenta nuevamente...');
      return;
    }

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) => RoleFormSheet(
        permissionsViewModel: _permissionsViewModel,
        roleToEdit: role,
        onSubmit: (name, desc, perms) async {
          await _viewModel.updateRole(
            UpdateRoleParams(
              id: role.id,
              name: name,
              description: desc,
              permissionCodes: perms,
            ),
          );
          if (mounted && _viewModel.errorMessage == null) {
            Navigator.of(context).pop();
            _showSnackBar('Rol actualizado exitosamente', isError: false);
          }
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Roles del sistema'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _openCreateRoleDialog,
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Nuevo Rol'),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading) {
      return ListView.builder(
        padding: const EdgeInsets.fromLTRB(0, 8, 0, 80),
        itemCount: 4,
        itemBuilder: (context, index) => const RoleSkeleton(),
      );
    }

    if (_viewModel.errorMessage != null) {
      return _buildErrorWidget();
    }

    if (_viewModel.roles.isEmpty) {
      return const Center(child: Text('No hay roles disponibles'));
    }

    return Stack(
      children: [
        ListView(
          padding: const EdgeInsets.fromLTRB(0, 8, 0, 80),
          children: _viewModel.roles.map((role) {
            return RoleCard(
              role: role,
              onEdit: () => _openEditRoleDialog(role),
              onActivate: () => _viewModel.activateRole(role.id),
              onDeactivate: () => _viewModel.deactivateRole(role.id),
              isToggling: _viewModel.toggling,
            );
          }).toList(),
        ),
        if (_viewModel.creating || _viewModel.toggling)
          Container(
            color: Colors.black26,
            child: const Center(child: CircularProgressIndicator()),
          ),
      ],
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
            onPressed: () => _viewModel.loadRoles(),
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
}
