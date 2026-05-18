import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/transactions_viewmodel.dart';

class TransactionDetailPage extends StatefulWidget {
  final String transactionId;

  const TransactionDetailPage({super.key, required this.transactionId});

  @override
  State<TransactionDetailPage> createState() => _TransactionDetailPageState();
}

class _TransactionDetailPageState extends State<TransactionDetailPage> {
  late TransactionsViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<TransactionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadTransactionById(widget.transactionId);
  }

  void _onViewModelChanged() {
    if (!mounted) return;

    // ✅ Manejo seguro de errores
    final errorMsg = _viewModel.errorMessage;
    if (errorMsg != null && errorMsg.isNotEmpty) {
      _showSnackBar(errorMsg);
      _viewModel.clearError();

      // Si el error indica que la transacción no existe, volver atrás
      if (errorMsg.contains('no encontrada') || errorMsg.contains('404')) {
        Future.delayed(const Duration(seconds: 2), () {
          if (mounted) context.pop();
        });
      }
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalle de Movimiento'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.selectedTransaction == null) {
      return const Center(child: CircularProgressIndicator());
    }

    final tx = _viewModel.selectedTransaction;
    if (tx == null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('No se pudo cargar la transacción'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => context.pop(),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
              ),
              child: const Text('Volver'),
            ),
          ],
        ),
      );
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // Estado y tipo
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      color: tx.isCompleted
                          ? const Color(0xFFE8F5E9)
                          : tx.isFailed
                          ? Colors.red.shade50
                          : Colors.grey.shade200,
                      borderRadius: BorderRadius.circular(30),
                    ),
                    child: Text(
                      tx.status,
                      style: TextStyle(
                        fontWeight: FontWeight.w600,
                        color: tx.isCompleted
                            ? const Color(0xFF2E7D32)
                            : tx.isFailed
                            ? Colors.red.shade700
                            : Colors.grey.shade600,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    tx.formattedAmount,
                    style: const TextStyle(
                      fontSize: 36,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2E7D32),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _getTransactionTypeLabel(tx.type),
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w500,
                      color: Colors.grey.shade700,
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          // Detalles
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _detailRow('ID', tx.id),
                  const Divider(),
                  _detailRow('Canal', tx.channel),
                  if (tx.sourceAccountNumber != null) ...[
                    const Divider(),
                    _detailRow('Cuenta origen', tx.sourceAccountNumber!),
                  ],
                  if (tx.targetAccountNumber != null) ...[
                    const Divider(),
                    _detailRow('Cuenta destino', tx.targetAccountNumber!),
                  ],
                  const Divider(),
                  _detailRow('Fecha', _formatDateTime(tx.processedAt)),
                  if (tx.description != null && tx.description!.isNotEmpty) ...[
                    const Divider(),
                    _detailRow('Descripción', tx.description!),
                  ],
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          // Movimientos
          if (tx.movements.isNotEmpty)
            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Movimientos',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF2E7D32),
                      ),
                    ),
                    const SizedBox(height: 12),
                    ...tx.movements.asMap().entries.map((entry) {
                      final index = entry.key;
                      final movement = entry.value;
                      final isLast = index == tx.movements.length - 1;
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 12),
                        child: Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  movement.movementType,
                                  style: const TextStyle(
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                                Text(
                                  '${movement.movementType == 'CREDIT' ? '+' : '-'}${movement.amount.toStringAsFixed(2)} ${movement.currency}',
                                  style: TextStyle(
                                    color: movement.movementType == 'CREDIT'
                                        ? const Color(0xFF2E7D32)
                                        : Colors.red.shade700,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 4),
                            Text(
                              movement.description,
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.grey.shade600,
                              ),
                            ),
                            Text(
                              'Saldo: ${movement.balanceAfter.toStringAsFixed(2)} ${movement.currency}',
                              style: const TextStyle(fontSize: 11),
                            ),
                            if (!isLast) const Divider(),
                          ],
                        ),
                      );
                    }),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _detailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(label, style: TextStyle(color: Colors.grey.shade600)),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  String _getTransactionTypeLabel(String type) {
    switch (type) {
      case 'DEPOSIT':
        return 'Depósito';
      case 'WITHDRAWAL':
        return 'Retiro';
      case 'TRANSFER':
        return 'Transferencia';
      case 'HOLD':
        return 'Bloqueo de fondos';
      case 'RELEASE':
        return 'Liberación de fondos';
      case 'PAYMENT':
        return 'Pago';
      default:
        return type;
    }
  }

  String _formatDateTime(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
