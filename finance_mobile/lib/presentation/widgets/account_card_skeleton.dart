import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class AccountCardSkeleton extends StatelessWidget {
  const AccountCardSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    return Skeletonizer(
      enabled: true,
      child: Card(
        margin: const EdgeInsets.only(bottom: 12),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: isSmallScreen
            ? _buildVerticalLayout()
            : _buildHorizontalLayout(),
      ),
    );
  }

  Widget _buildVerticalLayout() {
    return const Padding(
      padding: EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(child: Icon(Icons.account_balance_wallet)),
              SizedBox(width: 12),
              Expanded(child: Text('Cuenta de ejemplo')),
              Icon(Icons.chevron_right),
            ],
          ),
          SizedBox(height: 8),
          Text('CHK-USD-2026-000001'),
          SizedBox(height: 4),
          Text('0.00 BOB'),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return const ListTile(
      leading: CircleAvatar(child: Icon(Icons.account_balance_wallet)),
      title: Text('Cuenta de ejemplo'),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [Text('CHK-USD-2026-000001'), Text('0.00 BOB')],
      ),
      trailing: Icon(Icons.chevron_right),
    );
  }
}
