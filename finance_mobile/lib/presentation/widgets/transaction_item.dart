import 'package:flutter/material.dart';
import '../../domain/entities/transaction.dart';

class TransactionItem extends StatelessWidget {
  final Transaction transaction;
  final VoidCallback? onTap;

  const TransactionItem({super.key, required this.transaction, this.onTap});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: onTap,
        child: isSmallScreen
            ? _buildVerticalLayout()
            : _buildHorizontalLayout(),
      ),
    );
  }

  Widget _buildVerticalLayout() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              _buildIcon(),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                    Text(
                      _getTransactionTitle(),
                      style: const TextStyle(fontWeight: FontWeight.w600),
                      overflow: TextOverflow.ellipsis,
                      maxLines: 1,
                    ),
                    const SizedBox(height: 2),
                    Text(
                      _formatDateTime(transaction.processedAt),
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 12),
              _buildAmountText(),
            ],
          ),
          const SizedBox(height: 4),
          _buildAccountSummary(),
          if (transaction.description != null &&
              transaction.description!.isNotEmpty)
            Padding(
              padding: const EdgeInsets.only(top: 4),
              child: Text(
                transaction.description!,
                style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
              ),
            ),
          const SizedBox(height: 4),
          Align(alignment: Alignment.centerRight, child: _buildStatusBadge()),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: _buildIcon(),
      title: Text(
        _getTransactionTitle(),
        style: const TextStyle(fontWeight: FontWeight.w600),
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            _formatDateTime(transaction.processedAt),
            style: const TextStyle(fontSize: 12),
          ),
          const SizedBox(height: 4),
          _buildAccountSummary(),
          if (transaction.description != null &&
              transaction.description!.isNotEmpty)
            Text(
              transaction.description!,
              style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
            ),
        ],
      ),
      trailing: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          _buildAmountText(),
          const SizedBox(height: 4),
          _buildStatusBadge(),
        ],
      ),
    );
  }

  Widget _buildIcon() {
    return CircleAvatar(
      backgroundColor: transaction.isDeposit
          ? const Color(0xFFE8F5E9)
          : transaction.isWithdrawal
          ? Colors.red.shade50
          : Colors.grey.shade100,
      child: Icon(
        transaction.isDeposit
            ? Icons.arrow_downward
            : transaction.isWithdrawal
            ? Icons.arrow_upward
            : transaction.isTransfer
            ? Icons.swap_horiz
            : transaction.isHold
            ? Icons.block
            : transaction.isRelease
            ? Icons.check_circle
            : Icons.payment,
        color: transaction.isDeposit
            ? const Color(0xFF2E7D32)
            : transaction.isWithdrawal
            ? Colors.red.shade700
            : Colors.grey.shade600,
        size: 20,
      ),
    );
  }

  Widget _buildAmountText() {
    return Text(
      transaction.isDeposit
          ? '+${transaction.formattedAmount}'
          : transaction.formattedAmount,
      style: TextStyle(
        fontWeight: FontWeight.bold,
        color: transaction.isDeposit
            ? const Color(0xFF2E7D32)
            : Colors.red.shade700,
        fontSize: 16,
      ),
    );
  }

  Widget _buildAccountSummary() {
    final parts = <String>[];
    if (transaction.sourceAccountNumber != null &&
        transaction.sourceAccountNumber!.isNotEmpty) {
      parts.add('Origen: ${_accountLabel(
        transaction.sourceAccountDisplayName,
        transaction.sourceAccountNumber,
      )}');
    }
    if (transaction.targetAccountNumber != null &&
        transaction.targetAccountNumber!.isNotEmpty) {
      parts.add('Destino: ${_accountLabel(
        transaction.targetAccountDisplayName,
        transaction.targetAccountNumber,
      )}');
    }
    if (parts.isEmpty) return const SizedBox.shrink();
    return Text(
      parts.join(' · '),
      style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
    );
  }

  String _accountLabel(String? displayName, String? accountNumber) {
    final name = displayName != null && displayName.isNotEmpty
        ? displayName
        : 'Cuenta';
    if (accountNumber == null || accountNumber.isEmpty) {
      return name;
    }
    return '$name ($accountNumber)';
  }

  Widget _buildStatusBadge() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: transaction.isCompleted
            ? const Color(0xFFE8F5E9)
            : transaction.isFailed
            ? Colors.red.shade50
            : Colors.grey.shade200,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        _friendlyStatus(transaction.status),
        style: TextStyle(
          fontSize: 10,
          color: transaction.isCompleted
              ? const Color(0xFF2E7D32)
              : transaction.isFailed
              ? Colors.red.shade700
              : Colors.grey.shade600,
        ),
      ),
    );
  }

  String _getTransactionTitle() {
    if (transaction.isDeposit) return 'Depósito';
    if (transaction.isWithdrawal) return 'Retiro';
    if (transaction.isTransfer) return 'Transferencia';
    if (transaction.isHold) return 'Bloqueo de fondos';
    if (transaction.isRelease) return 'Liberación de fondos';
    if (transaction.isPayment) return 'Pago';
    return transaction.type;
  }

  String _friendlyStatus(String status) {
    switch (status) {
      case 'COMPLETED':
        return 'Completada';
      case 'PENDING':
        return 'Pendiente';
      case 'PENDING_REVIEW':
        return 'En revisión';
      case 'PROCESSING':
        return 'Procesando';
      case 'AUTHORIZED':
        return 'Autorizada';
      case 'FAILED':
        return 'Fallida';
      case 'REVERSED':
        return 'Revertida';
      case 'PARTIALLY_REFUNDED':
        return 'Reembolso parcial';
      case 'CANCELLED':
        return 'Cancelada';
      case 'EXPIRED':
        return 'Expirada';
      default:
        return status;
    }
  }

  String _formatDateTime(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}
