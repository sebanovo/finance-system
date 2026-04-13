import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';
import 'presentation/pages/reset_password_page.dart';
// import 'reset_password_page.dart';

class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({super.key});

  @override
  State<ForgotPasswordPage> createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final emailController = TextEditingController();
  final tenantController = TextEditingController();
  bool isLoading = false;

  Future<void> sendResetEmail() async {
    final email = emailController.text.trim();
    final tenant = tenantController.text.trim();

    if (email.isEmpty || tenant.isEmpty) {
      _showSnackBar('Completa todos los campos');
      return;
    }

    setState(() => isLoading = true);

    final url = Uri.parse('${Env.baseUrl}/api/auth/forgot-password');
    try {
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Accept': '*/*',
          'X-Tenant-Slug': tenant,
        },
        body: jsonEncode({'email': email}),
      );

      final decoded = jsonDecode(response.body);
      if (response.statusCode == 200 && decoded['success'] == true) {
        _showSnackBar(
          'Correo enviado. Revisa tu bandeja de entrada.',
          isError: false,
        );
        // En lugar de volver atrás, ir a reset-password con tenant precargado
        Future.delayed(const Duration(seconds: 1), () {
          if (mounted) {
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(
                builder: (_) => ResetPasswordPage(initialTenant: tenant),
              ),
            );
          }
        });
      } else {
        _showSnackBar(decoded['message'] ?? 'Error al enviar el correo');
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
              'Para reajustar tu contraseña, ingresa tu correo electrónico y el slug de tu tenant.',
              style: TextStyle(fontSize: 14),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: tenantController,
              decoration: const InputDecoration(
                labelText: 'Slug del tenant (ej: miempresa)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: emailController,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                labelText: 'Correo electrónico',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: isLoading ? null : sendResetEmail,
                child: isLoading
                    ? const CircularProgressIndicator()
                    : const Text('Enviar instrucciones'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
