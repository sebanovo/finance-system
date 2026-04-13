import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';
import 'package:shared_preferences/shared_preferences.dart';

class RolesPage extends StatefulWidget {
  const RolesPage({super.key});

  @override
  State<RolesPage> createState() => _RolesPageState();
}

class _RolesPageState extends State<RolesPage> {
  List roles = [];
  bool loading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    fetchRoles();
  }

  Future<void> fetchRoles() async {
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

    final url = Uri.parse('${Env.baseUrl}/api/access/roles');

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
            roles = data;
            loading = false;
            errorMessage = null;
          });
        } else {
          setState(() {
            errorMessage = decoded['message'] ?? 'Error al obtener roles';
            loading = false;
          });
        }
      } else if (response.statusCode == 401) {
        setState(() {
          errorMessage = 'Sesión expirada. Vuelve a iniciar sesión.';
          loading = false;
        });
        Future.delayed(const Duration(seconds: 2), () {
          context.go('/login');
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Roles del Tenant")),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : errorMessage != null
          ? Center(child: Text(errorMessage!))
          : roles.isEmpty
          ? const Center(child: Text("No hay roles registrados"))
          : ListView.builder(
              itemCount: roles.length,
              itemBuilder: (context, index) {
                final role = roles[index];
                final name = role['name'] ?? 'Sin nombre';
                final description = role['description'] ?? 'Sin descripción';
                final isActive = role['active'] ?? false;
                final permissionCount =
                    (role['permissionCodes'] as List?)?.length ?? 0;

                return Card(
                  margin: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 4,
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: isActive
                          ? Colors.blue[100]
                          : Colors.grey[300],
                      child: Icon(
                        Icons.security,
                        color: isActive ? Colors.blue[700] : Colors.grey,
                      ),
                    ),
                    title: Text(name),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(description),
                        const SizedBox(height: 4),
                        Text(
                          '$permissionCount permiso(s)',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.grey,
                          ),
                        ),
                      ],
                    ),
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
                    onTap: () {
                      // Aquí podrías navegar a detalles del rol si lo deseas
                    },
                  ),
                );
              },
            ),
    );
  }
}
