import 'package:flutter/material.dart';
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
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Permisos del sistema')),
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
            Text(_viewModel.errorMessage!),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => _viewModel.loadPermissions(),
              child: const Text('Reintentar'),
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
          child: ExpansionTile(
            title: Text(
              entry.key.toUpperCase(),
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            children: entry.value.map((perm) {
              return ListTile(
                leading: const Icon(Icons.lock_outline, size: 20),
                title: Text(perm.code),
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
