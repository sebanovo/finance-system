import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/signup_viewmodel.dart';

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
  final _formKey = GlobalKey<FormState>();

  late SignupViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<SignupViewModel>();
    _viewModel.addListener(_onViewModelChanged);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      setState(() {});
    }
    if (_viewModel.success) {
      _showSnackBar(
        'Registro exitoso. Redirigiendo al login...',
        isError: false,
      );
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.go('/login');
      });
    }
  }

  Future<void> _signup() async {
    if (!_formKey.currentState!.validate()) return;
    await _viewModel.signup(
      companyName: companyNameController.text.trim(),
      tenantSlug: tenantSlugController.text.trim(),
      adminEmail: adminEmailController.text.trim(),
      password: passwordController.text,
      firstName: firstNameController.text.trim(),
      lastName: lastNameController.text.trim(),
    );
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : Colors.green,
      ),
    );
  }

  @override
  void dispose() {
    companyNameController.dispose();
    tenantSlugController.dispose();
    adminEmailController.dispose();
    passwordController.dispose();
    firstNameController.dispose();
    lastNameController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  // Validaciones
  String? _validateCompanyName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El nombre de la empresa es obligatorio';
    }
    if (value.trim().length < 3) {
      return 'Debe tener al menos 3 caracteres';
    }
    return null;
  }

  String? _validateTenantSlug(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El slug del tenant es obligatorio';
    }
    final trimmed = value.trim().toLowerCase();
    final regex = RegExp(r'^[a-z0-9]+(-[a-z0-9]+)*$');
    if (!regex.hasMatch(trimmed)) {
      return 'Solo letras minúsculas, números y guiones medios (-)';
    }
    return null;
  }

  String? _validateEmail(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El correo electrónico es obligatorio';
    }
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    if (!emailRegex.hasMatch(value.trim())) {
      return 'Ingresa un correo válido';
    }
    return null;
  }

  String? _validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'La contraseña es obligatoria';
    }
    if (value.length < 8) {
      return 'Debe tener al menos 8 caracteres';
    }
    if (!value.contains(RegExp(r'[0-9]'))) {
      return 'Debe contener al menos un dígito';
    }
    if (!value.contains(RegExp(r'[!@#$%^&*(),.?":{}|<>]'))) {
      return 'Debe contener al menos un carácter especial';
    }
    return null;
  }

  String? _validateFirstName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El nombre es obligatorio';
    }
    return null;
  }

  String? _validateLastName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'El apellido es obligatorio';
    }
    return null;
  }

  InputDecoration _buildInputDecoration({
    required String label,
    String? hint,
    required IconData icon,
  }) {
    return InputDecoration(
      labelText: label,
      hintText: hint,
      prefixIcon: Icon(icon, color: const Color(0xFF4CAF50)),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30),
        borderSide: BorderSide.none,
      ),
      filled: true,
      fillColor: Colors.grey.shade50,
      contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
      labelStyle: TextStyle(color: Colors.grey.shade700),
      errorStyle: const TextStyle(color: Colors.red, fontSize: 12, height: 1.2),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30),
        borderSide: BorderSide(color: Colors.red.shade300, width: 1),
      ),
      focusedErrorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(30),
        borderSide: BorderSide(color: Colors.red.shade300, width: 1),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Registro de nueva empresa'),
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
                    Image.asset("logo.png", width: 200),
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
                            TextFormField(
                              controller: companyNameController,
                              validator: _validateCompanyName,
                              decoration: _buildInputDecoration(
                                label: 'Nombre de la empresa',
                                hint: 'Ej: Mi Empresa SRL',
                                icon: Icons.business,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: tenantSlugController,
                              validator: _validateTenantSlug,
                              decoration: _buildInputDecoration(
                                label: 'Slug del tenant',
                                hint: 'Ej: miempresa2',
                                icon: Icons.link,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: adminEmailController,
                              keyboardType: TextInputType.emailAddress,
                              validator: _validateEmail,
                              decoration: _buildInputDecoration(
                                label: 'Correo del administrador',
                                hint: 'admin@ejemplo.com',
                                icon: Icons.email,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: passwordController,
                              obscureText: true,
                              validator: _validatePassword,
                              decoration: _buildInputDecoration(
                                label: 'Contraseña',
                                hint: 'Mínimo 8 caracteres',
                                icon: Icons.lock,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: firstNameController,
                              validator: _validateFirstName,
                              decoration: _buildInputDecoration(
                                label: 'Nombre',
                                hint: 'Ej: Juan',
                                icon: Icons.person,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: lastNameController,
                              validator: _validateLastName,
                              decoration: _buildInputDecoration(
                                label: 'Apellido',
                                hint: 'Ej: Pérez',
                                icon: Icons.person_outline,
                              ),
                            ),
                            const SizedBox(height: 28),
                            SizedBox(
                              width: double.infinity,
                              child: ElevatedButton(
                                onPressed: _viewModel.isLoading
                                    ? null
                                    : _signup,
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
                                            'Registrar empresa',
                                            style: TextStyle(
                                              fontSize: 18,
                                              fontWeight: FontWeight.bold,
                                              color: Colors.white,
                                            ),
                                          ),
                                  ),
                                ),
                              ),
                            ),
                            if (_viewModel.errorMessage != null)
                              Padding(
                                padding: const EdgeInsets.only(top: 12),
                                child: Text(
                                  _viewModel.errorMessage!,
                                  style: TextStyle(
                                    color: Colors.red.shade700,
                                    fontSize: 14,
                                  ),
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
                        textStyle: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                      child: const Text('¿Ya tienes cuenta? Inicia sesión'),
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
}
