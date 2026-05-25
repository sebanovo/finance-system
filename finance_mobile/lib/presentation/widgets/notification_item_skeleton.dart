import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class NotificationItemSkeleton extends StatelessWidget {
  const NotificationItemSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
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
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(child: Icon(Icons.notifications, size: 20)),
          SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Título de la notificación'),
                SizedBox(height: 4),
                Text('Descripción de la notificación...'),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return const ListTile(
      leading: CircleAvatar(child: Icon(Icons.notifications, size: 20)),
      title: Text('Título de la notificación'),
      subtitle: Text('Descripción de la notificación...'),
    );
  }
}
