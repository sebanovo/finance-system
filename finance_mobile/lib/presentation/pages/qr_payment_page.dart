import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../domain/entities/account.dart';
import '../viewmodels/accounts_viewmodel.dart';
import '../viewmodels/transactions_viewmodel.dart';

class QrPaymentPage extends StatefulWidget {
  const QrPaymentPage({super.key});

  @override
  State<QrPaymentPage> createState() => _QrPaymentPageState();
}

class _QrPaymentPageState extends State<QrPaymentPage> {
  late TransactionsViewModel _viewModel;
  late AccountsViewModel _accountsViewModel;
  final MobileScannerController _scannerController = MobileScannerController(
    detectionSpeed: DetectionSpeed.noDuplicates,
    formats: const [BarcodeFormat.qrCode],
  );

  String? _intentId;
  String? _rawPayload;
  String? _selectedSourceAccountId;
  final _idempotencyKeyController = TextEditingController();
  bool _detected = false;

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
    if (_viewModel.qrConfirmedTransaction != null) {
      _showSnackBar('Pago realizado exitosamente', isError: false);
      Future.delayed(const Duration(seconds: 2), () {
        if (mounted) context.pop();
      });
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
    return 'qr_pay_${DateTime.now().millisecondsSinceEpoch}_${DateTime.now().microsecond}';
  }

  String _getAccountDisplayText(Account account) {
    final name = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.accountNameLabel;
    return '$name - ${account.accountNumber} - ${account.formattedAvailableBalance}';
  }

  String? _extractIntentId(String rawValue) {
    try {
      final uri = Uri.parse(rawValue);
      if (uri.scheme == 'finance' &&
          uri.host == 'pay' &&
          uri.queryParameters['intentId'] != null) {
        return uri.queryParameters['intentId'];
      }
    } catch (_) {
      return null;
    }
    return null;
  }

  Future<void> _confirmPayment() async {
    if (_intentId == null || _intentId!.isEmpty) {
      _showSnackBar('Escanea un QR válido primero');
      return;
    }
    if (_selectedSourceAccountId == null) {
      _showSnackBar('Selecciona una cuenta origen');
      return;
    }

    await _viewModel.confirmQrIntent(
      intentId: _intentId!,
      sourceAccountId: _selectedSourceAccountId!,
      idempotencyKey: _idempotencyKeyController.text,
    );
  }

  void _resetScanner() {
    setState(() {
      _intentId = null;
      _rawPayload = null;
      _selectedSourceAccountId = null;
      _detected = false;
      _idempotencyKeyController.text = _generateIdempotencyKey();
      _viewModel.clearQrState();
    });
    _scannerController.start();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Pago por QR'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [
          IconButton(
            onPressed: _resetScanner,
            icon: const Icon(Icons.refresh),
          ),
        ],
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
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: SizedBox(
                  height: 320,
                  child: _detected
                      ? _buildDetectedState()
                      : MobileScanner(
                          controller: _scannerController,
                          onDetect: (capture) async {
                            if (_detected) return;
                            final rawValue = capture.barcodes.first.rawValue;
                            if (rawValue == null || rawValue.isEmpty) return;
                            final intentId = _extractIntentId(rawValue);
                            if (intentId == null) {
                              _showSnackBar('QR inválido');
                              return;
                            }
                            setState(() {
                              _detected = true;
                              _intentId = intentId;
                              _rawPayload = rawValue;
                            });
                            await _scannerController.stop();
                          },
                        ),
                ),
              ),
            ),
            const SizedBox(height: 16),
            if (_intentId != null) ...[
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
                        'QR detectado',
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                          fontSize: 18,
                        ),
                      ),
                      const SizedBox(height: 8),
                      _infoRow('Intent ID', _intentId!),
                      if (_rawPayload != null)
                        _infoRow('Código', _shortText(_rawPayload!)),
                      const SizedBox(height: 8),
                      _infoTile(
                        'QR listo para confirmar',
                        'Se detectó una intención de pago válida. Solo selecciona la cuenta origen y confirma.',
                      ),
                      const SizedBox(height: 16),
                      if (_accountsViewModel.loading)
                        const Center(child: CircularProgressIndicator())
                      else if (_accountsViewModel.accounts.isEmpty)
                        const Text('No hay cuentas disponibles')
                      else
                        DropdownButtonFormField<String>(
                          initialValue: _selectedSourceAccountId,
                          isExpanded: true,
                          decoration: const InputDecoration(
                            labelText: 'Cuenta origen',
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.all(
                                Radius.circular(12),
                              ),
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
                              _selectedSourceAccountId = value;
                            });
                          },
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
                      ),
                      if (_rawPayload != null) ...[
                        const SizedBox(height: 12),
                        Align(
                          alignment: Alignment.centerLeft,
                          child: TextButton.icon(
                            onPressed: () async {
                              await Clipboard.setData(
                                ClipboardData(text: _rawPayload!),
                              );
                              if (mounted) {
                                _showSnackBar(
                                  'Código QR copiado al portapapeles',
                                  isError: false,
                                );
                              }
                            },
                            icon: const Icon(Icons.copy),
                            label: const Text('Copiar código'),
                          ),
                        ),
                      ],
                      const SizedBox(height: 16),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _viewModel.qrProcessing ? null : _confirmPayment,
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
                              : const Text('Confirmar pago'),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ] else ...[
              const Text(
                'Apunta la cámara al QR de cobro para continuar.',
                style: TextStyle(fontSize: 16),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildDetectedState() {
    return const Center(
      child: Padding(
        padding: EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.qr_code_scanner, size: 64, color: Color(0xFF2E7D32)),
            SizedBox(height: 12),
            Text(
              'QR leído correctamente',
              textAlign: TextAlign.center,
              style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16),
            ),
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
          Expanded(child: SelectableText(value)),
        ],
      ),
    );
  }

  Widget _infoTile(String title, String message) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF1F8E9),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontWeight: FontWeight.w600,
              color: Color(0xFF2E7D32),
            ),
          ),
          const SizedBox(height: 4),
          Text(message),
        ],
      ),
    );
  }

  String _shortText(String value, {int max = 32}) {
    if (value.length <= max) return value;
    return '${value.substring(0, max)}...';
  }

  @override
  void dispose() {
    _scannerController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    _accountsViewModel.removeListener(_onAccountsViewModelChanged);
    _idempotencyKeyController.dispose();
    super.dispose();
  }
}
