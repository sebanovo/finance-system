import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/network/api_client.dart';
import '../viewmodels/accounts_viewmodel.dart';
import '../widgets/account_card.dart';
import '../widgets/account_card_skeleton.dart';
import '../widgets/create_account_sheet.dart';
import '../widgets/total_balance_card.dart';

class AccountsPage extends StatefulWidget {
  const AccountsPage({super.key});

  @override
  State<AccountsPage> createState() => _AccountsPageState();
}

class _AccountsPageState extends State<AccountsPage> {
  late AccountsViewModel _viewModel;
  late ApiClient _apiClient;
  static const int _skeletonItemCount = 3;

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
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onViewModelChanged);
    _viewModel.loadAccounts();
  }

  bool get _canCreateAccount => _apiClient.hasPermission('me.accounts.create');

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
      builder: (context) => CreateAccountSheet(
        accountNameOptions: accountNameOptions,
        accountTypeOptions: accountTypeOptions,
        currencyOptions: currencyOptions,
        onSubmit: (accountName, accountType, currency, customAlias) async {
          final navigator = Navigator.of(context);
          await _viewModel.createAccount(
            accountName: accountName,
            accountType: accountType,
            currency: currency,
            customAlias: customAlias,
          );
          if (!mounted || _viewModel.errorMessage != null) {
            return;
          }
          navigator.pop();
        },
      ),
    );
  }

  Widget _buildAccountOverview() {
    final activeCount = _viewModel.accounts.where((account) => account.active).length;
    final pendingCount = _viewModel.accounts.where((account) {
      final status = account.status.toUpperCase();
      return status.startsWith('PENDING');
    }).length;
    final restrictedCount = _viewModel.accounts.where((account) {
      final status = account.status.toUpperCase();
      return ['FROZEN', 'BLOCKED', 'SUSPENDED'].contains(status);
    }).length;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF66BB6A)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Tus cuentas',
            style: TextStyle(
              color: Colors.white,
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            'Resumen compacto de tus cuentas y saldos.',
            style: TextStyle(
              color: Colors.white.withValues(alpha: 0.9),
              fontSize: 13,
            ),
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _buildOverviewChip('Activas', '$activeCount'),
              _buildOverviewChip('Pendientes', '$pendingCount'),
              _buildOverviewChip('Restringidas', '$restrictedCount'),
              _buildOverviewChip('Total', '${_viewModel.accounts.length}'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildOverviewChip(String label, String value) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.14),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            label,
            style: const TextStyle(
              color: Colors.white70,
              fontSize: 11,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
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
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            tooltip: 'Notificaciones',
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      floatingActionButton: _canCreateAccount
          ? FloatingActionButton.extended(
              onPressed: _openCreateAccountDialog,
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              icon: const Icon(Icons.add),
              label: const Text('Nueva Cuenta'),
            )
          : null,
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (!_apiClient.hasPermission('me.accounts.list')) {
      return _buildPermissionDeniedWidget(
        'No tienes permisos para ver cuentas',
        'Tu perfil no tiene acceso a la lista de cuentas.',
      );
    }

    return RefreshIndicator(
      onRefresh: () => _viewModel.loadAccounts(),
      color: const Color(0xFF2E7D32),
      child: ListView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        children: [
          if (_viewModel.loading) ...[
            TotalBalanceCard(
              totalBalance: '0.00 BOB',
              accountsCount: 0,
              isLoading: true,
            ),
            const SizedBox(height: 16),
            ...List.generate(
              _skeletonItemCount,
              (_) => const Padding(
                padding: EdgeInsets.only(bottom: 12),
                child: AccountCardSkeleton(),
              ),
            ),
          ] else if (_viewModel.errorMessage != null) ...[
            _buildErrorWidget(),
          ] else ...[
            _buildAccountOverview(),
            const SizedBox(height: 16),
            TotalBalanceCard(
              totalBalance: _viewModel.formattedTotalBalance,
              accountsCount: _viewModel.accounts.length,
              isLoading: false,
            ),
            const SizedBox(height: 16),
            if (_viewModel.accounts.isEmpty)
              _buildEmptyWidget()
            else
              ..._viewModel.accounts.map(
                (account) => AccountCard(
                  account: account,
                  onTap: () => context.push('/accounts/${account.id}'),
                ),
              ),
          ],
        ],
      ),
    );
  }

  Widget _buildErrorWidget() {
    final height = MediaQuery.of(context).size.height * 0.7;

    return SizedBox(
      height: height,
      child: Center(
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
      ),
    );
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

  Widget _buildEmptyWidget() {
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
          if (_canCreateAccount)
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
}
