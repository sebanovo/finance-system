import 'package:flutter/material.dart';
import '../../domain/entities/account.dart';

class AccountInfoCard extends StatelessWidget {
  final Account account;
  final double? totalBalance;
  final double? heldBalance;

  const AccountInfoCard({
    super.key,
    required this.account,
    this.totalBalance,
    this.heldBalance,
  });

  @override
  Widget build(BuildContext context) {
    final isSmallScreen = MediaQuery.of(context).size.width < 500;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            _buildAliasOrName(isSmallScreen),
            const SizedBox(height: 8),
            _buildAccountNumber(),
            const SizedBox(height: 16),
            _buildBalance(),
            const SizedBox(height: 8),
            if ((heldBalance ?? account.heldBalance) > 0) _buildHeldBalance(),
            const SizedBox(height: 16),
            _buildStatusBadge(),
          ],
        ),
      ),
    );
  }

  Widget _buildAliasOrName(bool isSmallScreen) {
    final text = account.customAlias?.isNotEmpty == true
        ? account.customAlias!
        : account.accountNameLabel;
    return Text(
      text,
      style: TextStyle(
        fontSize: isSmallScreen ? 20 : 24,
        fontWeight: FontWeight.bold,
        color: const Color(0xFF2E7D32),
      ),
      textAlign: TextAlign.center,
    );
  }

  Widget _buildAccountNumber() {
    return Text(
      account.accountNumber,
      style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
    );
  }

  Widget _buildBalance() {
    final balance = totalBalance ?? account.totalBalance;
    return Text(
      '${balance.toStringAsFixed(2)} ${account.currency}',
      style: const TextStyle(
        fontSize: 36,
        fontWeight: FontWeight.bold,
        color: Color(0xFF2E7D32),
      ),
    );
  }

  Widget _buildHeldBalance() {
    final held = heldBalance ?? account.heldBalance;
    return Text(
      'Saldo retenido: ${held.toStringAsFixed(2)} ${account.currency}',
      style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
    );
  }

  Widget _buildStatusBadge() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: account.active ? const Color(0xFFE8F5E9) : Colors.grey.shade200,
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
    );
  }
}
