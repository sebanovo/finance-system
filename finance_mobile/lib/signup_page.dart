import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';

class SignupPage extends StatefulWidget {
  const SignupPage({super.key});

  @override
  State<SignupPage> createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  final companyNameController = TextEditingController();
  final tenantSlugController = TextEditingController();
  final adminEmailController = TextEditingController();
  final passwordController = TextEditingController();
  final firstNameController = TextEditingController();
  final lastNameController = TextEditingController();

  bool isLoading = false;

  Future<void> signup() async {
    // Validaciones básicas
    if (companyNameController.text.trim().isEmpty ||
        tenantSlugController.text.trim().isEmpty ||
        adminEmailController.text.trim().isEmpty ||
        passwordController.text.trim().isEmpty ||
        firstNameController.text.trim().isEmpty ||
        lastNameController.text.trim().isEmpty) {
      _showSnackBar('Por favor completa todos los campos');
      return;
    }

    setState(() => isLoading = true);

    try {
      final url = Uri.parse('${Env.baseUrl}/api/public/signup');

      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'companyName': companyNameController.text.trim(),
          'tenantSlug': tenantSlugController.text.trim(),
          'adminEmail': adminEmailController.text.trim(),
          'password': passwordController.text,
          'firstName': firstNameController.text.trim(),
          'lastName': lastNameController.text.trim(),
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final jsonResponse = jsonDecode(response.body);
        _showSnackBar(jsonResponse['message'] ?? 'Registro exitoso');
        // Opcional: volver a login después de 2 segundos
        Future.delayed(const Duration(seconds: 2), () {
          if (mounted) Navigator.pop(context);
        });
      } else {
        final error = jsonDecode(response.body);
        _showSnackBar(error['message'] ?? 'Error al registrar');
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
    companyNameController.dispose();
    tenantSlugController.dispose();
    adminEmailController.dispose();
    passwordController.dispose();
    firstNameController.dispose();
    lastNameController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Registro de nueva empresa')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: companyNameController,
              decoration: const InputDecoration(
                labelText: 'Nombre de la empresa',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: tenantSlugController,
              decoration: const InputDecoration(
                labelText: 'Slug del tenant (ej: miempresa2)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: adminEmailController,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                labelText: 'Correo del administrador',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: passwordController,
              obscureText: true,
              decoration: const InputDecoration(
                labelText: 'Contraseña',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: firstNameController,
              decoration: const InputDecoration(
                labelText: 'Nombre',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: lastNameController,
              decoration: const InputDecoration(
                labelText: 'Apellido',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: isLoading ? null : signup,
                child: isLoading
                    ? const CircularProgressIndicator()
                    : const Text('Registrar empresa'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
