import 'package:flutter/material.dart';
import '../../domain/entities/account.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _surfaceVariant = Color(0xFFF9FAFB);
const _outline = Color(0xFFE5E7EB);

class AccountCard extends StatelessWidget {
  final Account account;
  final VoidCallback onTap;

  const AccountCard({super.key, required this.account, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      color: _surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: _outline),
      ),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          border: account.primary
              ? Border.all(color: _green, width: 2)
              : null,
        ),
        child: InkWell(
          borderRadius: BorderRadius.circular(16),
          onTap: onTap,
          child: isSmallScreen
              ? _buildVerticalLayout()
              : _buildHorizontalLayout(),
        ),
      ),
    );
  }

  Widget _buildVerticalLayout() {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Fila superior: Avatar + Tipo de cuenta
          Row(
            children: [
              _buildAvatar(),
              const SizedBox(width: 12),
              Expanded(
                child: Row(
                  children: [
                    Flexible(
                      child: Text(
                        account.accountNameLabel,
                        style: const TextStyle(fontWeight: FontWeight.w600),
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    if (account.primary) _buildPrimaryBadge(),
                  ],
                ),
              ),
              Icon(Icons.chevron_right, color: Colors.grey.shade400),
            ],
          ),
          const SizedBox(height: 8),
          // Número de cuenta
          Text(
            account.accountNumber,
            style: const TextStyle(fontSize: 12, color: Colors.grey),
          ),
          const SizedBox(height: 4),
          // Saldo
          Text(
            account.formattedAvailableBalance,
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 18,
              color: account.availableBalance > 0
                  ? _green
                  : Colors.grey.shade700,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return ListTile(
      leading: _buildAvatar(),
      title: Row(
        children: [
          Flexible(
            child: Text(
              account.accountNameLabel,
              style: const TextStyle(fontWeight: FontWeight.w600),
              overflow: TextOverflow.ellipsis,
            ),
          ),
          if (account.primary) _buildPrimaryBadge(),
        ],
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(account.accountNumber, style: const TextStyle(fontSize: 12)),
          const SizedBox(height: 4),
          Text(
            account.formattedAvailableBalance,
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 16,
              color: account.availableBalance > 0
                  ? _green
                  : Colors.grey.shade700,
            ),
          ),
        ],
      ),
      trailing: Icon(Icons.chevron_right, color: Colors.grey.shade400),
    );
  }

  Widget _buildAvatar() {
    return CircleAvatar(
      backgroundColor: account.primary
          ? _green
          : _surfaceVariant,
      child: Icon(
        _getAccountIcon(account.accountType),
        color: account.primary ? Colors.white : _green,
      ),
    );
  }

  Widget _buildPrimaryBadge() {
    return Container(
      margin: const EdgeInsets.only(left: 8),
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: _green,
        borderRadius: BorderRadius.circular(12),
      ),
      child: const Text(
        'Principal',
        style: TextStyle(color: Colors.white, fontSize: 10),
      ),
    );
  }

  IconData _getAccountIcon(String accountType) {
    switch (accountType) {
      case 'WALLET':
        return Icons.account_balance_wallet;
      case 'SAVINGS':
        return Icons.savings;
      case 'CHECKING':
        return Icons.account_balance;
      case 'CREDIT_CARD':
        return Icons.credit_card;
      case 'PREPAID_CARD':
        return Icons.card_giftcard;
      case 'LOAN':
        return Icons.money_off;
      default:
        return Icons.account_balance_wallet;
    }
  }
}
