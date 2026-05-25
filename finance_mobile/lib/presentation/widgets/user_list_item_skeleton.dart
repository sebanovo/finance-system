import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class UserListItemSkeleton extends StatelessWidget {
  const UserListItemSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 600;

    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: isSmallScreen
            ? _buildVerticalLayout()
            : _buildHorizontalLayout(),
      ),
    );
  }

  // 📱 Layout vertical para pantallas pequeñas
  Widget _buildVerticalLayout() {
    return const Padding(
      padding: EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Fila superior: Avatar + Nombre
          Row(
            children: [
              CircleAvatar(radius: 24, child: Icon(Icons.person, size: 28)),
              SizedBox(width: 12),
              Expanded(
                child: Text(
                  'Nombre de usuario',
                  style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16),
                ),
              ),
              Icon(Icons.edit, size: 20),
            ],
          ),
          SizedBox(height: 8),
          // Email
          Text('correo@ejemplo.com', style: TextStyle(fontSize: 12)),
          SizedBox(height: 12),
          // Fila inferior: Rol + Estado
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Chip(
                label: Text('Rol de ejemplo'),
                backgroundColor: Color(0xFFE8F5E9),
              ),
              SizedBox(width: 8),
              _StatusBadgeSkeleton(),
            ],
          ),
        ],
      ),
    );
  }

  // 💻 Layout horizontal para pantallas grandes
  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: const CircleAvatar(
        radius: 24,
        child: Icon(Icons.person, size: 28),
      ),
      title: const Text(
        'Nombre de usuario',
        style: TextStyle(fontWeight: FontWeight.w600),
      ),
      subtitle: const Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('correo@ejemplo.com', style: TextStyle(fontSize: 12)),
          SizedBox(height: 6),
          Chip(
            label: Text('Rol de ejemplo'),
            backgroundColor: Color(0xFFE8F5E9),
          ),
        ],
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.edit, size: 20),
          const SizedBox(width: 8),
          const _StatusBadgeSkeleton(),
        ],
      ),
    );
  }
}

// Widget auxiliar para el badge de estado en skeleton
class _StatusBadgeSkeleton extends StatelessWidget {
  const _StatusBadgeSkeleton();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.grey.shade300,
        borderRadius: BorderRadius.circular(20),
      ),
      child: const Text(
        'Activo',
        style: TextStyle(fontSize: 12, fontWeight: FontWeight.w500),
      ),
    );
  }
}
