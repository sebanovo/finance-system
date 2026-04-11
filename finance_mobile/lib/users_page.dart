// users_page.dart (versión con botón de agregar)
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
  bool loading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    fetchUsuarios();
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
        // Refrescar la lista
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Usuarios del Tenant"),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _mostrarDialogoCrearUsuario,
            tooltip: 'Agregar usuario',
          ),
        ],
      ),
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
                final fullName =
                    '${user['firstName'] ?? ''} ${user['lastName'] ?? ''}'
                        .trim();
                final displayName = fullName.isNotEmpty
                    ? fullName
                    : user['email'] ?? 'Sin nombre';
                final email = user['email'] ?? 'Sin correo';
                final isActive = user['active'] ?? false;

                return ListTile(
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
                  subtitle: Text(email),
                  trailing: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: isActive ? Colors.green[100] : Colors.red[100],
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      isActive ? 'Activo' : 'Inactivo',
                      style: TextStyle(
                        color: isActive ? Colors.green[700] : Colors.red[700],
                        fontSize: 12,
                      ),
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
