import 'package:flutter/material.dart';
import '../../domain/entities/role.dart';

class EditRoleDialog extends StatefulWidget {
  final String userId;
  final List<String> currentRoleIds;
  final List<Role> availableRoles;
  final Future<void> Function(String userId, List<String> roleIds) onAssignRoles;

  const EditRoleDialog({
    super.key,
    required this.userId,
    required this.currentRoleIds,
    required this.availableRoles,
    required this.onAssignRoles,
  });

  @override
  State<EditRoleDialog> createState() => _EditRoleDialogState();
}

class _EditRoleDialogState extends State<EditRoleDialog> {
  late final Set<String> _selectedRoleIds;
  bool _isSubmitting = false;

  @override
  void initState() {
    super.initState();
    _selectedRoleIds = widget.currentRoleIds.toSet();
  }

  Future<void> _submit() async {
    if (_selectedRoleIds.isEmpty) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Selecciona al menos un rol')));
      return;
    }

    setState(() => _isSubmitting = true);
    try {
      await widget.onAssignRoles(widget.userId, _selectedRoleIds.toList());
      if (mounted) Navigator.pop(context);
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Asignar roles'),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      content: SizedBox(
        width: 420,
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  'Puedes seleccionar más de un rol',
                  style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
                ),
              ),
              const SizedBox(height: 12),
              ...widget.availableRoles.map((role) {
                final selected = _selectedRoleIds.contains(role.id);
                return CheckboxListTile(
                  contentPadding: EdgeInsets.zero,
                  controlAffinity: ListTileControlAffinity.leading,
                  value: selected,
                  onChanged: _isSubmitting
                      ? null
                      : (checked) {
                          setState(() {
                            if (checked == true) {
                              _selectedRoleIds.add(role.id);
                            } else {
                              _selectedRoleIds.remove(role.id);
                            }
                          });
                        },
                  title: Text(role.name),
                  subtitle: Text(role.description),
                );
              }),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isSubmitting ? null : () => Navigator.pop(context),
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
