import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
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
      builder: (context) => CreateAccountSheet(
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
          if (mounted && _viewModel.errorMessage == null)
            Navigator.of(context).pop();
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
      return Column(
        children: [
          TotalBalanceCard(
            totalBalance: '0.00 BOB',
            accountsCount: 0,
            isLoading: true,
          ),
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _skeletonItemCount,
              itemBuilder: (context, index) => const AccountCardSkeleton(),
            ),
          ),
        ],
      );
    }

    if (_viewModel.errorMessage != null) {
      return _buildErrorWidget();
    }

    if (_viewModel.accounts.isEmpty) {
      return _buildEmptyWidget();
    }

    return Column(
      children: [
        TotalBalanceCard(
          totalBalance: _viewModel.formattedTotalBalance,
          accountsCount: _viewModel.accounts.length,
          isLoading: false,
        ),
        Expanded(
          child: RefreshIndicator(
            onRefresh: () => _viewModel.loadAccounts(),
            color: const Color(0xFF2E7D32),
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _viewModel.accounts.length,
              itemBuilder: (context, index) {
                final account = _viewModel.accounts[index];
                return AccountCard(
                  account: account,
                  onTap: () => context.push('/accounts/${account.id}'),
                );
              },
            ),
          ),
        ),
      ],
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
