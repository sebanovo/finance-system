import 'package:flutter/material.dart';
import '../../domain/entities/notification.dart';

class NotificationItem extends StatelessWidget {
  final AppNotification notification;
  final VoidCallback onTap;
  final VoidCallback onLongPress;

  const NotificationItem({
    super.key,
    required this.notification,
    required this.onTap,
    required this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      color: notification.isUnread ? const Color(0xFFF1F8E9) : Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: onTap,
        onLongPress: onLongPress,
        child: isSmallScreen
            ? _buildVerticalLayout()
            : _buildHorizontalLayout(),
      ),
    );
  }

  Widget _buildVerticalLayout() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildAvatar(),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      notification.title,
                      style: TextStyle(
                        fontWeight: notification.isUnread
                            ? FontWeight.bold
                            : FontWeight.normal,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      notification.body,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(fontSize: 13),
                    ),
                  ],
                ),
              ),
              if (notification.isUnread) _buildUnreadDot(),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: _buildAvatar(),
      title: Text(
        notification.title,
        style: TextStyle(
          fontWeight: notification.isUnread
              ? FontWeight.bold
              : FontWeight.normal,
        ),
      ),
      subtitle: Text(
        notification.body,
        maxLines: 2,
        overflow: TextOverflow.ellipsis,
      ),
      trailing: notification.isUnread ? _buildUnreadDot() : null,
    );
  }

  Widget _buildAvatar() {
    return CircleAvatar(
      backgroundColor: notification.isUnread
          ? const Color(0xFF2E7D32)
          : Colors.grey.shade300,
      child: Icon(
        _getIconForCategory(notification.category),
        color: Colors.white,
        size: 20,
      ),
    );
  }

  Widget _buildUnreadDot() {
    return Container(
      width: 8,
      height: 8,
      decoration: const BoxDecoration(
        color: Color(0xFF2E7D32),
        shape: BoxShape.circle,
      ),
    );
  }

  IconData _getIconForCategory(String category) {
    switch (category) {
      case 'TRANSACTIONS':
        return Icons.payment;
      case 'SECURITY':
        return Icons.security;
      default:
        return Icons.notifications;
    }
  }
}
