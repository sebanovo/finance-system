// lib/presentation/widgets/total_balance_card.dart
import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class TotalBalanceCard extends StatelessWidget {
  final String totalBalance;
  final int accountsCount;
  final bool isLoading;

  const TotalBalanceCard({
    super.key,
    required this.totalBalance,
    required this.accountsCount,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    return Skeletonizer(
      enabled: isLoading,
      child: Container(
        margin: const EdgeInsets.all(16),
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: const Color(0xFF166534),
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: const Color(0xFF111827), width: 1.2),
        ),
        child: Column(
          children: [
            const Text(
              'Saldo Total',
              style: TextStyle(color: Colors.white70, fontSize: 14),
            ),
            const SizedBox(height: 8),
            Text(
              isLoading ? '0.00 BOB' : totalBalance,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 32,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              isLoading
                  ? '0 cuenta(s) activa(s)'
                  : '$accountsCount cuenta(s) activa(s)',
              style: const TextStyle(color: Colors.white70, fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
}
