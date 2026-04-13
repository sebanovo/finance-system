import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UsuariosPage extends StatefulWidget {
  const UsuariosPage({super.key});

  @override
  State<UsuariosPage> createState() => _UsuariosPageState();
}

class _UsuariosPageState extends State<UsuariosPage> {
  List usuarios = [];
  Map<String, List<dynamic>> rolesMap = {}; // userId -> lista de roles
  Map<String, bool> loadingRolesMap = {}; // userId -> si está cargando
  List<dynamic> availableRoles = []; // roles disponibles para asignar
  bool loadingRolesList = true; // carga de roles disponibles
  bool loading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    fetchAvailableRoles(); // primero obtener roles disponibles
  }

  Future<void> fetchAvailableRoles() async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');

    if (accessToken == null || tenantSlug == null) {
      setState(() {
        errorMessage = 'Sesión no válida';
        loading = false;
        loadingRolesList = false;
      });
      return;
    }

    final url = Uri.parse('${Env.baseUrl}/api/access/roles'); // ✅ URL corregida
    try {
      final response = await http.get(
        url,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'X-Tenant-Slug': tenantSlug,
          'Accept': '*/*',
        },
      );

      if (response.statusCode == 200) {
        final decoded = jsonDecode(response.body);
        if (decoded['success'] == true) {
          setState(() {
            availableRoles = decoded['data'] ?? []; // ✅ data es lista directa
            loadingRolesList = false;
          });
          fetchUsuarios();
        } else {
          setState(() {
            errorMessage = decoded['message'] ?? 'Error al cargar roles';
            loadingRolesList = false;
            loading = false;
          });
        }
      } else {
        setState(() {
          errorMessage = 'Error ${response.statusCode} al cargar roles';
          loadingRolesList = false;
          loading = false;
        });
      }
    } catch (e) {
      setState(() {
        errorMessage = 'Error de conexión: $e';
        loadingRolesList = false;
        loading = false;
      });
    }
  }

  Future<void> fetchUsuarios() async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    if (accessToken == null) {
      setState(() {
        errorMessage = 'No hay sesión activa. Inicia sesión nuevamente.';
        loading = false;
      });
      return;
    }

    final tenantSlug = prefs.getString('tenantSlug');
    if (tenantSlug == null) {
      setState(() {
        errorMessage = 'No se encontró el tenant. Reinicia la app.';
        loading = false;
      });
      return;
    }

    final url = Uri.parse('${Env.baseUrl}/api/users');

    try {
      final response = await http.get(
        url,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'X-Tenant-Slug': tenantSlug,
          'Accept': '*/*',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> decoded = jsonDecode(response.body);
        if (decoded['success'] == true) {
          final List<dynamic> data = decoded['data'] ?? [];
          setState(() {
            usuarios = data;
            loading = false;
            errorMessage = null;
          });
          // Cargar roles para cada usuario
          for (var user in data) {
            final userId = user['id'];
            if (userId != null && !rolesMap.containsKey(userId)) {
              fetchRoles(userId, accessToken, tenantSlug);
            }
          }
        } else {
          setState(() {
            errorMessage = decoded['message'] ?? 'Error al obtener usuarios';
            loading = false;
          });
        }
      } else if (response.statusCode == 401) {
        setState(() {
          errorMessage = 'Sesión expirada. Vuelve a iniciar sesión.';
          loading = false;
        });
        Future.delayed(const Duration(seconds: 2), () {
          Navigator.pushReplacementNamed(context, '/login');
        });
      } else {
        setState(() {
          errorMessage = 'Error ${response.statusCode}: ${response.body}';
          loading = false;
        });
      }
    } catch (e) {
      setState(() {
        errorMessage = 'Error de conexión: $e';
        loading = false;
      });
    }
  }

  Future<void> fetchRoles(
    String userId,
    String accessToken,
    String tenantSlug,
  ) async {
    setState(() {
      loadingRolesMap[userId] = true;
    });

    final url = Uri.parse('${Env.baseUrl}/api/access/users/$userId/roles');
    try {
      final response = await http.get(
        url,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'X-Tenant-Slug': tenantSlug,
          'Accept': '*/*',
        },
      );

      if (response.statusCode == 200) {
        final decoded = jsonDecode(response.body);
        if (decoded['success'] == true) {
          final List<dynamic> roles = decoded['data']['roles'] ?? [];
          setState(() {
            rolesMap[userId] = roles;
          });
        } else {
          setState(() {
            rolesMap[userId] = [];
          });
        }
      } else {
        setState(() {
          rolesMap[userId] = [];
        });
      }
    } catch (e) {
      setState(() {
        rolesMap[userId] = [];
      });
    } finally {
      setState(() {
        loadingRolesMap[userId] = false;
      });
    }
  }

  // Asignar rol a un usuario (reemplaza los roles existentes)
  Future<void> assignRole(String userId, String roleId) async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');

    if (accessToken == null || tenantSlug == null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Sesión no válida')));
      return;
    }

    final url = Uri.parse('${Env.baseUrl}/api/access/users/$userId/roles');
    try {
      final response = await http.put(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $accessToken',
          'X-Tenant-Slug': tenantSlug,
          'Accept': '*/*',
        },
        body: jsonEncode({
          'roleIds': [roleId],
        }),
      );

      if (response.statusCode == 200) {
        final decoded = jsonDecode(response.body);
        if (decoded['success'] == true) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Rol asignado correctamente')),
          );
          // Recargar roles de este usuario
          await fetchRoles(userId, accessToken, tenantSlug);
        } else {
          throw Exception(decoded['message'] ?? 'Error al asignar rol');
        }
      } else {
        final errorBody = jsonDecode(response.body);
        throw Exception(errorBody['message'] ?? 'Error ${response.statusCode}');
      }
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Error: $e')));
    }
  }

  Future<void> crearUsuario({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');

    if (accessToken == null || tenantSlug == null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Sesión no válida')));
      return;
    }

    final url = Uri.parse('${Env.baseUrl}/api/users');
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
        'X-Tenant-Slug': tenantSlug,
        'Accept': '*/*',
      },
      body: jsonEncode({
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final Map<String, dynamic> decoded = jsonDecode(response.body);
      if (decoded['success'] == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Usuario creado exitosamente')),
        );
        // Refrescar la lista (reinicia rolesMap también)
        setState(() {
          rolesMap.clear();
          loadingRolesMap.clear();
        });
        await fetchUsuarios();
      } else {
        throw Exception(decoded['message'] ?? 'Error al crear usuario');
      }
    } else {
      final errorBody = jsonDecode(response.body);
      throw Exception(errorBody['message'] ?? 'Error ${response.statusCode}');
    }
  }

  void _mostrarDialogoCrearUsuario() {
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
                TextFormField(
                  controller: passwordController,
                  decoration: const InputDecoration(labelText: 'Contraseña'),
                  obscureText: true,
                  validator: (value) =>
                      value!.length >= 6 ? null : 'Mínimo 6 caracteres',
                ),
                TextFormField(
                  controller: firstNameController,
                  decoration: const InputDecoration(labelText: 'Nombre'),
                ),
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
                Navigator.pop(context); // Cerrar diálogo
                try {
                  await crearUsuario(
                    email: emailController.text.trim(),
                    password: passwordController.text,
                    firstName: firstNameController.text.trim(),
                    lastName: lastNameController.text.trim(),
                  );
                } catch (e) {
                  ScaffoldMessenger.of(
                    context,
                  ).showSnackBar(SnackBar(content: Text('Error: $e')));
                }
              }
            },
            child: const Text('Crear'),
          ),
        ],
      ),
    );
  }

  void _mostrarDialogoEditarRol(String userId, String currentRoleId) {
    String? selectedRoleId = currentRoleId.isNotEmpty ? currentRoleId : null;
    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setStateDialog) {
          return AlertDialog(
            title: const Text('Asignar Rol'),
            content: DropdownButtonFormField<String>(
              decoration: const InputDecoration(labelText: 'Selecciona un rol'),
              value: selectedRoleId,
              items: availableRoles.map<DropdownMenuItem<String>>((role) {
                // ✅ tipo explícito
                return DropdownMenuItem<String>(
                  value: role['id'] as String?,
                  child: Text(role['name'] ?? 'Sin nombre'),
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
                    await assignRole(userId, selectedRoleId!);
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Selecciona un rol')),
                    );
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
    if (loadingRolesList) {
      return Scaffold(
        appBar: AppBar(title: Text("Usuarios del Tenant")),
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text("Usuarios del Tenant")),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : errorMessage != null
          ? Center(child: Text(errorMessage!))
          : usuarios.isEmpty
          ? const Center(child: Text("No hay usuarios registrados"))
          : ListView.builder(
              itemCount: usuarios.length,
              itemBuilder: (context, index) {
                final user = usuarios[index];
                final userId = user['id'];
                final fullName =
                    '${user['firstName'] ?? ''} ${user['lastName'] ?? ''}'
                        .trim();
                final displayName = fullName.isNotEmpty
                    ? fullName
                    : user['email'] ?? 'Sin nombre';
                final email = user['email'] ?? 'Sin correo';
                final isActive = user['active'] ?? false;
                final roles = rolesMap[userId] ?? [];
                final loadingRoles = loadingRolesMap[userId] ?? false;
                final currentRoleId = roles.isNotEmpty
                    ? roles.first['id']
                    : null;

                return Card(
                  margin: const EdgeInsets.symmetric(
                    horizontal: 8,
                    vertical: 4,
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: isActive
                          ? Colors.green[100]
                          : Colors.grey[300],
                      child: Icon(
                        Icons.person,
                        color: isActive ? Colors.green[700] : Colors.grey,
                      ),
                    ),
                    title: Text(displayName),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(email, style: const TextStyle(fontSize: 12)),
                        const SizedBox(height: 6),
                        if (loadingRoles)
                          const SizedBox(
                            height: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        else if (roles.isNotEmpty)
                          Chip(
                            label: Text(roles.first['name'] ?? 'Rol'),
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
                          onPressed: () {
                            _mostrarDialogoEditarRol(
                              userId,
                              currentRoleId ?? '',
                            );
                          },
                          tooltip: 'Editar rol',
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: isActive
                                ? Colors.green[100]
                                : Colors.red[100],
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            isActive ? 'Activo' : 'Inactivo',
                            style: TextStyle(
                              color: isActive
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
        onPressed: _mostrarDialogoCrearUsuario,
        child: const Icon(Icons.add),
      ),
    );
  }
}
