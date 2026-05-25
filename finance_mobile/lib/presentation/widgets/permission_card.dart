import 'package:flutter/material.dart';

class PermissionCard extends StatelessWidget {
  final String module;
  final List<dynamic> permissions;

  const PermissionCard({
    super.key,
    required this.module,
    required this.permissions,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: ExpansionTile(
        title: Text(
          module.toUpperCase(),
          style: const TextStyle(
            fontWeight: FontWeight.bold,
            color: Color(0xFF2E7D32),
          ),
        ),
        children: permissions
            .map((perm) => _buildPermissionItem(perm))
            .toList(),
      ),
    );
  }

  Widget _buildPermissionItem(dynamic perm) {
    return ListTile(
      leading: const Icon(
        Icons.lock_outline,
        size: 20,
        color: Color(0xFF4CAF50),
      ),
      title: Text(
        perm.code,
        style: const TextStyle(fontWeight: FontWeight.w500),
      ),
      subtitle: Text(perm.description),
      dense: true,
    );
  }
}
