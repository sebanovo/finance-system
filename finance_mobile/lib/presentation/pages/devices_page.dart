// lib/presentation/pages/devices_page.dart
import 'package:flutter/material.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/devices_viewmodel.dart';
import '../widgets/device_card.dart';
import '../widgets/device_card_skeleton.dart';

class DevicesPage extends StatefulWidget {
  const DevicesPage({super.key});

  @override
  State<DevicesPage> createState() => _DevicesPageState();
}

class _DevicesPageState extends State<DevicesPage> {
  late DevicesViewModel _viewModel;
  static const int _skeletonItemCount = 1;

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

  Future<void> _confirmDeactivate(device) async {
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

  Future<void> _confirmRevoke(device) async {
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
      return ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _skeletonItemCount,
        itemBuilder: (context, index) => const DeviceCardSkeleton(),
      );
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
          return DeviceCard(
            device: device,
            onDeactivate: () => _confirmDeactivate(device),
            onRevoke: () => _confirmRevoke(device),
          );
        },
      ),
    );
  }
}
