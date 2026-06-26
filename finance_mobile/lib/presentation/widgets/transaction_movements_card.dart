import 'package:flutter/material.dart';
import '../../domain/entities/transaction.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

class TransactionMovementsCard extends StatelessWidget {
  final List<Movement> movements;

  const TransactionMovementsCard({super.key, required this.movements});

  @override
  Widget build(BuildContext context) {
    return Card(
      color: _surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: const BorderSide(color: _outline),
      ),
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
                color: _ink,
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
                Expanded(
                  child: Text(
                    _friendlyMovementType(movement.movementType),
                    style: const TextStyle(fontWeight: FontWeight.w600),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                Text(
                  '${movement.movementType == 'CREDIT' ? '+' : '-'}${movement.amount.toStringAsFixed(2)} ${movement.currency}',
                    style: TextStyle(
                    color: movement.movementType == 'CREDIT'
                        ? _green
                        : Colors.red.shade700,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              '${movement.accountDisplayName} · ${movement.accountNumber}',
              style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 2),
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
                Expanded(
                  child: Text(
                    _friendlyMovementType(movement.movementType),
                    style: const TextStyle(fontWeight: FontWeight.w600),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                Text(
                  '${movement.movementType == 'CREDIT' ? '+' : '-'}${movement.amount.toStringAsFixed(2)} ${movement.currency}',
                    style: TextStyle(
                    color: movement.movementType == 'CREDIT'
                        ? _green
                        : Colors.red.shade700,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            '${movement.accountDisplayName} · ${movement.accountNumber}',
            style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 2),
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

  String _friendlyMovementType(String type) {
    switch (type) {
      case 'CREDIT':
        return 'Crédito';
      case 'DEBIT':
        return 'Débito';
      default:
        return type;
    }
  }
}
