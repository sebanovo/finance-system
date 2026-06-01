import 'package:flutter/material.dart';
import '../../domain/entities/transaction.dart';

class TransactionDetailsCard extends StatelessWidget {
  final Transaction transaction;

  const TransactionDetailsCard({super.key, required this.transaction});

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _detailRow(context, 'Tipo', _getTransactionTypeLabel(transaction.type)),
            const Divider(),
            _detailRow(context, 'Estado', transaction.status),
            const Divider(),
            _detailRow(context, 'Canal', _friendlyChannel(transaction.channel)),
            if (transaction.sourceAccountNumber != null) ...[
              const Divider(),
              _detailRow(
                context,
                'Cuenta origen',
                _accountLabel(
                  transaction.sourceAccountDisplayName,
                  transaction.sourceAccountNumber,
                ),
              ),
            ],
            if (transaction.targetAccountNumber != null) ...[
              const Divider(),
              _detailRow(
                context,
                'Cuenta destino',
                _accountLabel(
                  transaction.targetAccountDisplayName,
                  transaction.targetAccountNumber,
                ),
              ),
            ],
            const Divider(),
            _detailRow(
              context,
              'Fecha',
              _formatDateTime(transaction.processedAt),
            ),
            if (transaction.fxDetail?.applied == true) ...[
              const Divider(),
              _detailRow(
                context,
                'Cambio',
                _formatFxSummary(transaction.fxDetail!),
              ),
            ],
            if (transaction.description != null &&
                transaction.description!.isNotEmpty) ...[
              const Divider(),
              _detailRow(context, 'Descripción', transaction.description!),
            ],
          ],
        ),
      ),
    );
  }

  Widget _detailRow(BuildContext context, String label, String value) {
    final isSmallScreen = MediaQuery.of(context).size.width < 500;

    if (isSmallScreen) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label, style: TextStyle(color: Colors.grey.shade600)),
            const SizedBox(height: 4),
            Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
          ],
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(label, style: TextStyle(color: Colors.grey.shade600)),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  String _formatDateTime(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
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

  String _friendlyChannel(String channel) {
    switch (channel) {
      case 'MANUAL':
        return 'Manual';
      case 'QR':
        return 'QR';
      case 'API':
        return 'API';
      case 'ADMIN':
        return 'Administrador';
      case 'CASHBOX':
        return 'Caja';
      case 'SCHEDULED':
        return 'Programado';
      case 'EXTERNAL_BANK':
        return 'Banco externo';
      case 'SYSTEM':
        return 'Sistema';
      default:
        return channel;
    }
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

  String _formatFxSummary(FxDetail fx) {
    final parts = <String>[];
    if (fx.sourceCurrency != null && fx.sourceCurrency!.isNotEmpty) {
      parts.add('${fx.sourceAmount?.toStringAsFixed(2) ?? '-'} ${fx.sourceCurrency}');
    }
    if (fx.targetCurrency != null && fx.targetCurrency!.isNotEmpty) {
      parts.add('${fx.targetAmountNet?.toStringAsFixed(2) ?? '-'} ${fx.targetCurrency}');
    }
    if (fx.effectiveExchangeRate != null) {
      parts.add('Tasa ${fx.effectiveExchangeRate!.toStringAsFixed(4)}');
    }
    return parts.join(' · ');
  }
}
