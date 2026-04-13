import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';

class ResetPasswordPage extends StatefulWidget {
  final String? initialTenant;
  final String? token;

  const ResetPasswordPage({super.key, this.initialTenant, this.token});

  @override
  State<ResetPasswordPage> createState() => _ResetPasswordPageState();
}

class _ResetPasswordPageState extends State<ResetPasswordPage> {
  final tenantController = TextEditingController();
  final tokenController = TextEditingController();
  final newPasswordController = TextEditingController();
  final confirmPasswordController = TextEditingController();
  bool isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.initialTenant != null) {
      tenantController.text = widget.initialTenant!;
    }
    if (widget.token != null) {
      tokenController.text = widget.token!;
    }
  }

  Future<void> resetPassword() async {
    final tenant = tenantController.text.trim();
    final token = tokenController.text.trim();
    final newPassword = newPasswordController.text.trim();
    final confirmPassword = confirmPasswordController.text.trim();

    if (tenant.isEmpty || token.isEmpty || newPassword.isEmpty) {
      _showSnackBar('Completa todos los campos');
      return;
    }
    if (newPassword != confirmPassword) {
      _showSnackBar('Las contraseñas no coinciden');
      return;
    }
    if (newPassword.length < 6) {
      _showSnackBar('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setState(() => isLoading = true);

    final url = Uri.parse('${Env.baseUrl}/api/auth/reset-password');
    try {
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Accept': '*/*',
          'X-Tenant-Slug': tenant,
        },
        body: jsonEncode({'newPassword': newPassword, 'token': token}),
      );

      final decoded = jsonDecode(response.body);
      if (response.statusCode == 200 && decoded['success'] == true) {
        _showSnackBar(
          'Contraseña restablecida. Inicia sesión.',
          isError: false,
        );
        Future.delayed(const Duration(seconds: 2), () {
          if (mounted) Navigator.pushReplacementNamed(context, '/login');
        });
      } else {
        _showSnackBar(decoded['message'] ?? 'Error al restablecer');
      }
    } catch (e) {
      _showSnackBar('Error de conexión: $e');
    } finally {
      if (mounted) setState(() => isLoading = false);
    }
  }

  void _showSnackBar(String msg, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
        backgroundColor: isError ? Colors.red : Colors.green,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Restablecer contraseña')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Ingresa el token que recibiste por correo y tu nueva contraseña.',
            ),
            const SizedBox(height: 16),
            TextField(
              controller: tenantController,
              decoration: const InputDecoration(
                labelText: 'Slug del tenant',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: tokenController,
              decoration: const InputDecoration(
                labelText: 'Token de restablecimiento',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: newPasswordController,
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'Nueva contraseña',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: confirmPasswordController,
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'Confirmar contraseña',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: isLoading ? null : resetPassword,
                child: isLoading
                    ? const CircularProgressIndicator()
                    : const Text('Restablecer contraseña'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
