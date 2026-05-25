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
            _detailRow(context, 'ID', transaction.id),
            const Divider(),
            _detailRow(context, 'Canal', transaction.channel),
            if (transaction.sourceAccountNumber != null) ...[
              const Divider(),
              _detailRow(
                context,
                'Cuenta origen',
                transaction.sourceAccountNumber!,
              ),
            ],
            if (transaction.targetAccountNumber != null) ...[
              const Divider(),
              _detailRow(
                context,
                'Cuenta destino',
                transaction.targetAccountNumber!,
              ),
            ],
            const Divider(),
            _detailRow(
              context,
              'Fecha',
              _formatDateTime(transaction.processedAt),
            ),
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
}
