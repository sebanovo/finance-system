import 'package:flutter/material.dart';
import '../viewmodels/signup_viewmodel.dart';

class SignupForm extends StatefulWidget {
  final SignupViewModel viewModel;
  final GlobalKey<FormState> formKey;
  final TextEditingController companyNameController;
  final TextEditingController tenantSlugController;
  final TextEditingController adminEmailController;
  final TextEditingController passwordController;
  final TextEditingController firstNameController;
  final TextEditingController lastNameController;
  final VoidCallback onSubmit;

  const SignupForm({
    super.key,
    required this.viewModel,
    required this.formKey,
    required this.companyNameController,
    required this.tenantSlugController,
    required this.adminEmailController,
    required this.passwordController,
    required this.firstNameController,
    required this.lastNameController,
    required this.onSubmit,
  });

  @override
  State<SignupForm> createState() => _SignupFormState();
}

class _SignupFormState extends State<SignupForm> {
  bool _obscurePassword = true;

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
    return Form(
      key: widget.formKey,
      child: Column(
        children: [
          TextFormField(
            controller: widget.companyNameController,
            validator: widget.viewModel.validateCompanyName,
            decoration: _buildInputDecoration(
              label: 'Nombre de la empresa',
              hint: 'Ej: Mi Empresa SRL',
              icon: Icons.business,
            ),
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: widget.tenantSlugController,
            validator: widget.viewModel.validateTenantSlug,
            decoration: _buildInputDecoration(
              label: 'Slug del tenant',
              hint: 'Ej: miempresa2',
              icon: Icons.link,
            ),
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: widget.adminEmailController,
            keyboardType: TextInputType.emailAddress,
            validator: widget.viewModel.validateEmail,
            decoration: _buildInputDecoration(
              label: 'Correo del administrador',
              hint: 'admin@ejemplo.com',
              icon: Icons.email,
            ),
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: widget.passwordController,
            obscureText: _obscurePassword,
            validator: widget.viewModel.validatePassword,
            decoration: _buildInputDecoration(
              label: 'Contraseña',
              hint: 'Mínimo 8 caracteres',
              icon: Icons.lock,
            ).copyWith(
              suffixIcon: IconButton(
                onPressed: () {
                  setState(() => _obscurePassword = !_obscurePassword);
                },
                icon: Icon(
                  _obscurePassword ? Icons.visibility_off : Icons.visibility,
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: widget.firstNameController,
            validator: widget.viewModel.validateFirstName,
            decoration: _buildInputDecoration(
              label: 'Nombre',
              hint: 'Ej: Juan',
              icon: Icons.person,
            ),
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: widget.lastNameController,
            validator: widget.viewModel.validateLastName,
            decoration: _buildInputDecoration(
              label: 'Apellido',
              hint: 'Ej: Pérez',
              icon: Icons.person_outline,
            ),
          ),
          const SizedBox(height: 28),
          _buildSubmitButton(),
          if (widget.viewModel.errorMessage != null)
            Padding(
              padding: const EdgeInsets.only(top: 12),
              child: Text(
                widget.viewModel.errorMessage!,
                style: TextStyle(color: Colors.red.shade700, fontSize: 14),
                textAlign: TextAlign.center,
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton(
        onPressed: widget.viewModel.isLoading ? null : widget.onSubmit,
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.transparent,
          shadowColor: Colors.transparent,
          padding: const EdgeInsets.symmetric(vertical: 14),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(40),
          ),
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
            child: widget.viewModel.isLoading
                ? const CircularProgressIndicator(color: Colors.white)
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
    );
  }
}
