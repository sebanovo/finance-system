import 'dart:io';

import 'package:flutter/material.dart';
import 'package:finance_mobile/constants/env.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import '../viewmodels/home_viewmodel.dart';
import '../viewmodels/profile_viewmodel.dart';
import '../widgets/change_password_dialog.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _surfaceVariant = Color(0xFFF9FAFB);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  late final ProfileViewModel _viewModel;
  final _imagePicker = ImagePicker();
  final _formKey = GlobalKey<FormState>();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  XFile? _selectedPhoto;
  String? _selectedPhotoLabel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<ProfileViewModel>();
    _viewModel.addListener(_onChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _viewModel.loadProfile();
    });
  }

  void _onChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(_viewModel.errorMessage!)));
      _viewModel.clearError();
    }
    final profile = _viewModel.profile;
    if (profile != null) {
      _firstNameController.text = profile.firstName ?? '';
      _lastNameController.text = profile.lastName ?? '';
    }
    setState(() {});
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    _viewModel.removeListener(_onChanged);
    super.dispose();
  }

  Future<void> _pickPhoto(ImageSource source) async {
    try {
      final picked = await _imagePicker.pickImage(
        source: source,
        preferredCameraDevice: CameraDevice.front,
        imageQuality: 85,
      );
      if (picked == null || !mounted) return;
      setState(() {
        _selectedPhoto = picked;
        _selectedPhotoLabel = source == ImageSource.camera
            ? 'Foto capturada'
            : 'Foto seleccionada';
      });
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('No se pudo obtener la foto: $e')));
    }
  }

  Future<void> _saveProfile() async {
    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    final success = await _viewModel.updateProfile(
      firstName: _firstNameController.text.trim().isEmpty
          ? null
          : _firstNameController.text.trim(),
      lastName: _lastNameController.text.trim().isEmpty
          ? null
          : _lastNameController.text.trim(),
      photoPath: _selectedPhoto?.path,
      photoName: _selectedPhoto?.name,
    );

    if (!success || !mounted) return;

    await di.sl<HomeViewModel>().loadUserInfo();
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Perfil actualizado'),
        backgroundColor: Colors.green,
      ),
    );
    setState(() {
      _selectedPhoto = null;
      _selectedPhotoLabel = null;
    });
  }

  Future<void> _removePhoto() async {
    final success = await _viewModel.removeProfilePhoto();
    if (!success || !mounted) return;
    await di.sl<HomeViewModel>().loadUserInfo();
    if (!mounted) return;
    setState(() {
      _selectedPhoto = null;
      _selectedPhotoLabel = null;
    });
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Foto eliminada'),
        backgroundColor: Colors.green,
      ),
    );
  }

  Future<void> _logout() async {
    await _viewModel.logout();
    if (!mounted) return;
    context.go('/login');
  }

  void _showChangePasswordDialog() {
    showDialog(
      context: context,
      builder: (context) => ChangePasswordDialog(
        onChangePassword: (current, newPwd) async {
          await di.sl<HomeViewModel>().changePassword(current, newPwd);
        },
      ),
    );
  }

  String _profileImageUrl() {
    final profile = _viewModel.profile;
    if (profile == null || profile.profilePhotoUrl == null) return '';
    return Uri.parse(Env.baseUrl).resolve(profile.profilePhotoUrl!).toString();
  }

  @override
  Widget build(BuildContext context) {
    final profile = _viewModel.profile;
    final isLoading = _viewModel.loading && profile == null;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mi perfil'),
        backgroundColor: _surface,
        foregroundColor: _green,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadProfile(),
        color: _green,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildHeader(profile),
            const SizedBox(height: 16),
            if (isLoading)
              const Center(child: CircularProgressIndicator(color: _green))
            else if (profile != null) ...[
              Card(
                color: _surface,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(24),
                  side: const BorderSide(color: _outline),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Form(
                    key: _formKey,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        const Text(
                          'Editar perfil',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: _ink,
                          ),
                        ),
                        const SizedBox(height: 16),
                        TextFormField(
                          controller: _firstNameController,
                          decoration: const InputDecoration(
                            labelText: 'Nombre',
                            border: OutlineInputBorder(),
                          ),
                        ),
                        const SizedBox(height: 12),
                        TextFormField(
                          controller: _lastNameController,
                          decoration: const InputDecoration(
                            labelText: 'Apellido',
                            border: OutlineInputBorder(),
                          ),
                        ),
                        const SizedBox(height: 12),
                        TextFormField(
                          initialValue: profile.email,
                          enabled: false,
                          decoration: const InputDecoration(
                            labelText: 'Correo',
                            border: OutlineInputBorder(),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 12),
              Card(
                color: _surface,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(24),
                  side: const BorderSide(color: _outline),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Text(
                          'Foto de perfil',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: _ink,
                          ),
                        ),
                      const SizedBox(height: 12),
                      _ProfilePhotoPreview(
                        localPhoto: _selectedPhoto,
                        remoteUrl: _profileImageUrl(),
                        fallbackInitial: profile.initial,
                        label: _selectedPhotoLabel,
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: OutlinedButton.icon(
                              onPressed: _viewModel.saving
                                  ? null
                                  : () => _pickPhoto(ImageSource.camera),
                              icon: const Icon(Icons.photo_camera_outlined),
                              label: const Text('Tomar foto'),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: OutlinedButton.icon(
                              onPressed: _viewModel.saving
                                  ? null
                                  : () => _pickPhoto(ImageSource.gallery),
                              icon: const Icon(Icons.photo_library_outlined),
                              label: const Text('Usar foto'),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: ElevatedButton.icon(
                              onPressed: _viewModel.saving
                                  ? null
                                  : _saveProfile,
                              icon: _viewModel.saving
                                  ? const SizedBox(
                                      height: 18,
                                      width: 18,
                                      child: CircularProgressIndicator(
                                        strokeWidth: 2,
                                        color: Colors.white,
                                      ),
                                    )
                                  : const Icon(Icons.save_outlined),
                              label: const Text('Guardar cambios'),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: _green,
                                foregroundColor: Colors.white,
                              ),
                            ),
                          ),
                          const SizedBox(width: 12),
                          OutlinedButton.icon(
                            onPressed: _viewModel.saving ? null : _removePhoto,
                            icon: const Icon(Icons.delete_outline),
                            label: const Text('Quitar foto'),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 12),
              Card(
                color: _surface,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(24),
                  side: const BorderSide(color: _outline),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Text(
                          'Seguridad',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: _ink,
                          ),
                        ),
                      const SizedBox(height: 12),
                      ElevatedButton.icon(
                        onPressed: _showChangePasswordDialog,
                        icon: const Icon(Icons.lock_reset),
                        label: const Text('Cambiar contraseña'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: _green,
                          foregroundColor: Colors.white,
                        ),
                      ),
                      const SizedBox(height: 8),
                      OutlinedButton.icon(
                        onPressed: _logout,
                        icon: const Icon(Icons.logout),
                        label: const Text('Cerrar sesión'),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: _ink,
                          side: const BorderSide(color: _outline),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ] else
              Card(
                color: _surface,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(24),
                  side: const BorderSide(color: _outline),
                ),
                child: const Padding(
                  padding: EdgeInsets.all(20),
                  child: Text(
                    'No pudimos cargar tu perfil. Intenta refrescar.',
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(dynamic profile) {
    final name = profile?.displayName ?? 'Perfil';
    final email = profile?.email ?? '';
    final remoteUrl = profile == null || profile.profilePhotoUrl == null
        ? ''
        : Uri.parse(Env.baseUrl).resolve(profile.profilePhotoUrl!).toString();
    final ImageProvider? imageProvider = _selectedPhoto != null
        ? FileImage(File(_selectedPhoto!.path))
        : remoteUrl.isNotEmpty
        ? NetworkImage(remoteUrl)
        : null;

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: _green,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: _outline),
      ),
      child: Row(
        children: [
          CircleAvatar(
            radius: 30,
            backgroundColor: _surface,
            backgroundImage: imageProvider,
            child: (_selectedPhoto == null && remoteUrl.isEmpty)
                ? Text(
                    profile?.initial ?? 'U',
                    style: const TextStyle(
                      color: _green,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  )
                : null,
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  email,
                  style: const TextStyle(color: Colors.white70, fontSize: 13),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _ProfilePhotoPreview extends StatelessWidget {
  final XFile? localPhoto;
  final String remoteUrl;
  final String fallbackInitial;
  final String? label;

  const _ProfilePhotoPreview({
    required this.localPhoto,
    required this.remoteUrl,
    required this.fallbackInitial,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    final hasLocal = localPhoto != null;
    final hasRemote = remoteUrl.isNotEmpty;

    return Container(
      height: 180,
      decoration: BoxDecoration(
        color: _surfaceVariant,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: _outline),
      ),
      child: Stack(
        fit: StackFit.expand,
        children: [
          if (hasLocal)
            ClipRRect(
              borderRadius: BorderRadius.circular(24),
              child: Image.file(File(localPhoto!.path), fit: BoxFit.cover),
            )
          else if (hasRemote)
            ClipRRect(
              borderRadius: BorderRadius.circular(24),
              child: Image.network(remoteUrl, fit: BoxFit.cover),
            )
          else
            Center(
              child: CircleAvatar(
                radius: 42,
                backgroundColor: _green,
                child: Text(
                  fallbackInitial,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 28,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          if (label != null)
            Positioned(
              left: 12,
              bottom: 12,
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 6,
                ),
                decoration: BoxDecoration(
                  color: Colors.black,
                  borderRadius: BorderRadius.circular(999),
                ),
                child: Text(
                  label!,
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
