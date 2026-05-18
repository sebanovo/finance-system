import 'package:finance_mobile/domain/entities/transaction.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/transactions_viewmodel.dart';

class TransactionsPage extends StatefulWidget {
  const TransactionsPage({super.key});

  @override
  State<TransactionsPage> createState() => _TransactionsPageState();
}

class _TransactionsPageState extends State<TransactionsPage> {
  late TransactionsViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<TransactionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadTransactions();
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.transactionCreated) {
      _viewModel.clearTransactionCreated();
      _showSnackBar('Transacción creada exitosamente', isError: false);
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
        title: const Text('Mis Movimientos'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [
          PopupMenuButton<String>(
            onSelected: (value) {
              if (value == 'deposit') {
                context.push('/transactions/deposit');
              } else if (value == 'transfer') {
                context.push('/transactions/transfer');
              } else if (value == 'withdrawal') {
                context.push('/transactions/withdrawal');
              } else if (value == 'payment') {
                context.push('/transactions/payment');
              }
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'deposit',
                child: Row(
                  children: [
                    Icon(Icons.arrow_downward, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Depositar'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'transfer',
                child: Row(
                  children: [
                    Icon(Icons.swap_horiz, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Transferir'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'withdrawal',
                child: Row(
                  children: [
                    Icon(Icons.arrow_upward, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Retirar'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'payment',
                child: Row(
                  children: [
                    Icon(Icons.payment, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Pagar'),
                  ],
                ),
              ),
            ],
          ),
        ],
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
              onPressed: () => _viewModel.loadTransactions(),
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

    if (_viewModel.transactions.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.history, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            const Text(
              'No hay movimientos',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 8),
            const Text(
              'Realiza tu primer depósito o transferencia',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _viewModel.loadTransactions(),
      color: const Color(0xFF2E7D32),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _viewModel.transactions.length,
        itemBuilder: (context, index) {
          final tx = _viewModel.transactions[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: tx.isDeposit
                    ? const Color(0xFFE8F5E9)
                    : tx.isWithdrawal
                    ? Colors.red.shade50
                    : Colors.grey.shade100,
                child: Icon(
                  tx.isDeposit
                      ? Icons.arrow_downward
                      : tx.isWithdrawal
                      ? Icons.arrow_upward
                      : tx.isTransfer
                      ? Icons.swap_horiz
                      : tx.isHold
                      ? Icons.block
                      : tx.isRelease
                      ? Icons.check_circle
                      : Icons.payment,
                  color: tx.isDeposit
                      ? const Color(0xFF2E7D32)
                      : tx.isWithdrawal
                      ? Colors.red.shade700
                      : Colors.grey.shade600,
                  size: 20,
                ),
              ),
              title: Text(
                _getTransactionTitle(tx),
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _formatDateTime(tx.processedAt),
                    style: const TextStyle(fontSize: 12),
                  ),
                  if (tx.description != null && tx.description!.isNotEmpty)
                    Text(
                      tx.description!,
                      style: TextStyle(
                        fontSize: 11,
                        color: Colors.grey.shade500,
                      ),
                    ),
                ],
              ),
              trailing: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    tx.isDeposit
                        ? '+${tx.formattedAmount}'
                        : tx.formattedAmount,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: tx.isDeposit
                          ? const Color(0xFF2E7D32)
                          : Colors.red.shade700,
                      fontSize: 16,
                    ),
                  ),
                  Container(
                    margin: const EdgeInsets.only(top: 4),
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 2,
                    ),
                    decoration: BoxDecoration(
                      color: tx.isCompleted
                          ? const Color(0xFFE8F5E9)
                          : tx.isFailed
                          ? Colors.red.shade50
                          : Colors.grey.shade200,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      tx.status,
                      style: TextStyle(
                        fontSize: 10,
                        color: tx.isCompleted
                            ? const Color(0xFF2E7D32)
                            : tx.isFailed
                            ? Colors.red.shade700
                            : Colors.grey.shade600,
                      ),
                    ),
                  ),
                ],
              ),
              onTap: () {
                context.push('/transactions/${tx.id}');
              },
            ),
          );
        },
      ),
    );
  }

  String _getTransactionTitle(Transaction tx) {
    if (tx.isDeposit) return 'Depósito';
    if (tx.isWithdrawal) return 'Retiro';
    if (tx.isTransfer) return 'Transferencia';
    if (tx.isHold) return 'Bloqueo de fondos';
    if (tx.isRelease) return 'Liberación de fondos';
    if (tx.isPayment) return 'Pago';
    return tx.type;
  }

  String _formatDateTime(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
