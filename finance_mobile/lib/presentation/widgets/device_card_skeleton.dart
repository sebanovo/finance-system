import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class DeviceCardSkeleton extends StatelessWidget {
  const DeviceCardSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.only(bottom: 12),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: isSmallScreen
            ? _buildVerticalLayout()
            : _buildHorizontalLayout(),
      ),
    );
  }

  Widget _buildVerticalLayout() {
    return const Padding(
      padding: EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 24,
                child: Text('🤖', style: TextStyle(fontSize: 20)),
              ),
              SizedBox(width: 12),
              Expanded(
                child: Text(
                  'Nombre del dispositivo',
                  style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16),
                ),
              ),
            ],
          ),
          SizedBox(height: 8),
          Text('ANDROID - v1.0.0'),
          SizedBox(height: 4),
          Text('Último acceso: 01/01/2024 12:00'),
          SizedBox(height: 8),
          Align(
            alignment: Alignment.centerRight,
            child: _StatusBadgeSkeleton(),
          ),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: const CircleAvatar(
        radius: 24,
        child: Text('🤖', style: TextStyle(fontSize: 20)),
      ),
      title: const Text('Nombre del dispositivo'),
      subtitle: const Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('ANDROID - v1.0.0'),
          Text('Último acceso: 01/01/2024 12:00'),
        ],
      ),
      trailing: const _StatusBadgeSkeleton(),
    );
  }
}

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
