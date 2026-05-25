import 'package:flutter/material.dart';
import '../../domain/entities/transaction.dart';

class TransactionMovementsCard extends StatelessWidget {
  final List<Movement> movements;

  const TransactionMovementsCard({super.key, required this.movements});

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Movimientos',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Color(0xFF2E7D32),
              ),
            ),
            const SizedBox(height: 12),
            ...movements.asMap().entries.map((entry) {
              final index = entry.key;
              final movement = entry.value;
              final isLast = index == movements.length - 1;
              return _buildMovementItem(context, movement, isLast);
            }),
          ],
        ),
      ),
    );
  }

  Widget _buildMovementItem(
    BuildContext context,
    Movement movement,
    bool isLast,
  ) {
    final isSmallScreen = MediaQuery.of(context).size.width < 500;

    if (isSmallScreen) {
      return Padding(
        padding: const EdgeInsets.only(bottom: 12),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  movement.movementType,
                  style: const TextStyle(fontWeight: FontWeight.w600),
                ),
                Text(
                  '${movement.movementType == 'CREDIT' ? '+' : '-'}${movement.amount.toStringAsFixed(2)} ${movement.currency}',
                  style: TextStyle(
                    color: movement.movementType == 'CREDIT'
                        ? const Color(0xFF2E7D32)
                        : Colors.red.shade700,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              movement.description,
              style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
            ),
            Text(
              'Saldo: ${movement.balanceAfter.toStringAsFixed(2)} ${movement.currency}',
              style: const TextStyle(fontSize: 11),
            ),
            if (!isLast) const Divider(),
          ],
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                movement.movementType,
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              Text(
                '${movement.movementType == 'CREDIT' ? '+' : '-'}${movement.amount.toStringAsFixed(2)} ${movement.currency}',
                style: TextStyle(
                  color: movement.movementType == 'CREDIT'
                      ? const Color(0xFF2E7D32)
                      : Colors.red.shade700,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            movement.description,
            style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
          ),
          Text(
            'Saldo: ${movement.balanceAfter.toStringAsFixed(2)} ${movement.currency}',
            style: const TextStyle(fontSize: 11),
          ),
          if (!isLast) const Divider(),
        ],
      ),
    );
  }
}
