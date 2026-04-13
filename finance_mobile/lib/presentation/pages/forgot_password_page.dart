import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/forgot_password_viewmodel.dart';

class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({super.key});

  @override
  State<ForgotPasswordPage> createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final emailController = TextEditingController();
  final tenantController = TextEditingController();

  late ForgotPasswordViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<ForgotPasswordViewModel>();
    _viewModel.addListener(_onViewModelChanged);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.success) {
      _showSnackBar(
        'Correo enviado. Revisa tu bandeja de entrada.',
        isError: false,
      );
      Future.delayed(const Duration(seconds: 1), () {
        if (mounted) {
          // Navegar a ResetPasswordPage pasando el tenant como extra
          context.push('/reset-password', extra: tenantController.text.trim());
        }
      });
    }
  }

  Future<void> _sendResetEmail() async {
    final email = emailController.text.trim();
    final tenant = tenantController.text.trim();
    if (email.isEmpty || tenant.isEmpty) {
      _showSnackBar('Completa todos los campos');
      return;
    }
    await _viewModel.sendResetEmail(email, tenant);
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
  void dispose() {
    emailController.dispose();
    tenantController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
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
                onPressed: _viewModel.isLoading ? null : _sendResetEmail,
                child: _viewModel.isLoading
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
