import 'package:flutter/material.dart';

class CreateAccountSheet extends StatefulWidget {
  final Map<String, String> accountNameOptions;
  final Map<String, String> accountTypeOptions;
  final Map<String, String> currencyOptions;
  final Future<void> Function(
    String accountName,
    String accountType,
    String currency,
    String customAlias,
  )
  onSubmit;

  const CreateAccountSheet({
    super.key,
    required this.accountNameOptions,
    required this.accountTypeOptions,
    required this.currencyOptions,
    required this.onSubmit,
  });

  @override
  State<CreateAccountSheet> createState() => _CreateAccountSheetState();
}

class _CreateAccountSheetState extends State<CreateAccountSheet> {
  final _formKey = GlobalKey<FormState>();
  final _customAliasController = TextEditingController();
  String _selectedAccountName = 'SAVINGS_ACCOUNT';
  String _selectedAccountType = 'CHECKING';
  String _selectedCurrency = 'BOB';
  bool _submitting = false;

  @override
  void dispose() {
    _customAliasController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _submitting = true);
    await widget.onSubmit(
      _selectedAccountName,
      _selectedAccountType,
      _selectedCurrency,
      _customAliasController.text.trim(),
    );
    setState(() => _submitting = false);
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom,
      ),
      child: Container(
        constraints: BoxConstraints(
          maxHeight: MediaQuery.of(context).size.height * 0.75,
        ),
        padding: const EdgeInsets.fromLTRB(24, 16, 24, 24),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHandleBar(),
              const SizedBox(height: 16),
              const Text(
                'Nueva Cuenta',
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF2E7D32),
                ),
              ),
              const SizedBox(height: 20),
              _buildAccountTypeDropdown(),
              const SizedBox(height: 16),
              _buildClassificationDropdown(),
              const SizedBox(height: 16),
              _buildCurrencyDropdown(),
              const SizedBox(height: 16),
              _buildAliasField(),
              const SizedBox(height: 24),
              _buildSubmitButton(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHandleBar() {
    return Center(
      child: Container(
        width: 40,
        height: 4,
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: BorderRadius.circular(2),
        ),
      ),
    );
  }

  Widget _buildAccountTypeDropdown() {
    return DropdownButtonFormField<String>(
      value: _selectedAccountName,
      decoration: const InputDecoration(
        labelText: 'Tipo de cuenta',
        prefixIcon: Icon(Icons.account_balance_wallet),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12)),
        ),
      ),
      items: widget.accountNameOptions.entries.map((entry) {
        return DropdownMenuItem(value: entry.key, child: Text(entry.value));
      }).toList(),
      onChanged: (value) => setState(() => _selectedAccountName = value!),
    );
  }

  Widget _buildClassificationDropdown() {
    return DropdownButtonFormField<String>(
      value: _selectedAccountType,
      decoration: const InputDecoration(
        labelText: 'Clasificación',
        prefixIcon: Icon(Icons.category),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12)),
        ),
      ),
      items: widget.accountTypeOptions.entries.map((entry) {
        return DropdownMenuItem(value: entry.key, child: Text(entry.value));
      }).toList(),
      onChanged: (value) => setState(() => _selectedAccountType = value!),
    );
  }

  Widget _buildCurrencyDropdown() {
    return DropdownButtonFormField<String>(
      value: _selectedCurrency,
      decoration: const InputDecoration(
        labelText: 'Moneda',
        prefixIcon: Icon(Icons.currency_exchange),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12)),
        ),
      ),
      items: widget.currencyOptions.entries.map((entry) {
        return DropdownMenuItem(value: entry.key, child: Text(entry.value));
      }).toList(),
      onChanged: (value) => setState(() => _selectedCurrency = value!),
    );
  }

  Widget _buildAliasField() {
    return TextFormField(
      controller: _customAliasController,
      decoration: const InputDecoration(
        labelText: 'Alias (opcional)',
        prefixIcon: Icon(Icons.edit_note),
        hintText: 'Ej: Mi cuenta de ahorros',
        border: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12)),
        ),
      ),
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton.icon(
        onPressed: _submitting ? null : _submit,
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFF2E7D32),
          foregroundColor: Colors.white,
          padding: const EdgeInsets.symmetric(vertical: 14),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        icon: _submitting
            ? const SizedBox(
                width: 18,
                height: 18,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  color: Colors.white,
                ),
              )
            : const Icon(Icons.add),
        label: Text(_submitting ? 'Creando...' : 'Crear Cuenta'),
      ),
    );
  }
}
