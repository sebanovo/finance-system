import 'dart:convert';
import 'package:finance_mobile/signup_page.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:finance_mobile/constants/env.dart';
import 'forgot_password.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:finance_mobile/core/network/api_client.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final tenantController = TextEditingController();

  bool isLoading = false;

  Future<void> login() async {
    final tenant = tenantController.text.trim();
    if (tenant.isEmpty) {
      _showSnackBar('Por favor ingresa el nombre del tenant');
      return;
    }

    setState(() => isLoading = true);

    try {
      final url = Uri.parse('${Env.baseUrl}/api/auth/login');

      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Accept': '*/*',
          'X-Tenant-Slug': tenant,
        },
        body: jsonEncode({
          'email': emailController.text.trim(),
          'password': passwordController.text,
        }),
      );

      if (response.statusCode == 200) {
        final jsonResponse = jsonDecode(response.body);
        if (jsonResponse['success'] == true) {
          final data = jsonResponse['data'];
          final prefs = await SharedPreferences.getInstance();
          await prefs.setString('accessToken', data['accessToken']);
          await prefs.setString('refreshToken', data['refreshToken']);
          await prefs.setString('tenantSlug', tenant);

          // ✅ Configurar ApiClient con el token y tenant
          final apiClient = di.sl<ApiClient>();
          apiClient.setToken(data['accessToken']);
          apiClient.setTenant(tenant);

          if (!mounted) return;
          context.go('/home');
        } else {
          _showSnackBar(jsonResponse['message'] ?? 'Error desconocido');
        }
      } else {
        _showSnackBar('Error de login: ${response.statusCode}');
      }
    } catch (e) {
      _showSnackBar('Error de conexión: $e');
    } finally {
      if (mounted) setState(() => isLoading = false);
    }
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  void dispose() {
    emailController.dispose();
    passwordController.dispose();
    tenantController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Login')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Image.asset("logo.png", width: 100),
            TextField(
              controller: tenantController,
              decoration: const InputDecoration(
                labelText: 'Nombre del tenant',
                hintText: 'Ej: miempresa',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: emailController,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                labelText: 'Correo electrónico',
                hintText: 'usuario@ejemplo.com',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: passwordController,
              decoration: const InputDecoration(
                labelText: 'Contraseña',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: isLoading ? null : login,
                child: isLoading
                    ? const CircularProgressIndicator()
                    : const Text('Iniciar sesión'),
              ),
            ),
            TextButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const ForgotPasswordPage()),
                );
              },
              child: const Text('¿Olvidaste tu contraseña?'),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const SignupPage()),
                );
              },
              child: const Text('¿No tienes cuenta? Registra tu empresa'),
            ),
          ],
        ),
      ),
    );
  }
}
