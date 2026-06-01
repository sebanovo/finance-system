import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../domain/entities/account.dart';
import '../viewmodels/accounts_viewmodel.dart';
import '../viewmodels/transactions_viewmodel.dart';

class CreateQrChargePage extends StatefulWidget {
  const CreateQrChargePage({super.key});

  @override
  State<CreateQrChargePage> createState() => _CreateQrChargePageState();
}

class _CreateQrChargePageState extends State<CreateQrChargePage> {
  late TransactionsViewModel _viewModel;
  late AccountsViewModel _accountsViewModel;

  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _externalReferenceController = TextEditingController();
  final _idempotencyKeyController = TextEditingController();
  String? _targetAccountId;
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

  void _onAccountsViewModelChanged() {
    if (!mounted) return;
    setState(() {});
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.qrErrorMessage != null) {
      _showSnackBar(_viewModel.qrErrorMessage!);
      _viewModel.clearQrState();
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

  String _generateIdempotencyKey() {
    return 'qr_charge_${DateTime.now().millisecondsSinceEpoch}_${DateTime.now().microsecond}';
  }

  String _getAccountDisplayText(Account account) {
    final name = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.accountNameLabel;
    return '$name - ${account.accountNumber} - ${account.formattedAvailableBalance}';
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_targetAccountId == null) {
      _showSnackBar('Selecciona una cuenta destino');
      return;
    }

    await _viewModel.createQrIntent(
      targetAccountId: _targetAccountId!,
      amount: double.parse(_amountController.text),
      currency: _selectedCurrency,
      idempotencyKey: _idempotencyKeyController.text,
      externalReference: _externalReferenceController.text.isEmpty
          ? null
          : _externalReferenceController.text,
      description: _descriptionController.text.isEmpty
          ? null
          : _descriptionController.text,
    );

    if (_viewModel.qrIntent != null) {
      _showSnackBar('Cobro por QR generado correctamente', isError: false);
    }
  }

  void _resetForm() {
    setState(() {
      _amountController.clear();
      _descriptionController.clear();
      _externalReferenceController.clear();
      _targetAccountId = null;
      _selectedCurrency = 'BOB';
      _idempotencyKeyController.text = _generateIdempotencyKey();
      _viewModel.clearQrState();
    });
  }

  @override
  Widget build(BuildContext context) {
    final qrIntent = _viewModel.qrIntent;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Cobro por QR'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Form(
                  key: _formKey,
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
                      if (_accountsViewModel.loading)
                        const Center(child: CircularProgressIndicator())
                      else if (_accountsViewModel.accounts.isEmpty)
                        const Text('No hay cuentas disponibles')
                      else
                        DropdownButtonFormField<String>(
                          initialValue: _targetAccountId,
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
                              _targetAccountId = value;
                            });
                          },
                          validator: (value) =>
                              value == null ? 'Selecciona una cuenta' : null,
                        ),
                      const SizedBox(height: 16),
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
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _externalReferenceController,
                        decoration: const InputDecoration(
                          labelText: 'Referencia externa (opcional)',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(12)),
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _descriptionController,
                        maxLines: 2,
                        decoration: const InputDecoration(
                          labelText: 'Descripción (opcional)',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(12)),
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _idempotencyKeyController,
                        decoration: const InputDecoration(
                          labelText: 'Idempotency Key',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(12)),
                          ),
                        ),
                        validator: (value) => value == null || value.isEmpty
                            ? 'Campo requerido'
                            : null,
                      ),
                      const SizedBox(height: 16),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _viewModel.qrProcessing ? null : _submit,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFF2E7D32),
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(14),
                            ),
                          ),
                          child: _viewModel.qrProcessing
                              ? const SizedBox(
                                  height: 20,
                                  width: 20,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                    color: Colors.white,
                                  ),
                                )
                              : const Text('Generar cobro por QR'),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            if (qrIntent != null) ...[
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
                        'QR listo para cobrar',
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                          fontSize: 18,
                        ),
                      ),
                      const SizedBox(height: 12),
                      Center(
                        child: Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(color: const Color(0xFFE0E0E0)),
                          ),
                          child: QrImageView(
                            data: qrIntent.qrPayload,
                            version: QrVersions.auto,
                            size: 240,
                            gapless: false,
                            backgroundColor: Colors.white,
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      _infoRow('Estado', _friendlyStatus(qrIntent.status)),
                      _infoRow('Monto', '${qrIntent.amount.toStringAsFixed(2)} ${qrIntent.currency}'),
                      _infoRow('Vence', qrIntent.expiresAt?.toIso8601String() ?? '-'),
                      _infoRow('Firma QR', _shortText(qrIntent.qrSignature)),
                      const SizedBox(height: 12),
                      const Text(
                        'Código QR',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(height: 8),
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: const Color(0xFFF7F7F7),
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(color: const Color(0xFFE0E0E0)),
                        ),
                        child: SelectableText(
                          _qrPayloadSummary(qrIntent.qrPayload),
                          textAlign: TextAlign.left,
                        ),
                      ),
                      const SizedBox(height: 16),
                      Row(
                        children: [
                          Expanded(
                            child: OutlinedButton(
                              onPressed: () async {
                              await Clipboard.setData(
                                  ClipboardData(text: qrIntent.qrPayload),
                                );
                                if (mounted) {
                                  _showSnackBar(
                                    'Código QR copiado al portapapeles',
                                    isError: false,
                                  );
                                }
                              },
                              child: const Text('Copiar código'),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: ElevatedButton(
                              onPressed: _resetForm,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF2E7D32),
                                foregroundColor: Colors.white,
                              ),
                              child: const Text('Nuevo cobro'),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 90,
            child: Text(
              label,
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
          ),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }

  String _friendlyStatus(String status) {
    switch (status) {
      case 'PENDING':
        return 'Pendiente';
      case 'CONFIRMED':
        return 'Confirmado';
      case 'CANCELLED':
        return 'Cancelado';
      case 'EXPIRED':
        return 'Expirado';
      default:
        return status;
    }
  }

  String _shortText(String value, {int max = 32}) {
    if (value.length <= max) return value;
    return '${value.substring(0, max)}...';
  }

  String _qrPayloadSummary(String payload) {
    try {
      final uri = Uri.parse(payload);
      if (uri.queryParameters['intentId'] != null) {
        return 'finance://pay?intentId=${_shortText(uri.queryParameters['intentId']!, max: 8)}';
      }
      return _shortText(payload, max: 80);
    } catch (_) {
      return _shortText(payload, max: 80);
    }
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    _accountsViewModel.removeListener(_onAccountsViewModelChanged);
    _amountController.dispose();
    _descriptionController.dispose();
    _externalReferenceController.dispose();
    _idempotencyKeyController.dispose();
    super.dispose();
  }
}
