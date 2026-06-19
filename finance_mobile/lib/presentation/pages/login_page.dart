// lib/presentation/pages/login_page.dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/services/biometric_auth_service.dart';
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
  final _formKey = GlobalKey<FormState>();
  bool _obscurePassword = true;

  late LoginViewModel _viewModel;
  late BiometricAuthService _biometricAuthService;
  bool _biometricSupported = false;
  bool _biometricEnabled = false;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<LoginViewModel>();
    _biometricAuthService = di.sl<BiometricAuthService>();
    _viewModel.addListener(_onViewModelChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _biometricSupported = await _biometricAuthService.isBiometricAvailable();
      _biometricEnabled = await _biometricAuthService.isBiometricEnabled();
      if (mounted) {
        setState(() {});
      }
    });
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    setState(() {});
  }

  Future<void> _login() async {
    if (!_formKey.currentState!.validate()) return;
    final success = await _viewModel.login(
      emailController.text.trim(),
      passwordController.text,
      tenantController.text.trim(),
    );
    if (!success || !mounted) {
      return;
    }

    await _biometricAuthService.saveCredentials(
      tenantSlug: tenantController.text.trim(),
      email: emailController.text.trim(),
      password: passwordController.text,
    );

    await _offerBiometricSetupIfNeeded();

    if (mounted) {
      context.go('/home');
    }
  }

  Future<void> _tryBiometricLogin() async {
    if (!mounted) return;
    final messenger = ScaffoldMessenger.of(context);
    final available = await _biometricAuthService.isBiometricAvailable();
    if (!available) {
      messenger.showSnackBar(
        const SnackBar(
          content: Text('No hay biometría disponible en este dispositivo.'),
        ),
      );
      return;
    }

    final storedCredentials = await _biometricAuthService.hasStoredCredentials();
    if (!storedCredentials) {
      messenger.showSnackBar(
        const SnackBar(
          content: Text('Primero iniciá sesión con correo y contraseña.'),
        ),
      );
      return;
    }

    final biometricEnabled = await _biometricAuthService.isBiometricEnabled();
    if (!biometricEnabled) {
      if (!mounted) return;
      // ignore: use_build_context_synchronously
      final enable = await _showBiometricDialog(
        title: 'Activar huella',
        message: '¿Querés activar huella digital para este dispositivo?',
        confirmLabel: 'Sí, activar',
      );

      if (enable != true) {
        return;
      }

      await _biometricAuthService.setBiometricEnabled(true);
      if (mounted) {
        setState(() {
          _biometricEnabled = true;
        });
      }
    }

    final success = await _biometricAuthService.authenticateUser();
    if (!success || !mounted) {
      return;
    }

    final creds = await _biometricAuthService.readCredentials();
    if (creds == null) {
      messenger.showSnackBar(
        const SnackBar(
          content: Text('Primero iniciá sesión con correo y contraseña.'),
        ),
      );
      return;
    }

    final loggedIn = await _viewModel.login(
      creds.email,
      creds.password,
      creds.tenantSlug,
    );
    if (loggedIn && mounted) {
      context.go('/home');
    }
  }

  Future<void> _offerBiometricSetupIfNeeded() async {
    final storedCredentials = await _biometricAuthService.hasStoredCredentials();
    if (!storedCredentials) {
      return;
    }

    final alreadyEnabled = await _biometricAuthService.isBiometricEnabled();
    if (alreadyEnabled) {
      return;
    }

    final alreadyAsked = await _biometricAuthService.hasSeenBiometricPrompt();
    if (alreadyAsked) {
      await _biometricAuthService.markBiometricPromptSeen();
      return;
    }

    final supported = await _biometricAuthService.isBiometricAvailable();
    if (!supported || !mounted) {
      await _biometricAuthService.markBiometricPromptSeen();
      return;
    }

    final enableBiometrics = await _showBiometricDialog(
      title: 'Activar huella',
      message: '¿Querés usar huella digital para iniciar sesión más rápido en este dispositivo?',
      confirmLabel: 'Sí, activar',
    );

    await _biometricAuthService.markBiometricPromptSeen();
    if (enableBiometrics == true) {
      await _biometricAuthService.setBiometricEnabled(true);
      if (mounted) {
        setState(() {
          _biometricEnabled = true;
        });
      }
    }
  }

  Future<bool?> _showBiometricDialog({
    required String title,
    required String message,
    required String confirmLabel,
  }) {
    return showDialog<bool>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: Text(title),
          content: Text(message),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(false),
              child: const Text('No, gracias'),
            ),
            ElevatedButton(
              onPressed: () => Navigator.of(dialogContext).pop(true),
              child: Text(confirmLabel),
            ),
          ],
        );
      },
    );
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
      appBar: AppBar(
        title: const Text('Iniciar Sesión'),
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
                    Image.asset("assets/logo.png", width: 200),
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
                              controller: tenantController,
                              textInputAction: TextInputAction.next,
                              validator: _viewModel.validateTenant,
                              onFieldSubmitted: (_) => FocusScope.of(context).nextFocus(),
                              decoration: _buildInputDecoration(
                                label: 'Nombre del tenant',
                                hint: 'Ej: miempresa',
                                icon: Icons.business,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: emailController,
                              keyboardType: TextInputType.emailAddress,
                              textInputAction: TextInputAction.next,
                              autofillHints: const [AutofillHints.email],
                              validator: _viewModel.validateEmail,
                              onFieldSubmitted: (_) => FocusScope.of(context).nextFocus(),
                              decoration: _buildInputDecoration(
                                label: 'Correo electrónico',
                                hint: 'usuario@ejemplo.com',
                                icon: Icons.email,
                              ),
                            ),
                            const SizedBox(height: 16),
                            TextFormField(
                              controller: passwordController,
                              obscureText: _obscurePassword,
                              autocorrect: false,
                              enableSuggestions: false,
                              textInputAction: TextInputAction.done,
                              autofillHints: const [AutofillHints.password],
                              onFieldSubmitted: (_) => _viewModel.isLoading ? null : _login(),
                              validator: _viewModel.validatePassword,
                              decoration: _buildInputDecoration(
                                label: 'Contraseña',
                                hint: '',
                                icon: Icons.lock,
                                suffixIcon: IconButton(
                                  tooltip: _obscurePassword ? 'Mostrar contraseña' : 'Ocultar contraseña',
                                  onPressed: () {
                                    setState(() {
                                      _obscurePassword = !_obscurePassword;
                                    });
                                  },
                                  icon: Icon(
                                    _obscurePassword ? Icons.visibility_outlined : Icons.visibility_off_outlined,
                                    color: const Color(0xFF4CAF50),
                                  ),
                                ),
                              ),
                            ),
                            const SizedBox(height: 28),
                            SizedBox(
                              width: double.infinity,
                              child: _buildLoginButton(),
                            ),
                            Padding(
                              padding: const EdgeInsets.only(top: 14),
                              child: Center(
                                child: SizedBox(
                                  width: 66,
                                  height: 66,
                                  child: ElevatedButton(
                                    onPressed: _viewModel.isLoading ? null : _tryBiometricLogin,
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: _biometricEnabled
                                          ? const Color(0xFF2E7D32)
                                          : (_biometricSupported
                                              ? Colors.white
                                              : Colors.grey.shade100),
                                      foregroundColor: _biometricEnabled
                                          ? Colors.white
                                          : (_biometricSupported
                                              ? const Color(0xFF2E7D32)
                                              : Colors.grey.shade500),
                                      shape: const CircleBorder(),
                                      padding: EdgeInsets.zero,
                                      elevation: 0,
                                      side: BorderSide(
                                        color: _biometricEnabled
                                            ? const Color(0xFF2E7D32)
                                            : (_biometricSupported
                                                ? const Color(0xFFC8E6C9)
                                                : Colors.grey.shade300),
                                        width: 1.6,
                                      ),
                                    ),
                                    child: const Icon(Icons.fingerprint, size: 32),
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
                      onPressed: () => context.push('/forgot-password'),
                      style: TextButton.styleFrom(
                        foregroundColor: const Color(0xFF2E7D32),
                        textStyle: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                      child: const Text('¿Olvidaste tu contraseña?'),
                    ),
                    const SizedBox(height: 8),
                    TextButton(
                      onPressed: () => context.push('/signup'),
                      style: TextButton.styleFrom(
                        foregroundColor: const Color(0xFF2E7D32),
                        textStyle: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                      child: const Text(
                        '¿No tienes cuenta? Registra tu empresa',
                      ),
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

  Widget _buildLoginButton() {
    return ElevatedButton(
      onPressed: _viewModel.isLoading ? null : _login,
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.transparent,
        shadowColor: Colors.transparent,
        padding: const EdgeInsets.symmetric(vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(40)),
      ),
      child: Ink(
        decoration: BoxDecoration(
          gradient: const LinearGradient(
            colors: [Color(0xFF2E7D32), Color(0xFF66BB6A)],
            begin: Alignment.centerLeft,
            end: Alignment.centerRight,
          ),
          borderRadius: BorderRadius.circular(40),
        ),
        child: Container(
          height: 40,
          alignment: Alignment.center,
          child: _viewModel.isLoading
              ? const CircularProgressIndicator(color: Colors.white)
              : const Text(
                  'Iniciar sesión',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
        ),
      ),
    );
  }

  InputDecoration _buildInputDecoration({
    required String label,
    required String hint,
    required IconData icon,
    Widget? suffixIcon,
  }) {
    return InputDecoration(
      labelText: label,
      hintText: hint.isEmpty ? null : hint,
      prefixIcon: Icon(icon, color: const Color(0xFF4CAF50)),
      suffixIcon: suffixIcon,
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
}
