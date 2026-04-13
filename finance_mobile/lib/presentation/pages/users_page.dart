import 'package:flutter/material.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/users_viewmodel.dart';

class UsersPage extends StatefulWidget {
  const UsersPage({super.key});

  @override
  State<UsersPage> createState() => _UsersPageState();
}

class _UsersPageState extends State<UsersPage> {
  late UsersViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<UsersViewModel>();
    _viewModel.addListener(_onViewModelChanged);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    setState(() {});
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : Colors.green,
      ),
    );
  }

  Future<void> _createUser({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    try {
      await _viewModel.createUser(
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
      );
      _showSnackBar('Usuario creado exitosamente', isError: false);
    } catch (e) {
      _showSnackBar('Error: $e');
    }
  }

  Future<void> _assignRole(String userId, String roleId) async {
    try {
      await _viewModel.assignRole(userId, roleId);
      _showSnackBar('Rol asignado correctamente', isError: false);
    } catch (e) {
      _showSnackBar('Error: $e');
    }
  }

  void _showCreateUserDialog() {
    final emailController = TextEditingController();
    final passwordController = TextEditingController();
    final firstNameController = TextEditingController();
    final lastNameController = TextEditingController();
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Crear Usuario'),
        content: Form(
          key: formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: emailController,
                  decoration: const InputDecoration(labelText: 'Email'),
                  validator: (value) =>
                      value!.contains('@') ? null : 'Email inválido',
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: passwordController,
                  decoration: const InputDecoration(labelText: 'Contraseña'),
                  obscureText: true,
                  validator: (value) =>
                      value!.length >= 6 ? null : 'Mínimo 6 caracteres',
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: firstNameController,
                  decoration: const InputDecoration(labelText: 'Nombre'),
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: lastNameController,
                  decoration: const InputDecoration(labelText: 'Apellido'),
                ),
              ],
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () async {
              if (formKey.currentState!.validate()) {
                Navigator.pop(context);
                await _createUser(
                  email: emailController.text.trim(),
                  password: passwordController.text,
                  firstName: firstNameController.text.trim(),
                  lastName: lastNameController.text.trim(),
                );
              }
            },
            child: const Text('Crear'),
          ),
        ],
      ),
    );
  }

  void _showEditRoleDialog(String userId, String? currentRoleId) {
    String? selectedRoleId = currentRoleId;
    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setStateDialog) {
          return AlertDialog(
            title: const Text('Asignar Rol'),
            content: DropdownButtonFormField<String>(
              decoration: const InputDecoration(labelText: 'Selecciona un rol'),
              value: selectedRoleId,
              items: _viewModel.availableRoles.map((role) {
                return DropdownMenuItem<String>(
                  value: role.id,
                  child: Text(role.name),
                );
              }).toList(),
              onChanged: (value) {
                setStateDialog(() {
                  selectedRoleId = value;
                });
              },
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Cancelar'),
              ),
              ElevatedButton(
                onPressed: () async {
                  if (selectedRoleId != null) {
                    Navigator.pop(context);
                    await _assignRole(userId, selectedRoleId ?? '');
                  } else {
                    _showSnackBar('Selecciona un rol');
                  }
                },
                child: const Text('Guardar'),
              ),
            ],
          );
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_viewModel.loadingRolesList) {
      return Scaffold(
        appBar: AppBar(title: Text('Usuarios del Tenant')),
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Usuarios del Tenant')),
      body: _viewModel.loading
          ? const Center(child: CircularProgressIndicator())
          : _viewModel.errorMessage != null
          ? Center(child: Text(_viewModel.errorMessage!))
          : _viewModel.users.isEmpty
          ? const Center(child: Text('No hay usuarios registrados'))
          : ListView.builder(
              itemCount: _viewModel.users.length,
              itemBuilder: (context, index) {
                final user = _viewModel.users[index];
                final displayName = user.fullName.isNotEmpty
                    ? user.fullName
                    : user.email;
                final roles = _viewModel.rolesMap[user.id] ?? [];
                final loadingRoles =
                    _viewModel.loadingRolesMap[user.id] ?? false;
                final currentRoleId = roles.isNotEmpty ? roles.first.id : null;

                return Card(
                  margin: const EdgeInsets.symmetric(
                    horizontal: 8,
                    vertical: 4,
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: user.active
                          ? Colors.green[100]
                          : Colors.grey[300],
                      child: Icon(
                        Icons.person,
                        color: user.active ? Colors.green[700] : Colors.grey,
                      ),
                    ),
                    title: Text(displayName),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(user.email, style: const TextStyle(fontSize: 12)),
                        const SizedBox(height: 6),
                        if (loadingRoles)
                          const SizedBox(
                            height: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        else if (roles.isNotEmpty)
                          Chip(
                            label: Text(roles.first.name),
                            backgroundColor: Colors.blue[50],
                            labelStyle: const TextStyle(fontSize: 11),
                            materialTapTargetSize:
                                MaterialTapTargetSize.shrinkWrap,
                            padding: EdgeInsets.zero,
                          )
                        else
                          const Text(
                            'Sin rol asignado',
                            style: TextStyle(fontSize: 11, color: Colors.grey),
                          ),
                      ],
                    ),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.edit, size: 20),
                          onPressed: () =>
                              _showEditRoleDialog(user.id, currentRoleId),
                          tooltip: 'Editar rol',
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: user.active
                                ? Colors.green[100]
                                : Colors.red[100],
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            user.active ? 'Activo' : 'Inactivo',
                            style: TextStyle(
                              color: user.active
                                  ? Colors.green[700]
                                  : Colors.red[700],
                              fontSize: 12,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateUserDialog,
        child: const Icon(Icons.add),
      ),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
