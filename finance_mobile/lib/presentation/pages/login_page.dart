import 'dart:io';

import 'package:flutter/material.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:finance_mobile/core/services/biometric_auth_service.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import '../viewmodels/login_viewmodel.dart';

enum _LoginMode { password, face }

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _imagePicker = ImagePicker();
  final _tenantController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  late final LoginViewModel _viewModel;
  late final BiometricAuthService _biometricAuthService;

  _LoginMode _mode = _LoginMode.password;
  bool _obscurePassword = true;
  bool _biometricAvailable = false;
  bool _biometricEnabled = false;
  XFile? _faceImage;
  String? _faceImageLabel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<LoginViewModel>();
    _biometricAuthService = di.sl<BiometricAuthService>();
    _viewModel.addListener(_onViewModelChanged);

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _biometricAvailable = await _biometricAuthService.isBiometricAvailable();
      _biometricEnabled = await _biometricAuthService.isBiometricEnabled();
      if (mounted) {
        setState(() {});
      }
    });
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(_viewModel.errorMessage!)));
      _viewModel.clearError();
    }
    setState(() {});
  }

  @override
  void dispose() {
    _tenantController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  Future<void> _submitPasswordLogin() async {
    if (!_formKey.currentState!.validate()) return;
    final success = await _viewModel.login(
      _emailController.text.trim(),
      _passwordController.text,
      _tenantController.text.trim(),
    );
    if (!success || !mounted) return;

    await _biometricAuthService.saveCredentials(
      tenantSlug: _tenantController.text.trim(),
      email: _emailController.text.trim(),
      password: _passwordController.text,
    );

    await _offerBiometricSetupIfNeeded();

    if (mounted) {
      context.go('/home');
    }
  }

  Future<void> _submitFaceLogin() async {
    final tenant = _tenantController.text.trim();
    final email = _emailController.text.trim();
    if (tenant.isEmpty || email.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Completá tenant y correo para usar login facial.'),
        ),
      );
      return;
    }
    if (_faceImage == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Tomá una selfie o elegí una foto primero.'),
        ),
      );
      return;
    }

    final success = await _viewModel.faceLogin(email, tenant, _faceImage!.path);
    if (success && mounted) {
      context.go('/home');
    }
  }

  Future<void> _pickFaceImage(ImageSource source) async {
    try {
      final picked = await _imagePicker.pickImage(
        source: source,
        preferredCameraDevice: CameraDevice.front,
        imageQuality: 85,
      );
      if (picked == null || !mounted) return;
      setState(() {
        _faceImage = picked;
        _faceImageLabel = source == ImageSource.camera
            ? 'Selfie capturada'
            : 'Foto seleccionada';
      });
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('No se pudo obtener la imagen: $e')),
      );
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

    final storedCredentials = await _biometricAuthService
        .hasStoredCredentials();
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
      final enable = await _showBiometricDialog(
        title: 'Activar huella',
        message: '¿Querés activar huella digital para este dispositivo?',
        confirmLabel: 'Sí, activar',
      );
      if (enable != true) {
        return;
      }
      await _biometricAuthService.setBiometricEnabled(true);
      _biometricEnabled = true;
      if (mounted) setState(() {});
    }

    final success = await _biometricAuthService.authenticateUser();
    if (!success || !mounted) return;

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
    final storedCredentials = await _biometricAuthService
        .hasStoredCredentials();
    if (!storedCredentials) return;

    final alreadyEnabled = await _biometricAuthService.isBiometricEnabled();
    if (alreadyEnabled) return;

    final alreadyAsked = await _biometricAuthService.hasSeenBiometricPrompt();
    if (alreadyAsked) return;

    final supported = await _biometricAuthService.isBiometricAvailable();
    if (!supported || !mounted) {
      await _biometricAuthService.markBiometricPromptSeen();
      return;
    }

    final enableBiometrics = await _showBiometricDialog(
      title: 'Activar huella',
      message:
          '¿Querés usar huella digital para iniciar sesión más rápido en este dispositivo?',
      confirmLabel: 'Sí, activar',
    );

    await _biometricAuthService.markBiometricPromptSeen();
    if (enableBiometrics == true) {
      await _biometricAuthService.setBiometricEnabled(true);
      _biometricEnabled = true;
      if (mounted) setState(() {});
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

  InputDecoration _inputDecoration({
    required String label,
    required String hint,
    required IconData icon,
    Widget? suffixIcon,
  }) {
    return InputDecoration(
      labelText: label,
      hintText: hint,
      prefixIcon: Icon(icon),
      suffixIcon: suffixIcon,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(18)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Iniciar sesión'),
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF2E7D32),
        elevation: 0,
      ),
      body: Container(
        color: Colors.white,
        child: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const SizedBox(height: 8),
                Center(child: Image.asset('assets/logo.png', width: 180)),
                const SizedBox(height: 20),
                _buildModeSelector(),
                const SizedBox(height: 16),
                AnimatedSwitcher(
                  duration: const Duration(milliseconds: 200),
                  child: _mode == _LoginMode.password
                      ? _buildPasswordSection()
                      : _buildFaceSection(),
                ),
                const SizedBox(height: 16),
                _buildAuthLinks(context),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildModeSelector() {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Row(
        children: [
          Expanded(
            child: _ModeChip(
              label: 'Contraseña',
              selected: _mode == _LoginMode.password,
              onTap: () => setState(() => _mode = _LoginMode.password),
            ),
          ),
          Expanded(
            child: _ModeChip(
              label: 'Rostro',
              selected: _mode == _LoginMode.face,
              onTap: () => setState(() => _mode = _LoginMode.face),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPasswordSection() {
    return Card(
      key: const ValueKey('password-section'),
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
      color: const Color(0xFFF1F8E9),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                'Acceso tradicional',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _tenantController,
                textInputAction: TextInputAction.next,
                validator: _viewModel.validateTenant,
                decoration: _inputDecoration(
                  label: 'Tenant',
                  hint: 'Ej: miempresa',
                  icon: Icons.business,
                ),
              ),
              const SizedBox(height: 14),
              TextFormField(
                controller: _emailController,
                keyboardType: TextInputType.emailAddress,
                textInputAction: TextInputAction.next,
                autofillHints: const [AutofillHints.email],
                validator: _viewModel.validateEmail,
                decoration: _inputDecoration(
                  label: 'Correo electrónico',
                  hint: 'usuario@ejemplo.com',
                  icon: Icons.email_outlined,
                ),
              ),
              const SizedBox(height: 14),
              TextFormField(
                controller: _passwordController,
                obscureText: _obscurePassword,
                autocorrect: false,
                enableSuggestions: false,
                textInputAction: TextInputAction.done,
                autofillHints: const [AutofillHints.password],
                validator: _viewModel.validatePassword,
                onFieldSubmitted: (_) {
                  if (!_viewModel.isLoading) {
                    _submitPasswordLogin();
                  }
                },
                decoration: _inputDecoration(
                  label: 'Contraseña',
                  hint: '••••••••',
                  icon: Icons.lock_outline,
                  suffixIcon: IconButton(
                    tooltip: _obscurePassword
                        ? 'Mostrar contraseña'
                        : 'Ocultar contraseña',
                    onPressed: () {
                      setState(() => _obscurePassword = !_obscurePassword);
                    },
                    icon: Icon(
                      _obscurePassword
                          ? Icons.visibility_outlined
                          : Icons.visibility_off_outlined,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 18),
              ElevatedButton(
                onPressed: _viewModel.isLoading ? null : _submitPasswordLogin,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2E7D32),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(18),
                  ),
                ),
                child: _viewModel.isLoading && _mode == _LoginMode.password
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Entrar'),
              ),
              const SizedBox(height: 12),
              if (_biometricAvailable)
                OutlinedButton.icon(
                  onPressed: _viewModel.isLoading ? null : _tryBiometricLogin,
                  icon: Icon(
                    _biometricEnabled ? Icons.fingerprint : Icons.fingerprint,
                  ),
                  label: Text(
                    _biometricEnabled ? 'Entrar con huella' : 'Activar huella',
                  ),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: const Color(0xFF2E7D32),
                    side: const BorderSide(color: Color(0xFF2E7D32)),
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(18),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFaceSection() {
    return Card(
      key: const ValueKey('face-section'),
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
      color: const Color(0xFFF1F8E9),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Text(
              'Acceso con rostro',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'Necesitás tenant, correo e imagen.',
              style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _tenantController,
              decoration: _inputDecoration(
                label: 'Tenant',
                hint: 'Ej: miempresa',
                icon: Icons.business,
              ),
            ),
            const SizedBox(height: 14),
            TextFormField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              autofillHints: const [AutofillHints.email],
              decoration: _inputDecoration(
                label: 'Correo electrónico',
                hint: 'usuario@ejemplo.com',
                icon: Icons.email_outlined,
              ),
            ),
            const SizedBox(height: 16),
            _FacePreviewCard(image: _faceImage, label: _faceImageLabel),
            const SizedBox(height: 14),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _viewModel.isLoading
                        ? null
                        : () => _pickFaceImage(ImageSource.camera),
                    icon: const Icon(Icons.photo_camera_outlined),
                    label: const Text('Tomar selfie'),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: const Color(0xFF2E7D32),
                      side: const BorderSide(color: Color(0xFF2E7D32)),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _viewModel.isLoading
                        ? null
                        : () => _pickFaceImage(ImageSource.gallery),
                    icon: const Icon(Icons.photo_library_outlined),
                    label: const Text('Usar foto'),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: const Color(0xFF2E7D32),
                      side: const BorderSide(color: Color(0xFF2E7D32)),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 14),
            ElevatedButton.icon(
              onPressed: _viewModel.isLoading ? null : _submitFaceLogin,
              icon: _viewModel.isLoading
                  ? const SizedBox(
                      height: 18,
                      width: 18,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Colors.white,
                      ),
                    )
                  : const Icon(Icons.face_retouching_natural),
              label: Text(
                _viewModel.isLoading ? 'Validando...' : 'Entrar con rostro',
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(18),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAuthLinks(BuildContext context) {
    return Column(
      children: [
        TextButton(
          onPressed: () => context.push('/forgot-password'),
          child: const Text(
            'Olvidé mi contraseña',
            style: TextStyle(color: Color(0xFF2E7D32)),
          ),
        ),
        TextButton(
          onPressed: () => context.push('/signup'),
          child: const Text(
            'Crear tenant nuevo',
            style: TextStyle(color: Color(0xFF2E7D32)),
          ),
        ),
      ],
    );
  }
}

class _ModeChip extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;

  const _ModeChip({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: selected ? Colors.white : Colors.transparent,
          borderRadius: BorderRadius.circular(999),
          boxShadow: selected
              ? [
                  BoxShadow(
                    color: const Color(0xFF2E7D32).withOpacity(0.14),
                    blurRadius: 12,
                    offset: const Offset(0, 4),
                  ),
                ]
              : null,
        ),
        child: Text(
          label,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontWeight: FontWeight.w700,
            color: selected ? const Color(0xFF2E7D32) : Colors.grey.shade700,
          ),
        ),
      ),
    );
  }
}

class _FacePreviewCard extends StatelessWidget {
  final XFile? image;
  final String? label;

  const _FacePreviewCard({required this.image, required this.label});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 170,
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.grey.shade300),
      ),
      child: image == null
          ? Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.face_4_outlined,
                  size: 54,
                  color: Colors.grey.shade500,
                ),
                const SizedBox(height: 8),
                Text(
                  'Tomá una selfie o elegí una foto',
                  style: TextStyle(color: Colors.grey.shade700),
                ),
              ],
            )
          : Stack(
              fit: StackFit.expand,
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(24),
                  child: Image.file(File(image!.path), fit: BoxFit.cover),
                ),
                Positioned(
                  left: 12,
                  bottom: 12,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 10,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.black.withOpacity(0.55),
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Text(
                      label ?? 'Imagen lista',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ],
            ),
    );
  }
}
