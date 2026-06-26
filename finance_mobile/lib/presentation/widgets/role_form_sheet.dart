import 'package:flutter/material.dart';
import '../viewmodels/permissions_viewmodel.dart';
import '../../domain/entities/permission.dart';
import '../../domain/entities/role.dart';

class RoleFormSheet extends StatefulWidget {
  final PermissionsViewModel permissionsViewModel;
  final Role? roleToEdit;
  final Future<void> Function(
    String name,
    String description,
    List<String> permissions,
  )
  onSubmit;

  const RoleFormSheet({
    super.key,
    required this.permissionsViewModel,
    this.roleToEdit,
    required this.onSubmit,
  });

  @override
  State<RoleFormSheet> createState() => _RoleFormSheetState();
}

class _RoleFormSheetState extends State<RoleFormSheet> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _descController = TextEditingController();
  final Set<String> _selectedCodes = {};
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    if (widget.roleToEdit != null) {
      _nameController.text = widget.roleToEdit!.name;
      _descController.text = widget.roleToEdit!.description;
      _selectedCodes.addAll(widget.roleToEdit!.permissionCodes);
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _descController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedCodes.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Selecciona al menos un permiso')),
      );
      return;
    }
    setState(() => _submitting = true);
    await widget.onSubmit(
      _nameController.text.trim(),
      _descController.text.trim(),
      _selectedCodes.toList(),
    );
    setState(() => _submitting = false);
  }

  @override
  Widget build(BuildContext context) {
    final permissions = widget.permissionsViewModel.permissions;
    final loadingPerms = widget.permissionsViewModel.loading;

    final grouped = <String, List<Permission>>{};
    for (final p in permissions) {
      grouped.putIfAbsent(p.module, () => []).add(p);
    }

    return Padding(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom,
      ),
      child: Container(
        constraints: BoxConstraints(
          maxHeight: MediaQuery.of(context).size.height * 0.85,
        ),
        padding: const EdgeInsets.fromLTRB(24, 16, 24, 24),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHandleBar(),
              const SizedBox(height: 16),
              _buildTitle(),
              const SizedBox(height: 20),
              _buildNameField(),
              const SizedBox(height: 16),
              _buildDescriptionField(),
              const SizedBox(height: 20),
              _buildPermissionsHeader(),
              const SizedBox(height: 8),
              _buildSelectAllPermissions(permissions),
              const SizedBox(height: 12),
              Expanded(
                child: loadingPerms
                    ? const Center(child: CircularProgressIndicator())
                    : permissions.isEmpty
                    ? const Text('No se pudieron cargar los permisos')
                    : SingleChildScrollView(
                        child: _buildPermissionsList(grouped),
                      ),
              ),
              const SizedBox(height: 20),
              _buildSubmitButton(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHandleBar() {
    return Center(
      child: Container(
        width: 40,
        height: 4,
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: BorderRadius.circular(2),
        ),
      ),
    );
  }

  Widget _buildTitle() {
    return Text(
      widget.roleToEdit != null ? 'Editar rol' : 'Crear nuevo rol',
      style: const TextStyle(
        fontSize: 20,
        fontWeight: FontWeight.bold,
        color: Color(0xFF2E7D32),
      ),
    );
  }

  Widget _buildNameField() {
    return TextFormField(
      controller: _nameController,
      decoration: InputDecoration(
        labelText: 'Nombre del rol',
        prefixIcon: const Icon(Icons.badge_outlined),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
      ),
      validator: (v) =>
          (v == null || v.trim().isEmpty) ? 'Campo requerido' : null,
    );
  }

  Widget _buildDescriptionField() {
    return TextFormField(
      controller: _descController,
      decoration: InputDecoration(
        labelText: 'Descripción',
        prefixIcon: const Icon(Icons.notes_outlined),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
      ),
      validator: (v) =>
          (v == null || v.trim().isEmpty) ? 'Campo requerido' : null,
    );
  }

  Widget _buildPermissionsHeader() {
    return Row(
      children: [
        const Icon(Icons.vpn_key_outlined, size: 18, color: Color(0xFF2E7D32)),
        const SizedBox(width: 6),
        Text(
          'Permisos (${_selectedCodes.length} seleccionados)',
          style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
        ),
      ],
    );
  }

  Widget _buildSelectAllPermissions(List<Permission> permissions) {
    final allSelected = permissions.isNotEmpty && _selectedCodes.length == permissions.length;
    final someSelected = _selectedCodes.isNotEmpty && !allSelected;

    return Card(
      elevation: 0,
      color: const Color(0xFFF7FBF3),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: Color(0xFFDDEED8)),
      ),
      child: CheckboxListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
        controlAffinity: ListTileControlAffinity.leading,
        activeColor: const Color(0xFF2E7D32),
        tristate: true,
        value: allSelected ? true : (someSelected ? null : false),
        title: const Text(
          'Seleccionar todos los permisos',
          style: TextStyle(fontWeight: FontWeight.w700),
        ),
        subtitle: Text(
          someSelected
              ? '${_selectedCodes.length} de ${permissions.length} seleccionados'
              : allSelected
                  ? 'Todos los permisos fueron asignados'
                  : 'Marca esta opción para asignar todo el catálogo',
        ),
        onChanged: _submitting
            ? null
            : (checked) {
                setState(() {
                  if (checked == true) {
                    _selectedCodes
                      ..clear()
                      ..addAll(permissions.map((p) => p.code));
                  } else {
                    _selectedCodes.clear();
                  }
                });
              },
      ),
    );
  }

  Widget _buildPermissionsList(Map<String, List<Permission>> grouped) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: grouped.entries.map((entry) {
        final module = entry.key;
        final perms = entry.value;
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.only(top: 8, bottom: 2),
              child: Text(
                module.toUpperCase(),
                style: TextStyle(
                  fontSize: 11,
                  fontWeight: FontWeight.bold,
                  color: Colors.grey[600],
                  letterSpacing: 1.2,
                ),
              ),
            ),
            _buildModuleToggle(module, perms),
            ...perms.map((p) {
              final selected = _selectedCodes.contains(p.code);
              return CheckboxListTile(
                dense: true,
                contentPadding: EdgeInsets.zero,
                activeColor: const Color(0xFF2E7D32),
                value: selected,
                title: Text(p.code, style: const TextStyle(fontSize: 13)),
                subtitle: Text(
                  p.description,
                  style: const TextStyle(fontSize: 11),
                ),
                onChanged: (checked) {
                  setState(() {
                    if (checked == true) {
                      _selectedCodes.add(p.code);
                    } else {
                      _selectedCodes.remove(p.code);
                    }
                  });
                },
              );
            }),
            const Divider(height: 1),
          ],
        );
      }).toList(),
    );
  }

  Widget _buildModuleToggle(String module, List<Permission> perms) {
    final selectedCount = perms.where((p) => _selectedCodes.contains(p.code)).length;
    final allSelected = perms.isNotEmpty && selectedCount == perms.length;
    final someSelected = selectedCount > 0 && selectedCount < perms.length;

    return CheckboxListTile(
      dense: true,
      contentPadding: const EdgeInsets.symmetric(horizontal: 0),
      controlAffinity: ListTileControlAffinity.leading,
      activeColor: const Color(0xFF2E7D32),
      tristate: true,
      value: allSelected ? true : (someSelected ? null : false),
      title: Text(
        'Seleccionar todo el módulo',
        style: TextStyle(fontSize: 12, color: Colors.grey[800]),
      ),
      subtitle: Text(
        '$selectedCount de ${perms.length} permisos',
        style: TextStyle(fontSize: 11, color: Colors.grey[600]),
      ),
      onChanged: _submitting
          ? null
          : (checked) {
              setState(() {
                if (checked == true) {
                  _selectedCodes.addAll(perms.map((p) => p.code));
                } else {
                  _selectedCodes.removeWhere((code) => perms.any((p) => p.code == code));
                }
              });
            },
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton.icon(
        onPressed: _submitting ? null : _submit,
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFF2E7D32),
          foregroundColor: Colors.white,
          padding: const EdgeInsets.symmetric(vertical: 14),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        icon: _submitting
            ? const SizedBox(
                width: 18,
                height: 18,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  color: Colors.white,
                ),
              )
            : const Icon(Icons.save_outlined),
        label: Text(
          _submitting
              ? 'Guardando...'
              : (widget.roleToEdit != null ? 'Actualizar Rol' : 'Crear Rol'),
        ),
      ),
    );
  }
}
