import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/accounts_viewmodel.dart';

class AccountsPage extends StatefulWidget {
  const AccountsPage({super.key});

  @override
  State<AccountsPage> createState() => _AccountsPageState();
}

class _AccountsPageState extends State<AccountsPage> {
  late AccountsViewModel _viewModel;

  final Map<String, String> accountNameOptions = {
    'MAIN_WALLET': 'Billetera principal',
    'SAVINGS_ACCOUNT': 'Cuenta de ahorro',
    'CHECKING_ACCOUNT': 'Cuenta corriente',
    'CREDIT_CARD_ACCOUNT': 'Cuenta de crédito',
    'PREPAID_CARD_ACCOUNT': 'Cuenta prepago',
    'LOAN_ACCOUNT': 'Cuenta de préstamo',
    'BUSINESS_ACCOUNT': 'Cuenta comercial',
    'SECONDARY_ACCOUNT': 'Cuenta secundaria',
  };

  final Map<String, String> accountTypeOptions = {
    'WALLET': 'Billetera',
    'SAVINGS': 'Ahorro',
    'CHECKING': 'Corriente',
    'CREDIT_CARD': 'Tarjeta de crédito',
    'PREPAID_CARD': 'Tarjeta prepago',
    'LOAN': 'Préstamo',
  };

