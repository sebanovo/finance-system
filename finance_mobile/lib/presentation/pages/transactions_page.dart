import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/routes/app_route_observer.dart';
import '../viewmodels/transactions_viewmodel.dart';
import '../widgets/transaction_item.dart';
import '../widgets/transaction_item_skeleton.dart';
import '../widgets/transactions_empty_widget.dart';

class TransactionsPage extends StatefulWidget {
  const TransactionsPage({super.key});

  @override
  State<TransactionsPage> createState() => _TransactionsPageState();
}

class _TransactionsPageState extends State<TransactionsPage> with RouteAware {
  late TransactionsViewModel _viewModel;
  static const int _skeletonItemCount = 5;

  final List<Map<String, dynamic>> _menuItems = [
    {'value': 'deposit', 'icon': Icons.arrow_downward, 'label': 'Depositar'},
    {'value': 'transfer', 'icon': Icons.swap_horiz, 'label': 'Transferir'},
    {'value': 'withdrawal', 'icon': Icons.arrow_upward, 'label': 'Retirar'},
    {'value': 'payment', 'icon': Icons.payment, 'label': 'Pagar'},
    {'value': 'hold', 'icon': Icons.block, 'label': 'Bloquear fondos'},
    {'value': 'release', 'icon': Icons.check_circle, 'label': 'Liberar fondos'},
  ];

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<TransactionsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadTransactions();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final route = ModalRoute.of(context);
    if (route is PageRoute) {
      appRouteObserver.subscribe(this, route);
    }
  }

  @override
  void didPopNext() {
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
        actions: [_buildMenuButton()],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildMenuButton() {
    return PopupMenuButton<String>(
      onSelected: (value) => context.push('/transactions/$value'),
      itemBuilder: (context) => _menuItems.map((item) {
        return PopupMenuItem<String>(
          // ✅ Especificar tipo String
          value: item['value'] as String,
          child: Row(
            children: [
              Icon(item['icon'] as IconData, color: const Color(0xFF2E7D32)),
              const SizedBox(width: 8),
              Text(item['label'] as String),
            ],
          ),
        );
      }).toList(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading) {
      return ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _skeletonItemCount,
        itemBuilder: (context, index) => const TransactionItemSkeleton(),
      );
    }

    if (_viewModel.errorMessage != null) {
      return _buildErrorWidget();
    }

    if (_viewModel.transactions.isEmpty) {
      return const TransactionsEmptyWidget();
    }

    return RefreshIndicator(
      onRefresh: () async => _viewModel.loadTransactions(),
      color: const Color(0xFF2E7D32),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _viewModel.transactions.length,
        itemBuilder: (context, index) {
          final tx = _viewModel.transactions[index];
          return TransactionItem(
            transaction: tx,
            onTap: () => context.push('/transactions/${tx.id}'),
          );
        },
      ),
    );
  }

  Widget _buildErrorWidget() {
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

  @override
  void dispose() {
    appRouteObserver.unsubscribe(this);
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
