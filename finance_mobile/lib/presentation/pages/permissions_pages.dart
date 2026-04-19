import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../viewmodels/permissions_viewmodel.dart';
import '../../core/di/injection_container.dart' as di;

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
    // Si el error es de autenticación (401), redirigir al login
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

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red.shade700),
    );
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
    if (_viewModel.loading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_viewModel.errorMessage != null) {
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

    if (_viewModel.permissions.isEmpty) {
      return const Center(child: Text('No hay permisos disponibles'));
    }

    final grouped = _viewModel.groupByModule;
    return ListView(
      padding: const EdgeInsets.all(8),
      children: grouped.entries.map((entry) {
        return Card(
          margin: const EdgeInsets.symmetric(vertical: 8),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          child: ExpansionTile(
            title: Text(
              entry.key.toUpperCase(),
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                color: Color(0xFF2E7D32),
              ),
            ),
            children: entry.value.map((perm) {
              return ListTile(
                leading: const Icon(
                  Icons.lock_outline,
                  size: 20,
                  color: Color(0xFF4CAF50),
                ),
                title: Text(
                  perm.code,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                subtitle: Text(perm.description),
                dense: true,
              );
            }).toList(),
          ),
        );
      }).toList(),
    );
  }
}
