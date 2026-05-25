import 'package:flutter/material.dart';

class NotificationOptionsModal extends StatelessWidget {
  final String notificationId;
  final VoidCallback onArchive;

  const NotificationOptionsModal({
    super.key,
    required this.notificationId,
    required this.onArchive,
  });

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            leading: const Icon(Icons.archive_outlined),
            title: const Text('Archivar'),
            onTap: () {
              Navigator.pop(context);
              onArchive();
            },
          ),
        ],
      ),
    );
  }
}
