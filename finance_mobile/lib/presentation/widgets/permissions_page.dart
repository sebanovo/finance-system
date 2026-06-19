import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../viewmodels/permissions_viewmodel.dart';
import '../../core/di/injection_container.dart' as di;
import '../widgets/permission_card.dart';
import '../widgets/permission_skeleton.dart';

class PermissionsPage extends StatefulWidget {
  const PermissionsPage({super.key});

  @override
  State<PermissionsPage> createState() => _PermissionsPageState();
}

class _PermissionsPageState extends State<PermissionsPage> {
  late PermissionsViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<PermissionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadPermissions();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    setState(() {});
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Permisos del sistema'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    // ✅ Skeleton mientras carga
    if (_viewModel.loading) {
      return ListView.builder(
        padding: const EdgeInsets.all(8),
        itemCount: 4,
        itemBuilder: (context, index) => const PermissionSkeleton(),
      );
    }

    if (_viewModel.errorMessage != null) {
      return _buildErrorWidget();
    }

    if (_viewModel.permissions.isEmpty) {
      return const Center(child: Text('No hay permisos disponibles'));
    }

    final grouped = _viewModel.groupByModule;
    return ListView(
      padding: const EdgeInsets.all(8),
      children: grouped.entries.map((entry) {
        return PermissionCard(module: entry.key, permissions: entry.value);
      }).toList(),
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
            onPressed: () => _viewModel.loadPermissions(),
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
