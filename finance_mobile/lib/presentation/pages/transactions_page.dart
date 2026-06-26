import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/routes/app_route_observer.dart';
import '../../../../core/network/api_client.dart';
import '../viewmodels/transactions_viewmodel.dart';
import '../widgets/transaction_item.dart';
import '../widgets/transaction_item_skeleton.dart';
import '../widgets/transactions_empty_widget.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _surfaceVariant = Color(0xFFF9FAFB);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

class TransactionsPage extends StatefulWidget {
  const TransactionsPage({super.key});

  @override
  State<TransactionsPage> createState() => _TransactionsPageState();
}

class _TransactionsPageState extends State<TransactionsPage> with RouteAware {
  late TransactionsViewModel _viewModel;
  late ApiClient _apiClient;
  static const int _skeletonItemCount = 5;

  final List<Map<String, dynamic>> _menuItems = [
    {
      'value': 'deposit',
      'icon': Icons.arrow_downward,
      'label': 'Depositar',
      'permission': 'me.transactions.deposit',
    },
    {
      'value': 'transfer',
      'icon': Icons.swap_horiz,
      'label': 'Transferir',
      'permission': 'me.transactions.transfer',
    },
    {
      'value': 'withdrawal',
      'icon': Icons.arrow_upward,
      'label': 'Retirar',
      'permission': 'me.transactions.withdrawal',
    },
    {
      'value': 'payment',
      'icon': Icons.payment,
      'label': 'Pagar',
      'permission': 'me.transactions.payment',
    },
    {
      'value': 'qr-charge',
      'icon': Icons.qr_code,
      'label': 'Cobro por QR',
      'permission': 'me.transactions.qr.create',
    },
    {
      'value': 'qr-pay',
      'icon': Icons.qr_code_scanner,
      'label': 'Pago por QR',
      'permission': 'me.transactions.qr.confirm',
    },
    {
      'value': 'hold',
      'icon': Icons.block,
      'label': 'Bloquear fondos',
      'permission': 'me.transactions.hold',
    },
    {
      'value': 'release',
      'icon': Icons.check_circle,
      'label': 'Liberar fondos',
      'permission': 'me.transactions.release',
    },
  ];

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<TransactionsViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadTransactions();
  }

  List<Map<String, dynamic>> get _visibleMenuItems => _menuItems
      .where((item) => _apiClient.hasPermission(item['permission'] as String))
      .toList();

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
        backgroundColor: _surface,
        elevation: 0,
        foregroundColor: _green,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            tooltip: 'Notificaciones',
            onPressed: () => context.push('/notifications'),
          ),
          _buildMenuButton(),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildMenuButton() {
    final items = _visibleMenuItems;
    if (items.isEmpty) {
      return const SizedBox.shrink();
    }

    return PopupMenuButton<String>(
      onSelected: (value) {
        final route = switch (value) {
          'qr-charge' => '/transactions/qr/charge',
          'qr-pay' => '/transactions/qr/pay',
          _ => '/transactions/$value',
        };
        context.push(route);
      },
      itemBuilder: (context) => items.map((item) {
        return PopupMenuItem<String>(
          value: item['value'] as String,
          child: Row(
            children: [
              Icon(item['icon'] as IconData, color: _green),
              const SizedBox(width: 8),
              Text(item['label'] as String),
            ],
          ),
        );
      }).toList(),
    );
  }

  Widget _buildBody() {
    if (!_apiClient.hasPermission('me.transactions.read')) {
      return _buildPermissionDeniedWidget(
        'No tienes permisos para ver movimientos',
        'Tu perfil no tiene acceso al historial de transacciones.',
      );
    }

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

    return RefreshIndicator(
      onRefresh: () async => _viewModel.loadTransactions(),
      color: _green,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildTransactionsOverview(),
          const SizedBox(height: 16),
          if (_viewModel.transactions.isEmpty)
            const TransactionsEmptyWidget()
          else
            ..._viewModel.transactions.map(
              (tx) => TransactionItem(
                transaction: tx,
                onTap: () => context.push('/transactions/${tx.id}'),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildTransactionsOverview() {
    final pending = _viewModel.transactions.where((tx) => tx.isPending).length;
    final completed = _viewModel.transactions
        .where((tx) => tx.isCompleted)
        .length;
    final failed = _viewModel.transactions.where((tx) => tx.isFailed).length;
    final total = _viewModel.transactions.length;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: _surface,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: _outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Movimientos',
            style: TextStyle(
              color: _ink,
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            'Historial resumido y acciones disponibles según tus permisos.',
            style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _buildOverviewChip('Total', '$total'),
              _buildOverviewChip('Pendientes', '$pending'),
              _buildOverviewChip('Completadas', '$completed'),
              _buildOverviewChip('Fallidas', '$failed'),
            ],
          ),
          if (_visibleMenuItems.isNotEmpty) ...[
            const SizedBox(height: 14),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: _visibleMenuItems
                  .take(4)
                  .map(
                    (item) => _buildActionChip(
                      icon: item['icon'] as IconData,
                      label: item['label'] as String,
                      onTap: () {
                        final value = item['value'] as String;
                        final route = switch (value) {
                          'qr-charge' => '/transactions/qr/charge',
                          'qr-pay' => '/transactions/qr/pay',
                          _ => '/transactions/$value',
                        };
                        context.push(route);
                      },
                    ),
                  )
                  .toList(),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildOverviewChip(String label, String value) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: _surfaceVariant,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: _outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            label,
            style: const TextStyle(
              color: Color(0xFF6B7280),
              fontSize: 11,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            value,
            style: const TextStyle(
              color: _ink,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionChip({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
  }) {
    return Material(
      color: _surface,
      borderRadius: BorderRadius.circular(16),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 18, color: _green),
              const SizedBox(width: 8),
              Text(
                label,
                style: const TextStyle(
                  color: _green,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ),
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
              backgroundColor: _green,
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

  Widget _buildPermissionDeniedWidget(String title, String message) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.lock_outline, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            Text(
              title,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              message,
              style: const TextStyle(color: Colors.grey),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
