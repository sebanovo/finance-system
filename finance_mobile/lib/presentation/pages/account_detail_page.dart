import 'package:flutter/material.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../viewmodels/accounts_viewmodel.dart';

class AccountDetailPage extends StatefulWidget {
  final String accountId;

  const AccountDetailPage({super.key, required this.accountId});

  @override
  State<AccountDetailPage> createState() => _AccountDetailPageState();
}

class _AccountDetailPageState extends State<AccountDetailPage> {
  late AccountsViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<AccountsViewModel>();
    _viewModel.addListener(_onViewModelChanged);
    _loadData();
  }

  void _loadData() {
    _viewModel.loadAccountById(widget.accountId);
    _viewModel.loadAccountBalance(widget.accountId);
    _viewModel.loadAccountTransactions(widget.accountId);
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
    final aliasController = TextEditingController(
      text: _viewModel.selectedAccount?.customAlias ?? '',
    );
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Editar alias'),
        content: TextField(
          controller: aliasController,
          decoration: const InputDecoration(
            labelText: 'Alias',
            hintText: 'Ej: Mi cuenta principal',
          ),
        ),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              await _viewModel.updateAlias(
                widget.accountId,
                aliasController.text.trim(),
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
            ),
            child: const Text('Guardar'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
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
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.selectedAccount == null) {
      return const Center(child: CircularProgressIndicator());
    }

    final account = _viewModel.selectedAccount;
    final balance = _viewModel.accountBalance;
    final transactions = _viewModel.transactions;

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

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Información de la cuenta
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  // Alias o nombre
                  Text(
                    account.customAlias?.isNotEmpty == true
                        ? account.customAlias!
                        : account.accountNameLabel,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2E7D32),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    account.accountNumber,
                    style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
                  ),
                  const SizedBox(height: 16),
                  // Saldo
                  Text(
                    balance?.formattedTotalBalance ??
                        account.formattedTotalBalance,
                    style: const TextStyle(
                      fontSize: 36,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2E7D32),
                    ),
                  ),
                  const SizedBox(height: 8),
                  // Detalle de saldo retenido
                  if ((balance?.heldBalance ?? account.heldBalance) > 0)
                    Text(
                      'Saldo retenido: ${balance?.formattedAvailableBalance ?? account.formattedAvailableBalance}',
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey.shade600,
                      ),
                    ),
                  const SizedBox(height: 16),
                  // Estado
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: account.active
                          ? const Color(0xFFE8F5E9)
                          : Colors.grey.shade200,
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      account.status,
                      style: TextStyle(
                        color: account.active
                            ? const Color(0xFF2E7D32)
                            : Colors.grey.shade600,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),
          // Información adicional
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
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
          ),
          const SizedBox(height: 24),
          // Transacciones
          Text(
            'Movimientos recientes',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: const Color(0xFF2E7D32),
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
              itemBuilder: (context, index) {
                final tx = transactions[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
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
                            : Icons.swap_horiz,
                        color: tx.isDeposit
                            ? const Color(0xFF2E7D32)
                            : tx.isWithdrawal
                            ? Colors.red.shade700
                            : Colors.grey.shade600,
                        size: 18,
                      ),
                    ),
                    title: Text(
                      tx.type,
                      style: const TextStyle(fontWeight: FontWeight.w600),
                    ),
                    subtitle: Text(
                      _formatDateTime(tx.processedAt),
                      style: const TextStyle(fontSize: 12),
                    ),
                    trailing: Text(
                      tx.isDeposit
                          ? '+${tx.formattedAmount}'
                          : tx.formattedAmount,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: tx.isDeposit
                            ? const Color(0xFF2E7D32)
                            : Colors.red.shade700,
                      ),
                    ),
                  ),
                );
              },
            ),
        ],
      ),
    );
  }

  Widget _infoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: TextStyle(color: Colors.grey.shade600)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }

  String _formatDateTime(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