  final Map<String, String> currencyOptions = {
    'BOB': 'Bolivianos (BOB)',
    'USD': 'Dólares (USD)',
    'EUR': 'Euros (EUR)',
    'USDT': 'Tether (USDT)',
  };

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<AccountsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadAccounts();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.accountCreated) {
      _viewModel.clearAccountCreated();
      _showSnackBar('Cuenta creada exitosamente', isError: false);
    }
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

  void _openCreateAccountDialog() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) => _CreateAccountSheet(
        accountNameOptions: accountNameOptions,
        accountTypeOptions: accountTypeOptions,
        currencyOptions: currencyOptions,
        onSubmit: (accountName, accountType, currency, customAlias) async {
          await _viewModel.createAccount(
            accountName: accountName,
            accountType: accountType,
            currency: currency,
            customAlias: customAlias,
          );
          if (mounted && _viewModel.errorMessage == null) {
            Navigator.of(context).pop();
          }
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mis Cuentas'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _openCreateAccountDialog,
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Nueva Cuenta'),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_viewModel.errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 64, color: Colors.red.shade300),
            const SizedBox(height: 16),
            Text(
              _viewModel.errorMessage!,
              style: const TextStyle(fontSize: 16),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => _viewModel.loadAccounts(),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
              ),
              child: const Text('Reintentar'),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () => context.go('/login'),
              child: const Text('Ir a inicio de sesión'),
            ),
          ],
        ),
      );
    }

    if (_viewModel.accounts.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.account_balance_wallet,
              size: 64,
              color: Colors.grey.shade400,
            ),
            const SizedBox(height: 16),
            const Text(
              'No tienes cuentas',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 8),
            const Text(
              'Crea tu primera cuenta para comenzar',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: _openCreateAccountDialog,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
              ),
              child: const Text('Crear primera cuenta'),
            ),
          ],
        ),
      );
    }

    return Column(
      children: [
        // Tarjeta de resumen de saldo total
        Container(
          margin: const EdgeInsets.all(16),
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            gradient: const LinearGradient(
              colors: [Color(0xFF2E7D32), Color(0xFF4CAF50)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(24),
            boxShadow: [
              BoxShadow(
                color: Colors.green.shade100,
                blurRadius: 10,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Column(
            children: [
              const Text(
                'Saldo Total',
                style: TextStyle(color: Colors.white70, fontSize: 14),
              ),
              const SizedBox(height: 8),
              Text(
                _viewModel.formattedTotalBalance,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                '${_viewModel.accounts.length} cuenta(s) activa(s)',
                style: const TextStyle(color: Colors.white70, fontSize: 12),
              ),
            ],
          ),
        ),
        // Lista de cuentas
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            itemCount: _viewModel.accounts.length,
            itemBuilder: (context, index) {
              final account = _viewModel.accounts[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Container(
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(16),
                    border: account.primary
                        ? Border.all(color: const Color(0xFF2E7D32), width: 2)
                        : null,
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: account.primary
                          ? const Color(0xFF2E7D32)
                          : const Color(0xFFE8F5E9),
                      child: Icon(
                        _getAccountIcon(account.accountType),
                        color: account.primary
                            ? Colors.white
                            : const Color(0xFF2E7D32),
                      ),
                    ),
                    title: Row(
                      children: [
                        Text(
                          account.accountNameLabel,
                          style: const TextStyle(fontWeight: FontWeight.w600),
                        ),
                        if (account.primary)
                          Container(
                            margin: const EdgeInsets.only(left: 8),
                            padding: const EdgeInsets.symmetric(
                              horizontal: 6,
                              vertical: 2,
                            ),
                            decoration: BoxDecoration(
                              color: const Color(0xFF2E7D32),
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: const Text(
                              'Principal',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 10,
                              ),
                            ),
                          ),
                      ],
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(account.accountNumber),
                        const SizedBox(height: 4),
                        Text(
                          account.formattedAvailableBalance,
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                            color: account.availableBalance > 0
                                ? const Color(0xFF2E7D32)
                                : Colors.grey.shade700,
                          ),
                        ),
                      ],
                    ),
                    trailing: Icon(
                      Icons.chevron_right,
                      color: Colors.grey.shade400,
                    ),
                    onTap: () {
                      context.push('/accounts/${account.id}');
                    },
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  IconData _getAccountIcon(String accountType) {
    switch (accountType) {
      case 'WALLET':
        return Icons.account_balance_wallet;
      case 'SAVINGS':
        return Icons.savings;
      case 'CHECKING':
        return Icons.account_balance;
      case 'CREDIT_CARD':
        return Icons.credit_card;
      case 'PREPAID_CARD':
        return Icons.card_giftcard;
      case 'LOAN':
        return Icons.money_off;
      default:
        return Icons.account_balance_wallet;
    }
  }
}

// ─────────────────────────────────────────────
// Create Account bottom sheet
// ─────────────────────────────────────────────
class _CreateAccountSheet extends StatefulWidget {
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

  const _CreateAccountSheet({
    required this.accountNameOptions,
    required this.accountTypeOptions,
    required this.currencyOptions,
    required this.onSubmit,
  });

  @override
  State<_CreateAccountSheet> createState() => _CreateAccountSheetState();
}

class _CreateAccountSheetState extends State<_CreateAccountSheet> {
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
              // Handle bar
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
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

              // Tipo de cuenta
              DropdownButtonFormField<String>(
                initialValue: _selectedAccountName,
                decoration: const InputDecoration(
                  labelText: 'Tipo de cuenta',
                  prefixIcon: Icon(Icons.account_balance_wallet),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(12)),
                  ),
                ),
                items: widget.accountNameOptions.entries.map((entry) {
                  return DropdownMenuItem(
                    value: entry.key,
                    child: Text(entry.value),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedAccountName = value!;
                  });
                },
              ),
              const SizedBox(height: 16),

              // Tipo de cuenta (clasificación)
              DropdownButtonFormField<String>(
                initialValue: _selectedAccountType,
                decoration: const InputDecoration(
                  labelText: 'Clasificación',
                  prefixIcon: Icon(Icons.category),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(12)),
                  ),
                ),
                items: widget.accountTypeOptions.entries.map((entry) {
                  return DropdownMenuItem(
                    value: entry.key,
                    child: Text(entry.value),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedAccountType = value!;
                  });
                },
              ),
              const SizedBox(height: 16),

              // Moneda
              DropdownButtonFormField<String>(
                initialValue: _selectedCurrency,
                decoration: const InputDecoration(
                  labelText: 'Moneda',
                  prefixIcon: Icon(Icons.currency_exchange),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(12)),
                  ),
                ),
                items: widget.currencyOptions.entries.map((entry) {
                  return DropdownMenuItem(
                    value: entry.key,
                    child: Text(entry.value),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedCurrency = value!;
                  });
                },
              ),
              const SizedBox(height: 16),

              // Alias personalizado (opcional)
              TextFormField(
                controller: _customAliasController,
                decoration: const InputDecoration(
                  labelText: 'Alias (opcional)',
                  prefixIcon: Icon(Icons.edit_note),
                  hintText: 'Ej: Mi cuenta de ahorros',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(12)),
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // Submit button
              SizedBox(
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
              ),
            ],
          ),
        ),
      ),
    );
  }
}
