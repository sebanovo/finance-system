import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';
import '../../domain/entities/user.dart';
import '../../domain/entities/role.dart';

class UserListItem extends StatelessWidget {
  final User user;
  final List<Role> roles;
  final bool isLoadingRoles;
  final String? currentRoleId;
  final VoidCallback onEditRole;
  final bool isLoading;

  const UserListItem({
    super.key,
    required this.user,
    required this.roles,
    required this.isLoadingRoles,
    required this.currentRoleId,
    required this.onEditRole,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 600;

    return Skeletonizer(
      enabled: isLoading,
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
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Fila superior: Avatar + Nombre + Botón editar
          Row(
            children: [
              _buildAvatar(),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  isLoading
                      ? 'Nombre de usuario'
                      : (user.fullName.isNotEmpty ? user.fullName : user.email),
                  style: const TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 16,
                  ),
                ),
              ),
              IconButton(
                icon: const Icon(
                  Icons.edit,
                  size: 20,
                  color: Color(0xFF4CAF50),
                ),
                onPressed: isLoading ? null : onEditRole,
                tooltip: 'Editar rol',
              ),
            ],
          ),
          const SizedBox(height: 8),
          // Email
          Text(
            isLoading ? 'correo@ejemplo.com' : user.email,
            style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
          ),
          const SizedBox(height: 12),
          // Fila inferior: Rol + Estado
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(child: _buildRoleChip()),
              const SizedBox(width: 8),
              _buildStatusBadge(),
            ],
          ),
        ],
      ),
    );
  }

  // 💻 Layout horizontal para pantallas grandes (tablet, desktop)
  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: _buildAvatar(),
      title: Text(
        isLoading
            ? 'Nombre de usuario'
            : (user.fullName.isNotEmpty ? user.fullName : user.email),
        style: const TextStyle(fontWeight: FontWeight.w600),
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            isLoading ? 'correo@ejemplo.com' : user.email,
            style: const TextStyle(fontSize: 12),
          ),
          const SizedBox(height: 6),
          _buildRoleChip(),
        ],
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.edit, size: 20, color: Color(0xFF4CAF50)),
            onPressed: isLoading ? null : onEditRole,
            tooltip: 'Editar rol',
          ),
          _buildStatusBadge(),
        ],
      ),
    );
  }

  Widget _buildAvatar() {
    if (isLoading) {
      return const CircleAvatar(
        radius: 24,
        child: Icon(Icons.person, size: 28),
      );
    }
    return CircleAvatar(
      radius: 24,
      backgroundColor: user.active ? const Color(0xFFC8E6C9) : Colors.grey[300],
      child: Icon(
        Icons.person,
        size: 28,
        color: user.active ? const Color(0xFF2E7D32) : Colors.grey,
      ),
    );
  }

  Widget _buildRoleChip() {
    if (isLoading) {
      return const Chip(
        label: Text('Rol de ejemplo'),
        backgroundColor: Color(0xFFE8F5E9),
      );
    }
    if (isLoadingRoles) {
      return const SizedBox(
        height: 20,
        width: 20,
        child: CircularProgressIndicator(strokeWidth: 2),
      );
    }
    if (roles.isNotEmpty) {
      return Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Chip(
            label: Text(roles.first.name),
            backgroundColor: const Color(0xFFE8F5E9),
            labelStyle: const TextStyle(fontSize: 11, color: Color(0xFF2E7D32)),
            materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            padding: EdgeInsets.zero,
          ),
        ],
      );
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: Colors.grey.shade200,
        borderRadius: BorderRadius.circular(16),
      ),
      child: const Text(
        'Sin rol asignado',
        style: TextStyle(fontSize: 11, color: Colors.grey),
      ),
    );
  }

  Widget _buildStatusBadge() {
    if (isLoading) {
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
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: user.active ? Colors.green[100] : Colors.red[100],
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        user.active ? 'Activo' : 'Inactivo',
        style: TextStyle(
          color: user.active ? Colors.green[700] : Colors.red[700],
          fontSize: 12,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }
}
