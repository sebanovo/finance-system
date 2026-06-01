import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import 'package:finance_mobile/domain/entities/account.dart';
import 'package:finance_mobile/domain/entities/account_lookup.dart';
import 'package:finance_mobile/domain/usecases/get_account_by_number_usecase.dart';
import '../viewmodels/accounts_viewmodel.dart';
import '../viewmodels/transactions_viewmodel.dart';

class CreateTransferPage extends StatefulWidget {
  const CreateTransferPage({super.key});

  @override
  State<CreateTransferPage> createState() => _CreateTransferPageState();
}

class _CreateTransferPageState extends State<CreateTransferPage> {
  late TransactionsViewModel _viewModel;
  late AccountsViewModel _accountsViewModel;
  late final GetAccountByNumberUseCase _getAccountByNumberUseCase;

  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _idempotencyKeyController = TextEditingController();
  final _targetAccountNumberController = TextEditingController();

  String? _sourceAccountId;
  AccountLookup? _resolvedTargetAccount;
  bool _resolvingTargetAccount = false;
  String? _targetLookupError;
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
    _getAccountByNumberUseCase = di.sl<GetAccountByNumberUseCase>();
    _viewModel.addListener(_onViewModelChanged);
    _accountsViewModel.addListener(_onAccountsViewModelChanged);
    _accountsViewModel.loadAccounts();
    _idempotencyKeyController.text = _generateIdempotencyKey();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.transactionCreated) {
      _viewModel.clearTransactionCreated();
      _showSnackBar('Transferencia realizada exitosamente', isError: false);
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
    return 'transfer_${DateTime.now().millisecondsSinceEpoch}_${DateTime.now().microsecond}';
  }

  String _getAccountDisplayText(Account account) {
    final name = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.accountNameLabel;
    return '$name - ${account.accountNumber} - ${account.formattedAvailableBalance}';
  }

  String _getLookupAccountDisplayText(AccountLookup account) {
    final name = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.displayName;
    return '$name - ${account.accountNumber}';
  }

  Future<void> _resolveTargetAccount({bool showFeedback = true}) async {
    final accountNumber = _targetAccountNumberController.text.trim();

    if (accountNumber.isEmpty) {
      setState(() {
        _targetLookupError = 'Ingresa el número de cuenta destino';
        _resolvedTargetAccount = null;
      });
      return;
    }

    setState(() {
      _resolvingTargetAccount = true;
      _targetLookupError = null;
      _resolvedTargetAccount = null;
    });

    try {
      final account = await _getAccountByNumberUseCase(accountNumber);
      if (!account.active) {
        if (!mounted) return;
        setState(() {
          _targetLookupError = 'La cuenta destino no está activa';
        });
        return;
      }

      if (account.id == _sourceAccountId) {
        if (!mounted) return;
        setState(() {
          _targetLookupError = 'No puedes transferir a la misma cuenta';
        });
        return;
      }

      if (!mounted) return;
      setState(() {
        _resolvedTargetAccount = account;
      });

      if (showFeedback) {
        _showSnackBar('Cuenta destino verificada', isError: false);
      }
    } catch (_) {
      if (!mounted) return;
      setState(() {
        _targetLookupError = 'No se encontró la cuenta destino';
      });
      if (showFeedback) {
        _showSnackBar('No se pudo verificar la cuenta destino');
      }
    } finally {
      if (mounted) {
        setState(() {
          _resolvingTargetAccount = false;
        });
      }
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_sourceAccountId == null) {
      _showSnackBar('Selecciona una cuenta origen');
      return;
    }

    final targetAccountNumber = _targetAccountNumberController.text.trim();
    if (targetAccountNumber.isEmpty) {
      _showSnackBar('Ingresa el número de cuenta destino');
      return;
    }

    if (_resolvedTargetAccount == null ||
        _resolvedTargetAccount!.accountNumber != targetAccountNumber) {
      await _resolveTargetAccount(showFeedback: false);
    }

    if (_resolvedTargetAccount == null) {
      _showSnackBar('Verifica la cuenta destino antes de continuar');
      return;
    }

    if (_resolvedTargetAccount!.id == _sourceAccountId) {
      _showSnackBar('No puedes transferir a la misma cuenta');
      return;
    }

    await _viewModel.createTransfer(
      amount: double.parse(_amountController.text),
      currency: _selectedCurrency,
      idempotencyKey: _idempotencyKeyController.text,
      sourceAccountId: _sourceAccountId!,
      targetAccountId: _resolvedTargetAccount!.id,
      description: _descriptionController.text.isEmpty
          ? null
          : _descriptionController.text,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Realizar Transferencia'),
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
                        'Cuenta origen',
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
                          initialValue: _sourceAccountId,
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
                          onChanged: (value) {
                            setState(() {
                              _sourceAccountId = value;
                              if (_resolvedTargetAccount?.id == value) {
                                _resolvedTargetAccount = null;
                                _targetLookupError =
                                    'No puedes transferir a la misma cuenta';
                              }
                            });
                          },
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
                        'Cuenta destino',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 12),
                      TextFormField(
                        controller: _targetAccountNumberController,
                        keyboardType: TextInputType.text,
                        textCapitalization: TextCapitalization.none,
                        decoration: const InputDecoration(
                          labelText: 'Número de cuenta destino',
                          hintText: 'Ej: 1234567890',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(12)),
                          ),
                        ),
                        onChanged: (_) {
                          if (_resolvedTargetAccount != null ||
                              _targetLookupError != null) {
                            setState(() {
                              _resolvedTargetAccount = null;
                              _targetLookupError = null;
                            });
                          }
                        },
                        validator: (value) => value == null || value.trim().isEmpty
                            ? 'Ingresa el número de cuenta destino'
                            : null,
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: OutlinedButton(
                              onPressed: _resolvingTargetAccount
                                  ? null
                                  : _resolveTargetAccount,
                              style: OutlinedButton.styleFrom(
                                foregroundColor: const Color(0xFF2E7D32),
                                side: const BorderSide(
                                  color: Color(0xFF2E7D32),
                                ),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(30),
                                ),
                              ),
                              child: _resolvingTargetAccount
                                  ? const SizedBox(
                                      height: 18,
                                      width: 18,
                                      child: CircularProgressIndicator(
                                        strokeWidth: 2,
                                      ),
                                    )
                                  : const Text('Verificar cuenta'),
                            ),
                          ),
                        ],
                      ),
                      if (_targetLookupError != null) ...[
                        const SizedBox(height: 8),
                        Text(
                          _targetLookupError!,
                          style: TextStyle(
                            color: Colors.red.shade700,
                            fontSize: 13,
                          ),
                        ),
                      ],
                      if (_resolvedTargetAccount != null) ...[
                        const SizedBox(height: 12),
                        Card(
                          color: Colors.green.shade50,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(14),
                            side: BorderSide(color: Colors.green.shade100),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(12),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  _getLookupAccountDisplayText(
                                    _resolvedTargetAccount!,
                                  ),
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                                const SizedBox(height: 6),
                                Text(
                                  '${_resolvedTargetAccount!.accountType} · ${_resolvedTargetAccount!.currency}',
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  'Estado: ${_resolvedTargetAccount!.status}',
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
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
                              items: _currencies.entries.map((entry) {
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
                          hintText: 'Motivo de la transferencia',
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
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: Colors.white,
                          ),
                        )
                      : const Text(
                          'Realizar Transferencia',
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
    _targetAccountNumberController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    _accountsViewModel.removeListener(_onAccountsViewModelChanged);
    super.dispose();
  }
}
