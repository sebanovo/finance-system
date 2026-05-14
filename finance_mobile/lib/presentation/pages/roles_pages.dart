import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/di/injection_container.dart' as di;
import '../viewmodels/roles_viewmodel.dart';
import '../viewmodels/permissions_viewmodel.dart';
import '../../domain/usecases/create_role_usecase.dart';
import '../../domain/usecases/update_role_usecase.dart';
import '../../domain/entities/role.dart';

class RolesPage extends StatefulWidget {
  const RolesPage({super.key});

  @override
  State<RolesPage> createState() => _RolesPageState();
}

class _RolesPageState extends State<RolesPage> {
  late RolesViewModel _viewModel;
  late PermissionsViewModel _permissionsViewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<RolesViewModel>();
    _permissionsViewModel = di.sl<PermissionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadRoles();
    _permissionsViewModel.loadPermissions();
  }

  void _onViewModelChanged() {
    if (!mounted) return;

    // Redirigir al login si la sesión expiró
    if (_viewModel.errorMessage != null &&
        (_viewModel.errorMessage!.contains('Sesión expirada') ||
            _viewModel.errorMessage!.contains('401') ||
            _viewModel.errorMessage!.contains('No hay sesión activa'))) {
      _showSnackBar(
        'Tu sesión ha expirado. Por favor inicia sesión nuevamente.',
      );
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.go('/login');
      });
    }
    // Mostrar mensaje de éxito al crear rol
    else if (_viewModel.roleCreated) {
      _viewModel.clearRoleCreated();
      _showSnackBar('Rol creado exitosamente', isError: false);
    }

    setState(() {});
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  void _openCreateRoleDialog() {
    if (_permissionsViewModel.permissions.isEmpty) {
      _showSnackBar('Cargando permisos, intenta nuevamente...');
      return;
    }

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) => _RoleFormSheet(
        permissionsViewModel: _permissionsViewModel,
        onSubmit: (name, desc, perms) async {
          await _viewModel.createRole(
            CreateRoleParams(
              name: name,
              description: desc,
              permissionCodes: perms,
            ),
          );
          if (mounted && _viewModel.errorMessage == null) {
            Navigator.of(context).pop();
          }
        },
      ),
    );
  }

  void _openEditRoleDialog(Role role) {
    if (_permissionsViewModel.permissions.isEmpty) {
      _showSnackBar('Cargando permisos, intenta nuevamente...');
      return;
    }

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) => _RoleFormSheet(
        permissionsViewModel: _permissionsViewModel,
        roleToEdit: role,
        onSubmit: (name, desc, perms) async {
          await _viewModel.updateRole(
            UpdateRoleParams(
              id: role.id,
              name: name,
              description: desc,
              permissionCodes: perms,
            ),
          );
          if (mounted && _viewModel.errorMessage == null) {
            Navigator.of(context).pop();
            _showSnackBar('Rol actualizado exitosamente', isError: false);
          }
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Roles del sistema'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _openCreateRoleDialog,
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Nuevo Rol'),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_viewModel.errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 64, color: Colors.red.shade300),
            const SizedBox(height: 16),
            Text(
              _viewModel.errorMessage!,
              style: const TextStyle(fontSize: 16),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => _viewModel.loadRoles(),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
              ),
              child: const Text('Reintentar'),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () => context.go('/login'),
              child: const Text('Ir a inicio de sesión'),
            ),
          ],
        ),
      );
    }

    if (_viewModel.roles.isEmpty) {
      return const Center(child: Text('No hay roles disponibles'));
    }

    return Stack(
      children: [
        ListView(
          padding: const EdgeInsets.fromLTRB(8, 8, 8, 80),
          children: _viewModel.roles.map((role) {
            return Card(
              margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              // ✅ ExpansionTile para hacer el rol desplegable
              child: Theme(
                data: Theme.of(
                  context,
                ).copyWith(dividerColor: Colors.transparent),
                child: ExpansionTile(
                  leading: CircleAvatar(
                    backgroundColor: role.active
                        ? const Color(0xFFC8E6C9)
                        : Colors.grey[300],
                    child: Icon(
                      Icons.security,
                      color: role.active
                          ? const Color(0xFF2E7D32)
                          : Colors.grey,
                    ),
                  ),
                  title: Text(
                    role.name,
                    style: const TextStyle(fontWeight: FontWeight.w600),
                  ),
                  subtitle: Text(
                    role.description,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 10,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: role.active
                              ? Colors.green[100]
                              : Colors.red[100],
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          role.active ? 'Activo' : 'Inactivo',
                          style: TextStyle(
                            color: role.active
                                ? Colors.green[700]
                                : Colors.red[700],
                            fontSize: 12,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      PopupMenuButton<String>(
                        onSelected: (value) {
                          if (value == 'edit') {
                            _openEditRoleDialog(role);
                          } else if (value == 'activate') {
                            _viewModel.activateRole(role.id);
                          } else if (value == 'deactivate') {
                            _viewModel.deactivateRole(role.id);
                          }
                        },
                        itemBuilder: (context) => [
                          const PopupMenuItem(
                            value: 'edit',
                            child: Row(
                              children: [
                                Icon(Icons.edit, size: 18),
                                SizedBox(width: 8),
                                Text('Editar'),
                              ],
                            ),
                          ),
                          if (!role.active)
                            const PopupMenuItem(
                              value: 'activate',
                              child: Row(
                                children: [
                                  Icon(
                                    Icons.check_circle,
                                    size: 18,
                                    color: Colors.green,
                                  ),
                                  SizedBox(width: 8),
                                  Text('Activar'),
                                ],
                              ),
                            ),
                          if (role.active)
                            const PopupMenuItem(
                              value: 'deactivate',
                              child: Row(
                                children: [
                                  Icon(
                                    Icons.block,
                                    size: 18,
                                    color: Colors.red,
                                  ),
                                  SizedBox(width: 8),
                                  Text('Desactivar'),
                                ],
                              ),
                            ),
                        ],
                      ),
                    ],
                  ),
                  children: [
                    // ✅ Contenido expandido del rol
                    Padding(
                      padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Divider(),
                          const SizedBox(height: 8),
                          // Descripción completa
                          if (role.description.isNotEmpty)
                            Padding(
                              padding: const EdgeInsets.only(bottom: 12),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text(
                                    'Descripción',
                                    style: TextStyle(
                                      fontSize: 12,
                                      fontWeight: FontWeight.w600,
                                      color: Colors.grey,
                                    ),
                                  ),
                                  const SizedBox(height: 4),
                                  Text(
                                    role.description,
                                    style: const TextStyle(fontSize: 14),
                                  ),
                                ],
                              ),
                            ),
                          // Permisos
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                'Permisos asociados',
                                style: TextStyle(
                                  fontSize: 12,
                                  fontWeight: FontWeight.w600,
                                  color: Colors.grey,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Wrap(
                                spacing: 6,
                                runSpacing: 6,
                                children: role.permissionCodes.map((code) {
                                  return Container(
                                    padding: const EdgeInsets.symmetric(
                                      horizontal: 10,
                                      vertical: 4,
                                    ),
                                    decoration: BoxDecoration(
                                      color: const Color(0xFFE8F5E9),
                                      borderRadius: BorderRadius.circular(16),
                                      border: Border.all(
                                        color: const Color(0xFFC8E6C9),
                                      ),
                                    ),
                                    child: Text(
                                      code,
                                      style: const TextStyle(
                                        fontSize: 11,
                                        color: Color(0xFF2E7D32),
                                      ),
                                    ),
                                  );
                                }).toList(),
                              ),
                              if (role.permissionCodes.isEmpty)
                                const Padding(
                                  padding: EdgeInsets.only(top: 4),
                                  child: Text(
                                    'No tiene permisos asignados',
                                    style: TextStyle(
                                      fontSize: 12,
                                      color: Colors.grey,
                                    ),
                                  ),
                                ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          // Información adicional
                          Row(
                            children: [
                              Icon(
                                Icons.schedule,
                                size: 12,
                                color: Colors.grey.shade500,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                'Creado: ${_formatDate(role.createdAt)}',
                                style: TextStyle(
                                  fontSize: 11,
                                  color: Colors.grey.shade500,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),
        ),
        // Overlay de carga
        if (_viewModel.creating || _viewModel.toggling)
          Container(
            color: Colors.black26,
            child: const Center(child: CircularProgressIndicator()),
          ),
      ],
    );
  }

  String _formatDate(DateTime? date) {
    if (date == null) return 'Fecha no disponible';
    return '${date.day}/${date.month}/${date.year}';
  }
}

// ─────────────────────────────────────────────
// Role Form bottom sheet (Create/Edit)
// ─────────────────────────────────────────────
class _RoleFormSheet extends StatefulWidget {
  final PermissionsViewModel permissionsViewModel;
  final Role? roleToEdit;
  final Future<void> Function(
    String name,
    String description,
    List<String> permissions,
  )
  onSubmit;

  const _RoleFormSheet({
    required this.permissionsViewModel,
    this.roleToEdit,
    required this.onSubmit,
  });

  @override
  State<_RoleFormSheet> createState() => _RoleFormSheetState();
}

class _RoleFormSheetState extends State<_RoleFormSheet> {
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

    // Agrupar permisos por módulo
    final Map<String, List<dynamic>> grouped = {};
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
              // Handle bar
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Text(
                widget.roleToEdit != null ? 'Editar rol' : 'Crear nuevo rol',
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF2E7D32),
                ),
              ),
              const SizedBox(height: 20),

              // Name field
              TextFormField(
                controller: _nameController,
                decoration: InputDecoration(
                  labelText: 'Nombre del rol',
                  prefixIcon: const Icon(Icons.badge_outlined),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                validator: (v) =>
                    (v == null || v.trim().isEmpty) ? 'Campo requerido' : null,
              ),
              const SizedBox(height: 16),

              // Description field
              TextFormField(
                controller: _descController,
                decoration: InputDecoration(
                  labelText: 'Descripción',
                  prefixIcon: const Icon(Icons.notes_outlined),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                validator: (v) =>
                    (v == null || v.trim().isEmpty) ? 'Campo requerido' : null,
              ),
              const SizedBox(height: 20),

              // Permissions section
              Row(
                children: [
                  const Icon(
                    Icons.vpn_key_outlined,
                    size: 18,
                    color: Color(0xFF2E7D32),
                  ),
                  const SizedBox(width: 6),
                  Text(
                    'Permisos (${_selectedCodes.length} seleccionados)',
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),

              // Permissions list
              Expanded(
                child: loadingPerms
                    ? const Center(child: CircularProgressIndicator())
                    : permissions.isEmpty
                    ? const Text('No se pudieron cargar los permisos')
                    : SingleChildScrollView(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: grouped.entries.map((entry) {
                            final module = entry.key;
                            final perms = entry.value;
                            return Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Padding(
                                  padding: const EdgeInsets.only(
                                    top: 8,
                                    bottom: 2,
                                  ),
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
                                ...perms.map((p) {
                                  final selected = _selectedCodes.contains(
                                    p.code,
                                  );
                                  return CheckboxListTile(
                                    dense: true,
                                    contentPadding: EdgeInsets.zero,
                                    activeColor: const Color(0xFF2E7D32),
                                    value: selected,
                                    title: Text(
                                      p.code,
                                      style: const TextStyle(fontSize: 13),
                                    ),
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
                        ),
                      ),
              ),
              const SizedBox(height: 20),

              // Submit button
              SizedBox(
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
                        : (widget.roleToEdit != null
                              ? 'Actualizar Rol'
                              : 'Crear Rol'),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
