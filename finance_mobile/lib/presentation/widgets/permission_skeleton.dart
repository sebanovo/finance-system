import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class PermissionSkeleton extends StatelessWidget {
  const PermissionSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.symmetric(vertical: 8),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: ExpansionTile(
          title: const Text('MÓDULO EJEMPLO'),
          children: List.generate(
            3,
            (index) => const ListTile(
              leading: Icon(Icons.lock_outline, size: 20),
              title: Text('permiso.ejemplo'),
              subtitle: Text('Descripción del permiso de ejemplo'),
              dense: true,
            ),
          ),
        ),
      ),
    );
  }
}
