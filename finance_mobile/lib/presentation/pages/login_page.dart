import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/login_viewmodel.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final tenantController = TextEditingController();

  late LoginViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<LoginViewModel>();
    _viewModel.addListener(_onViewModelChanged);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
  }

  Future<void> _login() async {
    final tenant = tenantController.text.trim();
    if (tenant.isEmpty) {
      _showSnackBar('Por favor ingresa el nombre del tenant');
      return;
    }
    final success = await _viewModel.login(
      emailController.text.trim(),
      passwordController.text,
      tenant,
    );
    if (success && mounted) {
      context.go('/home');
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
    _viewModel.removeListener(_onViewModelChanged);
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
                onPressed: _viewModel.isLoading ? null : _login,
                child: _viewModel.isLoading
                    ? const CircularProgressIndicator()
                    : const Text('Iniciar sesión'),
              ),
            ),
            TextButton(
              onPressed: () => context.push('/forgot-password'),
              child: const Text('¿Olvidaste tu contraseña?'),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () => context.push('/signup'),
              child: const Text('¿No tienes cuenta? Registra tu empresa'),
            ),
          ],
        ),
      ),
    );
  }
}
