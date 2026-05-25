import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class RoleSkeleton extends StatelessWidget {
  const RoleSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 16),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: ListTile(
          leading: const CircleAvatar(child: Icon(Icons.security)),
          title: const Text('Nombre del rol'),
          subtitle: const Text('Descripción del rol'),
          trailing: const Chip(label: Text('Activo')),
        ),
      ),
    );
  }
}
