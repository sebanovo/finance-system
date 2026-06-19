import 'package:flutter/material.dart';
import '../../domain/entities/role.dart';

class EditRoleDialog extends StatefulWidget {
  final String userId;
  final String? currentRoleId;
  final List<Role> availableRoles;
  final Future<void> Function(String userId, String roleId) onAssignRole;

  const EditRoleDialog({
    super.key,
    required this.userId,
    required this.currentRoleId,
    required this.availableRoles,
    required this.onAssignRole,
  });

  @override
  State<EditRoleDialog> createState() => _EditRoleDialogState();
}

class _EditRoleDialogState extends State<EditRoleDialog> {
  late String? _selectedRoleId;
  bool _isSubmitting = false;

  @override
  void initState() {
    super.initState();
    _selectedRoleId = widget.currentRoleId;
  }

  Future<void> _submit() async {
    if (_selectedRoleId == null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Selecciona un rol')));
      return;
    }

    setState(() => _isSubmitting = true);
    try {
      await widget.onAssignRole(widget.userId, _selectedRoleId!);
      if (mounted) Navigator.pop(context);
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Asignar Rol'),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      content: DropdownButtonFormField<String>(
        decoration: const InputDecoration(labelText: 'Selecciona un rol'),
        initialValue: _selectedRoleId,
        items: widget.availableRoles.map((role) {
          return DropdownMenuItem<String>(
            value: role.id,
            child: Text(role.name),
          );
        }).toList(),
        onChanged: (value) {
          setState(() {
            _selectedRoleId = value;
          });
        },
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: _isSubmitting ? null : _submit,
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF2E7D32),
            foregroundColor: Colors.white,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: _isSubmitting
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    color: Colors.white,
                  ),
                )
              : const Text('Guardar'),
        ),
      ],
    );
  }
}
