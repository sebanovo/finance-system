import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/transactions_viewmodel.dart';
import '../widgets/transaction_header_card.dart';
import '../widgets/transaction_details_card.dart';
import '../widgets/transaction_movements_card.dart';
import '../widgets/transaction_detail_skeleton.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

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

    final errorMsg = _viewModel.errorMessage;
    if (errorMsg != null && errorMsg.isNotEmpty) {
      _showSnackBar(errorMsg);
      _viewModel.clearError();

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
        backgroundColor: _surface,
        elevation: 0,
        foregroundColor: _green,
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.selectedTransaction == null) {
      return const TransactionDetailSkeleton();
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
                backgroundColor: _green,
                foregroundColor: Colors.white,
              ),
              child: const Text('Volver'),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _viewModel.loadTransactionById(widget.transactionId),
      color: _green,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TransactionHeaderCard(transaction: tx),
            const SizedBox(height: 16),
            TransactionDetailsCard(transaction: tx),
            if (tx.movements.isNotEmpty) ...[
              const SizedBox(height: 16),
              TransactionMovementsCard(movements: tx.movements),
            ],
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
