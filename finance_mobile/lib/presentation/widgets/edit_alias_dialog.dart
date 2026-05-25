// lib/presentation/widgets/edit_alias_dialog.dart
import 'package:flutter/material.dart';

class EditAliasDialog extends StatelessWidget {
  final String currentAlias;
  final Future<void> Function(String newAlias) onSave;

  const EditAliasDialog({
    super.key,
    required this.currentAlias,
    required this.onSave,
  });

  @override
  Widget build(BuildContext context) {
    final aliasController = TextEditingController(text: currentAlias);

    return AlertDialog(
      title: const Text('Editar alias'),
      content: TextField(
        controller: aliasController,
        decoration: const InputDecoration(
          labelText: 'Alias',
          hintText: 'Ej: Mi cuenta principal',
        ),
      ),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () async {
            Navigator.pop(context);
            await onSave(aliasController.text.trim());
          },
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF2E7D32),
            foregroundColor: Colors.white,
          ),
          child: const Text('Guardar'),
        ),
      ],
    );
  }
}
