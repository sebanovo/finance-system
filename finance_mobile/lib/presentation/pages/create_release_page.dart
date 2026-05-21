import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/accounts_viewmodel.dart';
import '../viewmodels/transactions_viewmodel.dart';

class CreateReleasePage extends StatefulWidget {
  const CreateReleasePage({super.key});

  @override
  State<CreateReleasePage> createState() => _CreateReleasePageState();
}

class _CreateReleasePageState extends State<CreateReleasePage> {
  late TransactionsViewModel _viewModel;
  late AccountsViewModel _accountsViewModel;

  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _idempotencyKeyController = TextEditingController();
  String? _selectedAccountId;
  String _selectedCurrency = 'BOB';

  final Map<String, String> _currencies = {
    'BOB': 'Bolivianos (BOB)',
    'USD': 'Dólares (USD)',
    'EUR': 'Euros (EUR)',
    'USDT': 'Tether (USDT)',
  };

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<TransactionsViewModel>();
    _accountsViewModel = di.sl<AccountsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _accountsViewModel.addListener(_onAccountsViewModelChanged);
    _accountsViewModel.loadAccounts();
    _idempotencyKeyController.text = _generateIdempotencyKey();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.transactionCreated) {
      _viewModel.clearTransactionCreated();
      _showSnackBar('Release creado exitosamente', isError: false);
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.pop();
      });
    }
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    setState(() {});
  }

  void _onAccountsViewModelChanged() {
    if (!mounted) return;
    setState(() {});
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  String _generateIdempotencyKey() {
    return 'hold_${DateTime.now().millisecondsSinceEpoch}_${DateTime.now().microsecond}';
  }

  String _getAccountDisplayText(account) {
    final name = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.accountNameLabel;
    return '$name - ${account.accountNumber} - ${account.formattedAvailableBalance}';
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedAccountId == null) {
      _showSnackBar('Selecciona una cuenta');
      return;
    }

    await _viewModel.createRelease(
      accountId: _selectedAccountId!,
      amount: double.parse(_amountController.text),
      currency: _selectedCurrency,
      idempotencyKey: _idempotencyKeyController.text,
      description: _descriptionController.text.isEmpty
          ? null
          : _descriptionController.text,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Liberar Fondos (Release)'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Card(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Cuenta',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 12),
                      if (_accountsViewModel.loading)
                        const Center(child: CircularProgressIndicator())
                      else if (_accountsViewModel.accounts.isEmpty)
                        const Text('No hay cuentas disponibles')
                      else
                        DropdownButtonFormField<String>(
                          initialValue: _selectedAccountId,
                          isExpanded: true,
                          decoration: const InputDecoration(
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.all(
                                Radius.circular(12),
                              ),
                            ),
                            contentPadding: EdgeInsets.symmetric(
                              horizontal: 12,
                              vertical: 8,
                            ),
                          ),
                          items: _accountsViewModel.accounts.map((account) {
                            return DropdownMenuItem(
                              value: account.id,
                              child: Text(
                                _getAccountDisplayText(account),
                                overflow: TextOverflow.ellipsis,
                              ),
                            );
                          }).toList(),
                          onChanged: (value) =>
                              setState(() => _selectedAccountId = value),
                          validator: (value) =>
                              value == null ? 'Selecciona una cuenta' : null,
                        ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Card(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Monto',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            flex: 2,
                            child: TextFormField(
                              controller: _amountController,
                              keyboardType:
                                  const TextInputType.numberWithOptions(
                                    decimal: true,
                                  ),
                              decoration: const InputDecoration(
                                labelText: 'Monto',
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.all(
                                    Radius.circular(12),
                                  ),
                                ),
                                prefixText: 'Bs. ',
                              ),
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'Campo requerido';
                                }
                                if (double.tryParse(value) == null) {
                                  return 'Monto inválido';
                                }
                                if (double.parse(value) <= 0) {
                                  return 'Monto debe ser mayor a 0';
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: DropdownButtonFormField<String>(
                              initialValue: _selectedCurrency,
                              isExpanded: true,
                              decoration: const InputDecoration(
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.all(
                                    Radius.circular(12),
                                  ),
                                ),
                                contentPadding: EdgeInsets.symmetric(
                                  horizontal: 12,
                                  vertical: 8,
                                ),
                              ),
                              items: _currencies.entries
                                  .map(
                                    (entry) => DropdownMenuItem(
                                      value: entry.key,
                                      child: Text(entry.value),
                                    ),
                                  )
                                  .toList(),
                              onChanged: (value) =>
                                  setState(() => _selectedCurrency = value!),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Card(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Descripción (opcional)',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 12),
                      TextFormField(
                        controller: _descriptionController,
                        maxLines: 3,
                        decoration: const InputDecoration(
                          hintText: 'Motivo del bloqueo',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(12)),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                height: 50,
                child: ElevatedButton(
                  onPressed: _viewModel.creating ? null : _submit,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF2E7D32),
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30),
                    ),
                  ),
                  child: _viewModel.creating
                      ? const SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: Colors.white,
                          ),
                        )
                      : const Text(
                          'Bloquear Fondos',
                          style: TextStyle(fontSize: 16),
                        ),
                ),
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _amountController.dispose();
    _descriptionController.dispose();
    _idempotencyKeyController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    _accountsViewModel.removeListener(_onAccountsViewModelChanged);
    super.dispose();
  }
}
