import 'package:flutter/material.dart';
import '../../core/di/injection_container.dart' as di;
import '../viewmodels/roles_viewmodel.dart';

class RolesPage extends StatefulWidget {
  const RolesPage({super.key});

  @override
  State<RolesPage> createState() => _RolesPageState();
}

class _RolesPageState extends State<RolesPage> {
  late RolesViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<RolesViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadRoles();
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
      appBar: AppBar(title: const Text('roles del sistema')),
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
              onPressed: () => _viewModel.loadRoles(),
              child: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    if (_viewModel.roles.isEmpty) {
      return const Center(child: Text('No hay roles disponibles'));
    }

    return ListView(
      padding: const EdgeInsets.all(8),
      children: _viewModel.roles.map((role) {
        return Card(
          margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 16),
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: role.active
                  ? Colors.blue[100]
                  : Colors.grey[300],
              child: Icon(
                Icons.security,
                color: role.active ? Colors.blue[700] : Colors.grey,
              ),
            ),
            title: Text(role.name),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(role.description),
                const SizedBox(height: 4),
                Text(
                  '${role.permissionsCodes.length} permiso(s)',
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                ),
              ],
            ),
            trailing: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: role.active ? Colors.green[100] : Colors.red[100],
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                role.active ? 'Activo' : 'Inactivo',
                style: TextStyle(
                  color: role.active ? Colors.green[700] : Colors.red[700],
                  fontSize: 12,
                ),
              ),
            ),
            onTap: () {},
          ),
        );
      }).toList(),
    );
  }
}
