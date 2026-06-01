import 'package:flutter/material.dart';
import '../../domain/entities/transaction.dart';

class TransactionHeaderCard extends StatelessWidget {
  final Transaction transaction;

  const TransactionHeaderCard({super.key, required this.transaction});

  @override
  Widget build(BuildContext context) {
    final isSmallScreen = MediaQuery.of(context).size.width < 500;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            _buildStatusBadge(),
            const SizedBox(height: 16),
            _buildAmount(isSmallScreen),
            const SizedBox(height: 8),
            _buildTypeLabel(),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusBadge() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: transaction.isCompleted
            ? const Color(0xFFE8F5E9)
            : transaction.isFailed
            ? Colors.red.shade50
            : Colors.grey.shade200,
        borderRadius: BorderRadius.circular(30),
      ),
      child: Text(
        _friendlyStatus(transaction.status),
        style: TextStyle(
          fontWeight: FontWeight.w600,
          color: transaction.isCompleted
              ? const Color(0xFF2E7D32)
              : transaction.isFailed
              ? Colors.red.shade700
              : Colors.grey.shade600,
        ),
      ),
    );
  }

  Widget _buildAmount(bool isSmallScreen) {
    return Text(
      transaction.formattedAmount,
      style: TextStyle(
        fontSize: isSmallScreen ? 28 : 36,
        fontWeight: FontWeight.bold,
        color: const Color(0xFF2E7D32),
      ),
    );
  }

  Widget _buildTypeLabel() {
    return Text(
      _getTransactionTypeLabel(transaction.type),
      style: TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.w500,
        color: Colors.grey.shade700,
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
}
