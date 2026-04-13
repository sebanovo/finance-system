import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/reset_password_viewmodel.dart';

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

  late ResetPasswordViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<ResetPasswordViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    if (widget.initialTenant != null) {
      tenantController.text = widget.initialTenant!;
    }
    if (widget.token != null) {
      tokenController.text = widget.token!;
    }
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!, isError: true);
      _viewModel.clearError();
    }
  }

  Future<void> _resetPassword() async {
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

    final success = await _viewModel.resetPassword(tenant, token, newPassword);
    if (success && mounted) {
      _showSnackBar('Contraseña restablecida. Inicia sesión.', isError: false);
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.go('/login');
      });
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
  void dispose() {
    tenantController.dispose();
    tokenController.dispose();
    newPasswordController.dispose();
    confirmPasswordController.dispose();
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
                onPressed: _viewModel.isLoading ? null : _resetPassword,
                child: _viewModel.isLoading
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
