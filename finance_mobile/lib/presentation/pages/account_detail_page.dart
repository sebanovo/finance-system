import 'package:flutter/material.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/accounts_viewmodel.dart';
import '../widgets/account_info_card.dart';
import '../widgets/account_detail_skeleton.dart';
import '../widgets/transaction_item.dart';
import '../widgets/edit_alias_dialog.dart';

class AccountDetailPage extends StatefulWidget {
  final String accountId;

  const AccountDetailPage({super.key, required this.accountId});

  @override
  State<AccountDetailPage> createState() => _AccountDetailPageState();
}

class _AccountDetailPageState extends State<AccountDetailPage> {
  late AccountsViewModel _viewModel;
  static const int _skeletonTransactionCount = 3;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<AccountsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _loadData();
  }

  Future<void> _loadData() async {
    await Future.wait([
      _viewModel.loadAccountById(widget.accountId),
      _viewModel.loadAccountBalance(widget.accountId),
      _viewModel.loadAccountTransactions(widget.accountId),
    ]);
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.aliasUpdated) {
      _viewModel.clearAliasUpdated();
      _showSnackBar('Alias actualizado correctamente', isError: false);
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

  void _showEditAliasDialog() {
    showDialog(
      context: context,
      builder: (context) => EditAliasDialog(
        currentAlias: _viewModel.selectedAccount?.customAlias ?? '',
        onSave: (newAlias) =>
            _viewModel.updateAlias(widget.accountId, newAlias),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isLoading = _viewModel.loading && _viewModel.selectedAccount == null;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalle de Cuenta'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: _showEditAliasDialog,
            tooltip: 'Editar alias',
          ),
        ],
      ),
      body: isLoading ? _buildLoadingBody() : _buildBody(),
    );
  }

  Widget _buildLoadingBody() {
    return SingleChildScrollView(
      child: Column(
        children: [
          const AccountDetailSkeleton(),
          const SizedBox(height: 16),
          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: _skeletonTransactionCount,
            itemBuilder: (context, index) => const AccountDetailSkeleton(),
          ),
        ],
      ),
    );
  }

  Widget _buildBody() {
    final account = _viewModel.selectedAccount;

    if (account == null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('No se pudo cargar la cuenta'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadData,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
              ),
              child: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    final balance = _viewModel.accountBalance;
    final transactions = _viewModel.transactions;

    return RefreshIndicator(
      onRefresh: () => _loadData(),
      color: const Color(0xFF2E7D32),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            AccountInfoCard(
              account: account,
              totalBalance: balance?.totalBalance,
              heldBalance: balance?.heldBalance,
            ),
            const SizedBox(height: 24),
            _buildAdditionalInfoCard(account),
            const SizedBox(height: 24),
            _buildTransactionsSection(transactions),
          ],
        ),
      ),
    );
  }

  Widget _buildAdditionalInfoCard(account) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _infoRow('Tipo de cuenta', account.accountNameLabel),
            const Divider(),
            _infoRow('Moneda', account.currency),
            const Divider(),
            _infoRow('Fecha de apertura', _formatDate(account.openedAt)),
          ],
        ),
      ),
    );
  }

  Widget _buildTransactionsSection(List transactions) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Movimientos recientes',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Color(0xFF2E7D32),
          ),
        ),
        const SizedBox(height: 12),
        if (_viewModel.loading && transactions.isEmpty)
          const Center(child: CircularProgressIndicator())
        else if (transactions.isEmpty)
          const Center(
            child: Padding(
              padding: EdgeInsets.all(32),
              child: Text('No hay movimientos registrados'),
            ),
          )
        else
          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: transactions.length,
            itemBuilder: (context, index) =>
                TransactionItem(transaction: transactions[index]),
          ),
      ],
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: TextStyle(color: Colors.grey.shade600)),
          Flexible(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
              textAlign: TextAlign.end,
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }
}
