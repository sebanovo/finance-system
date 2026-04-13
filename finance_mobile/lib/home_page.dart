import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:finance_mobile/constants/env.dart';
import 'users_page.dart';
import 'roles_page.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  Map<String, dynamic>? subscription;
  bool loading = true;
  String? errorMessage;
  Map<String, dynamic>? userInfo;
  bool loadingUser = true;

  @override
  void initState() {
    super.initState();
    fetchSubscription();
    fetchUserInfo();
  }

  // Método para obtener /api/secure/me
  Future<void> fetchUserInfo() async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');
    if (accessToken == null || tenantSlug == null) return;

    final url = Uri.parse('${Env.baseUrl}/api/secure/me');
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
            userInfo = decoded['data'];
            loadingUser = false;
          });
        }
      }
    } catch (e) {
      print('Error fetching user info: $e');
    }
  }

  Future<void> fetchSubscription() async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');

    if (accessToken == null || tenantSlug == null) {
      setState(() {
        errorMessage = 'Sesión no válida';
        loading = false;
      });
      return;
    }

    final url = Uri.parse('${Env.baseUrl}/api/subscription/current');
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
            subscription = decoded['data'];
            loading = false;
          });
        } else {
          setState(() {
            errorMessage = decoded['message'] ?? 'Error al obtener suscripción';
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

  Future<void> logout(BuildContext context) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    if (!context.mounted) return;
    Navigator.pushNamedAndRemoveUntil(context, '/login', (route) => false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Dashboard")),
      drawer: Drawer(
        child: ListView(
          children: [
            if (loadingUser)
              const DrawerHeader(
                decoration: BoxDecoration(color: Colors.blue),
                child: Center(
                  child: CircularProgressIndicator(color: Colors.white),
                ),
              )
            else if (userInfo != null)
              UserAccountsDrawerHeader(
                decoration: const BoxDecoration(color: Colors.blue),
                accountName: Text(
                  userInfo!['subject']?.split('@').first ?? 'Usuario',
                ),
                accountEmail: Text(userInfo!['subject'] ?? 'Sin email'),
                currentAccountPicture: CircleAvatar(
                  backgroundColor: Colors.white,
                  child: Text(
                    userInfo!['subject']?.substring(0, 1).toUpperCase() ?? 'U',
                    style: const TextStyle(fontSize: 24, color: Colors.blue),
                  ),
                ),
                otherAccountsPictures: [
                  Tooltip(
                    message:
                        'Roles: ${(userInfo!['roles'] as List).join(', ')}',
                    child: const Icon(Icons.verified_user, color: Colors.white),
                  ),
                ],
              ),
            ListTile(
              leading: const Icon(Icons.dashboard),
              title: const Text("Dashboard"),
              onTap: () {
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.school),
              title: const Text("Usuarios"),
              onTap: () {
                Navigator.pop(context);
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const UsuariosPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.security),
              title: const Text("Roles"),
              onTap: () {
                Navigator.pop(context);
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const RolesPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.lock_reset),
              title: const Text("Cambiar contraseña"),
              onTap: () {
                Navigator.pop(context); // cerrar drawer
                _mostrarDialogoCambioContrasena(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text("Cerrar sesión"),
              onTap: () => logout(context),
            ),
          ],
        ),
      ),
      body: RefreshIndicator(
        onRefresh: fetchSubscription,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Center(
                child: Text(
                  "Bienvenido al Dashboard",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
              ),
              const SizedBox(height: 16),
              Center(child: Image.asset("logo.png", width: 100)),
              const SizedBox(height: 24),
              if (loading)
                const Center(child: CircularProgressIndicator())
              else if (errorMessage != null)
                Card(
                  color: Colors.red[50],
                  child: Padding(
                    padding: const EdgeInsets.all(12.0),
                    child: Text(
                      errorMessage!,
                      style: const TextStyle(color: Colors.red),
                    ),
                  ),
                )
              else if (subscription != null)
                Card(
                  elevation: 4,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              subscription!['planName'] ?? 'Sin plan',
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 12,
                                vertical: 6,
                              ),
                              decoration: BoxDecoration(
                                color: subscription!['status'] == 'ACTIVE'
                                    ? Colors.green[100]
                                    : Colors.orange[100],
                                borderRadius: BorderRadius.circular(20),
                              ),
                              child: Text(
                                subscription!['status'] ?? 'DESCONOCIDO',
                                style: TextStyle(
                                  color: subscription!['status'] == 'ACTIVE'
                                      ? Colors.green[700]
                                      : Colors.orange[700],
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),
                        if (subscription!['trial'] == true)
                          Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: Colors.blue[50],
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Row(
                              children: [
                                const Icon(Icons.science, color: Colors.blue),
                                const SizedBox(width: 8),
                                Text(
                                  'Período de prueba · ${subscription!['remainingDays']} días restantes',
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        const SizedBox(height: 16),
                        _infoRow(
                          Icons.category,
                          'Tipo de plan',
                          subscription!['planType'] ?? '-',
                        ),
                        _infoRow(
                          Icons.people,
                          'Máx. usuarios',
                          '${subscription!['maxUsers']}',
                        ),
                        _infoRow(
                          Icons.security,
                          'Máx. roles',
                          '${subscription!['maxRoles']}',
                        ),
                        _infoRow(
                          Icons.calendar_today,
                          'Inicio',
                          _formatDate(subscription!['startedAt']),
                        ),
                        _infoRow(
                          Icons.event_busy,
                          'Expira',
                          _formatDate(subscription!['expiresAt']),
                        ),
                        if (subscription!['remainingDays'] != null &&
                            subscription!['remainingDays'] < 0)
                          Padding(
                            padding: const EdgeInsets.only(top: 8),
                            child: Text(
                              'Suscripción vencida',
                              style: TextStyle(color: Colors.red[700]),
                            ),
                          ),
                      ],
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  // Método para mostrar el diálogo
  void _mostrarDialogoCambioContrasena(BuildContext context) {
    final currentPasswordController = TextEditingController();
    final newPasswordController = TextEditingController();
    final confirmPasswordController = TextEditingController();
    final formKey = GlobalKey<FormState>();
    bool isLoading = false;

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => StatefulBuilder(
        builder: (dialogContext, setStateDialog) {
          return AlertDialog(
            title: const Text('Cambiar contraseña'),
            content: Form(
              key: formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextFormField(
                    controller: currentPasswordController,
                    obscureText: true,
                    decoration: const InputDecoration(
                      labelText: 'Contraseña actual',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) => value == null || value.isEmpty
                        ? 'Campo obligatorio'
                        : null,
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: newPasswordController,
                    obscureText: true,
                    decoration: const InputDecoration(
                      labelText: 'Nueva contraseña',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Campo obligatorio';
                      }
                      if (value.length < 6) {
                        return 'Mínimo 6 caracteres';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 12),
                  TextFormField(
                    controller: confirmPasswordController,
                    obscureText: true,
                    decoration: const InputDecoration(
                      labelText: 'Confirmar nueva contraseña',
                      border: OutlineInputBorder(),
                    ),
                    validator: (value) {
                      if (value != newPasswordController.text) {
                        return 'Las contraseñas no coinciden';
                      }
                      return null;
                    },
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(dialogContext),
                child: const Text('Cancelar'),
              ),
              ElevatedButton(
                onPressed: isLoading
                    ? null
                    : () async {
                        if (formKey.currentState!.validate()) {
                          setStateDialog(() => isLoading = true);
                          try {
                            await _cambiarContrasena(
                              currentPasswordController.text,
                              newPasswordController.text,
                            );
                            if (dialogContext.mounted) {
                              Navigator.pop(dialogContext);
                              ScaffoldMessenger.of(dialogContext).showSnackBar(
                                const SnackBar(
                                  content: Text(
                                    'Contraseña cambiada exitosamente',
                                  ),
                                  backgroundColor: Colors.green,
                                ),
                              );
                            }
                          } catch (e) {
                            if (dialogContext.mounted) {
                              ScaffoldMessenger.of(dialogContext).showSnackBar(
                                SnackBar(content: Text('Error: $e')),
                              );
                            }
                          } finally {
                            setStateDialog(() => isLoading = false);
                          }
                        }
                      },
                child: isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Cambiar'),
              ),
            ],
          );
        },
      ),
    );
  }

  // Método para llamar al endpoint de cambio de contraseña
  Future<void> _cambiarContrasena(
    String currentPassword,
    String newPassword,
  ) async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString('accessToken');
    final tenantSlug = prefs.getString('tenantSlug');

    if (accessToken == null || tenantSlug == null) {
      throw Exception('Sesión no válida');
    }

    final url = Uri.parse('${Env.baseUrl}/api/auth/change-password');
    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
        'X-Tenant-Slug': tenantSlug,
        'Accept': '*/*',
      },
      body: jsonEncode({
        'currentPassword': currentPassword,
        'newPassword': newPassword,
      }),
    );

    if (response.statusCode == 200) {
      final decoded = jsonDecode(response.body);
      if (decoded['success'] != true) {
        throw Exception(decoded['message'] ?? 'Error al cambiar contraseña');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada. Vuelve a iniciar sesión.');
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Error ${response.statusCode}');
    }
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 18, color: Colors.grey[600]),
          const SizedBox(width: 12),
          SizedBox(
            width: 100,
            child: Text(label, style: TextStyle(color: Colors.grey[700])),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(String? isoString) {
    if (isoString == null) return '-';
    try {
      final date = DateTime.parse(isoString).toLocal();
      return '${date.day}/${date.month}/${date.year}';
    } catch (e) {
      return isoString;
    }
  }
}
