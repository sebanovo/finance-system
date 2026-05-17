import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
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
    } else {
      setState(() {});
    }
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
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

  // Validaciones reutilizables
  String? _validateEmail(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El correo electrónico es obligatorio';
    }
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,}$');
    if (!emailRegex.hasMatch(value.trim())) {
      return 'Ingresa un correo válido';
    }
    return null;
  }

  String? _validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'La contraseña es obligatoria';
    }
    if (value.length < 8) {
      return 'Debe tener al menos 8 caracteres';
    }
    return null;
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
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        content: Form(
          key: formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  controller: emailController,
                  decoration: const InputDecoration(labelText: 'Email'),
                  validator: _validateEmail,
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: passwordController,
                  decoration: const InputDecoration(labelText: 'Contraseña'),
                  obscureText: true,
                  validator: _validatePassword,
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
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
            ),
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
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            content: DropdownButtonFormField<String>(
              decoration: const InputDecoration(labelText: 'Selecciona un rol'),
              initialValue: selectedRoleId,
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
                    await _assignRole(userId, selectedRoleId!);
                  } else {
                    _showSnackBar('Selecciona un rol');
                  }
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2E7D32),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(30),
                  ),
                ),
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
        appBar: AppBar(
          title: const Text('Usuarios del Tenant'),
          backgroundColor: Colors.white,
          elevation: 0,
          foregroundColor: const Color(0xFF2E7D32),
        ),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Usuarios del Tenant'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: _viewModel.loading
          ? const Center(child: CircularProgressIndicator())
          : _viewModel.errorMessage != null
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.error_outline,
                    size: 64,
                    color: Colors.red.shade300,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    _viewModel.errorMessage!,
                    style: const TextStyle(fontSize: 16),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),
                  ElevatedButton(
                    onPressed: () => _viewModel.loadUsers(),
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
            )
          : _viewModel.users.isEmpty
          ? const Center(child: Text('No hay usuarios registrados'))
          : ListView.builder(
              padding: const EdgeInsets.all(8),
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
                    horizontal: 16,
                    vertical: 6,
                  ),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: user.active
                          ? const Color(0xFFC8E6C9)
                          : Colors.grey[300],
                      child: Icon(
                        Icons.person,
                        color: user.active
                            ? const Color(0xFF2E7D32)
                            : Colors.grey,
                      ),
                    ),
                    title: Text(
                      displayName,
                      style: const TextStyle(fontWeight: FontWeight.w600),
                    ),
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
                            backgroundColor: const Color(0xFFE8F5E9),
                            labelStyle: const TextStyle(
                              fontSize: 11,
                              color: Color(0xFF2E7D32),
                            ),
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
                          icon: const Icon(
                            Icons.edit,
                            size: 20,
                            color: Color(0xFF4CAF50),
                          ),
                          onPressed: () =>
                              _showEditRoleDialog(user.id, currentRoleId),
                          tooltip: 'Editar rol',
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 10,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: user.active
                                ? Colors.green[100]
                                : Colors.red[100],
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Text(
                            user.active ? 'Activo' : 'Inactivo',
                            style: TextStyle(
                              color: user.active
                                  ? Colors.green[700]
                                  : Colors.red[700],
                              fontSize: 12,
                              fontWeight: FontWeight.w500,
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
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
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
