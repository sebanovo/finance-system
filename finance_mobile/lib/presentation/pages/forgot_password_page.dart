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
  final _formKey = GlobalKey<FormState>();
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
    // Redirigir al login si el error es de autenticación
    if (_viewModel.errorMessage != null &&
        (_viewModel.errorMessage!.contains('Sesión expirada') ||
            _viewModel.errorMessage!.contains('401') ||
            _viewModel.errorMessage!.contains('No hay sesión activa'))) {
      _showSnackBar(
        'Tu sesión ha expirado. Por favor inicia sesión nuevamente.',
      );
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.go('/login');
      });
    } else if (_viewModel.success) {
      _showSnackBar(
        'Correo enviado. Revisa tu bandeja de entrada.',
        isError: false,
      );
      Future.delayed(const Duration(seconds: 1), () {
        if (mounted) {
          context.push('/reset-password', extra: tenantController.text.trim());
        }
      });
    } else {
      setState(() {});
    }
  }

  void _showSnackBar(String msg, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  Future<void> _sendResetEmail() async {
    if (_formKey.currentState!.validate()) {
      await _viewModel.sendResetEmail(
        emailController.text.trim(),
        tenantController.text.trim(),
      );
    }
  }

  // Validaciones
  String? _validateTenant(String? value) {
    if (value == null || value.trim().isEmpty)
      return 'El slug del tenant es obligatorio';
    return null;
  }

  String? _validateEmail(String? value) {
    if (value == null || value.trim().isEmpty)
      return 'El correo electrónico es obligatorio';
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    if (!emailRegex.hasMatch(value.trim()))
      return 'Correo electrónico inválido';
    return null;
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
      appBar: AppBar(
        title: const Text('Restablecer contraseña'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: Container(
        color: Colors.white,
        child: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 24.0),
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Card(
                      elevation: 4,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(32),
                      ),
                      color: Colors.white,
                      child: Padding(
                        padding: const EdgeInsets.all(24.0),
                        child: Column(
                          children: [
                            _buildTextField(
                              controller: tenantController,
                              label: 'Slug del tenant',
                              hint: 'Ej: miempresa',
                              icon: Icons.business,
                              validator: _validateTenant,
                            ),
                            const SizedBox(height: 16),
                            _buildTextField(
                              controller: emailController,
                              label: 'Correo electrónico',
                              hint: 'usuario@ejemplo.com',
                              icon: Icons.email,
                              keyboardType: TextInputType.emailAddress,
                              validator: _validateEmail,
                            ),
                            const SizedBox(height: 28),
                            SizedBox(
                              width: double.infinity,
                              child: ElevatedButton(
                                onPressed: _viewModel.isLoading
                                    ? null
                                    : _sendResetEmail,
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: Colors.transparent,
                                  shadowColor: Colors.transparent,
                                  padding: const EdgeInsets.symmetric(
                                    vertical: 14,
                                  ),
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(40),
                                  ),
                                ),
                                child: Ink(
                                  decoration: BoxDecoration(
                                    gradient: const LinearGradient(
                                      colors: [
                                        Color(0xFF2E7D32),
                                        Color(0xFF66BB6A),
                                      ],
                                      begin: Alignment.centerLeft,
                                      end: Alignment.centerRight,
                                    ),
                                    borderRadius: BorderRadius.circular(40),
                                  ),
                                  child: Container(
                                    height: 40,
                                    alignment: Alignment.center,
                                    child: _viewModel.isLoading
                                        ? const CircularProgressIndicator(
                                            color: Colors.white,
                                          )
                                        : const Text(
                                            'Enviar instrucciones',
                                            style: TextStyle(
                                              fontSize: 16,
                                              fontWeight: FontWeight.bold,
                                              color: Colors.white,
                                            ),
                                          ),
                                  ),
                                ),
                              ),
                            ),
                            if (_viewModel.errorMessage != null &&
                                !_viewModel.success)
                              Padding(
                                padding: const EdgeInsets.only(top: 12),
                                child: Text(
                                  _viewModel.errorMessage!,
                                  style: TextStyle(color: Colors.red.shade700),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 20),
                    TextButton(
                      onPressed: () => context.go('/login'),
                      style: TextButton.styleFrom(
                        foregroundColor: const Color(0xFF2E7D32),
                      ),
                      child: const Text('Volver al inicio de sesión'),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String label,
    String? hint,
    required IconData icon,
    TextInputType keyboardType = TextInputType.text,
    required String? Function(String?) validator,
  }) {
    return TextFormField(
      controller: controller,
      keyboardType: keyboardType,
      validator: validator,
      decoration: InputDecoration(
        labelText: label,
        hintText: hint,
        prefixIcon: Icon(icon, color: const Color(0xFF4CAF50)),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(30),
          borderSide: BorderSide.none,
        ),
        filled: true,
        fillColor: Colors.grey.shade50,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 20,
          vertical: 16,
        ),
        labelStyle: TextStyle(color: Colors.grey.shade700),
        errorStyle: const TextStyle(color: Colors.red, fontSize: 12),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(30),
          borderSide: BorderSide(color: Colors.red.shade300, width: 1),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(30),
          borderSide: BorderSide(color: Colors.red.shade300, width: 1),
        ),
      ),
    );
  }
}
