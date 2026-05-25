import 'package:flutter/material.dart';
import '../../domain/entities/notification_device.dart';

class DeviceCard extends StatelessWidget {
  final NotificationDevice device;
  final VoidCallback onDeactivate;
  final VoidCallback onRevoke;

  const DeviceCard({
    super.key,
    required this.device,
    required this.onDeactivate,
    required this.onRevoke,
  });

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: isSmallScreen ? _buildVerticalLayout() : _buildHorizontalLayout(),
    );
  }

  // 📱 Layout vertical para pantallas pequeñas
  Widget _buildVerticalLayout() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Fila superior: Avatar + Nombre
          Row(
            children: [
              _buildAvatar(),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  device.deviceName,
                  style: const TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 16,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              if (device.isActive) _buildPopupMenu(),
            ],
          ),
          const SizedBox(height: 8),
          // Información del dispositivo
          Text(
            '${device.platform} - v${device.appVersion}',
            style: const TextStyle(fontSize: 12),
          ),
          const SizedBox(height: 4),
          Text(
            'Último acceso: ${_formatDate(device.lastSeenAt)}',
            style: const TextStyle(fontSize: 11, color: Colors.grey),
          ),
          const SizedBox(height: 8),
          // Estado
          Align(alignment: Alignment.centerRight, child: _buildStatusBadge()),
        ],
      ),
    );
  }

  // 💻 Layout horizontal para pantallas grandes
  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: _buildAvatar(),
      title: Text(
        device.deviceName,
        style: const TextStyle(fontWeight: FontWeight.w600),
        overflow: TextOverflow.ellipsis,
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
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [_buildStatusBadge(), if (device.isActive) _buildPopupMenu()],
      ),
    );
  }

  Widget _buildAvatar() {
    return CircleAvatar(
      radius: 24,
      backgroundColor: device.isActive
          ? const Color(0xFFE8F5E9)
          : Colors.grey.shade200,
      child: Text(
        _getPlatformIcon(device.platform),
        style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
      ),
    );
  }

  String _getPlatformIcon(String platform) {
    switch (platform) {
      case 'ANDROID':
        return '🤖';
      case 'IOS':
        return '🍎';
      case 'WEB':
        return '🌐';
      default:
        return '📱';
    }
  }

  Widget _buildStatusBadge() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: device.isActive ? Colors.green[100] : Colors.red[100],
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        device.isActive ? 'Activo' : device.status,
        style: TextStyle(
          color: device.isActive ? Colors.green[700] : Colors.red[700],
          fontSize: 12,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }

  Widget _buildPopupMenu() {
    return PopupMenuButton<String>(
      onSelected: (value) {
        if (value == 'deactivate') onDeactivate();
        if (value == 'revoke') onRevoke();
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
              Icon(Icons.delete_forever, size: 18, color: Colors.red),
              SizedBox(width: 8),
              Text('Revocar'),
            ],
          ),
        ),
      ],
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
