import 'package:finance_mobile/domain/entities/notification_device.dart';
import 'package:flutter/material.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/devices_viewmodel.dart';

class DevicesPage extends StatefulWidget {
  const DevicesPage({super.key});

  @override
  State<DevicesPage> createState() => _DevicesPageState();
}

class _DevicesPageState extends State<DevicesPage> {
  late DevicesViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<DevicesViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadDevices();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!, isError: true);
      _viewModel.clearMessages();
    }
    if (_viewModel.successMessage != null) {
      _showSnackBar(_viewModel.successMessage!, isError: false);
      _viewModel.clearMessages();
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

  Future<void> _confirmDeactivate(NotificationDevice device) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Desactivar dispositivo'),
        content: Text(
          '¿Estás seguro de que deseas desactivar "${device.deviceName}"?',
        ),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red.shade700,
              foregroundColor: Colors.white,
            ),
            child: const Text('Desactivar'),
          ),
        ],
      ),
    );
    if (confirm == true) {
      await _viewModel.deactivateDevice(device.id);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mis Dispositivos'),
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

    if (_viewModel.errorMessage != null && _viewModel.devices.isEmpty) {
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
              onPressed: () => _viewModel.loadDevices(),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
              ),
              child: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    if (_viewModel.devices.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.devices, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            const Text(
              'No hay dispositivos registrados',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 8),
            const Text(
              'Los dispositivos aparecerán aquí cuando inicies sesión',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _viewModel.loadDevices(),
      color: const Color(0xFF2E7D32),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _viewModel.devices.length,
        itemBuilder: (context, index) {
          final device = _viewModel.devices[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: device.isActive
                    ? const Color(0xFFE8F5E9)
                    : Colors.grey.shade200,
                child: Text(device.platform, style: TextStyle(fontSize: 20)),
              ),
              title: Text(
                device.deviceName,
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${device.platform} - v${device.appVersion}',
                    style: const TextStyle(fontSize: 12),
                  ),
                  Text(
                    'Último acceso: ${_formatDate(device.lastSeenAt)}',
                    style: const TextStyle(fontSize: 11, color: Colors.grey),
                  ),
                ],
              ),
              // En el trailing del ListTile, modificar los botones
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 10,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: device.isActive
                          ? Colors.green[100]
                          : Colors.red[100],
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      device.isActive ? 'Activo' : device.status,
                      style: TextStyle(
                        color: device.isActive
                            ? Colors.green[700]
                            : Colors.red[700],
                        fontSize: 12,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                  if (device.isActive)
                    PopupMenuButton<String>(
                      onSelected: (value) {
                        if (value == 'deactivate') {
                          _confirmDeactivate(device);
                        } else if (value == 'revoke') {
                          _confirmRevoke(device);
                        }
                      },
                      itemBuilder: (context) => [
                        const PopupMenuItem(
                          value: 'deactivate',
                          child: Row(
                            children: [
                              Icon(Icons.block, size: 18, color: Colors.orange),
                              SizedBox(width: 8),
                              Text('Desactivar'),
                            ],
                          ),
                        ),
                        const PopupMenuItem(
                          value: 'revoke',
                          child: Row(
                            children: [
                              Icon(
                                Icons.delete_forever,
                                size: 18,
                                color: Colors.red,
                              ),
                              SizedBox(width: 8),
                              Text('Revocar'),
                            ],
                          ),
                        ),
                      ],
                    ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }

  Future<void> _confirmRevoke(NotificationDevice device) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Revocar dispositivo'),
        content: Text(
          '¿Estás seguro de que deseas revocar "${device.deviceName}"?\n\n'
          'Esta acción es irreversible y el dispositivo no podrá volver a conectarse.',
        ),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red.shade700,
              foregroundColor: Colors.white,
            ),
            child: const Text('Revocar'),
          ),
        ],
      ),
    );
    if (confirm == true) {
      await _viewModel.revokeDevice(device.id);
    }
  }
}
