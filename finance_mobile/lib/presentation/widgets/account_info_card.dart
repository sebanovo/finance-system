import 'package:flutter/material.dart';
import '../../domain/entities/account.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _outline = Color(0xFFE5E7EB);
const _surfaceVariant = Color(0xFFF9FAFB);
const _ink = Color(0xFF111827);

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
      color: _surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: const BorderSide(color: _outline),
      ),
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
        color: _ink,
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
        color: _green,
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
        color: account.active ? _surfaceVariant : Colors.grey.shade200,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: _outline),
      ),
      child: Text(
        account.status,
        style: TextStyle(
          color: account.active
              ? _green
              : Colors.grey.shade600,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }
}
