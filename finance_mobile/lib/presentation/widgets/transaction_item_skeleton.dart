// lib/presentation/widgets/transaction_item_skeleton.dart
import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class TransactionItemSkeleton extends StatelessWidget {
  const TransactionItemSkeleton({super.key});

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
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  CircleAvatar(child: Icon(Icons.swap_horiz)),
                  SizedBox(width: 8),
                  Text('Transferencia'),
                ],
              ),
              Text('0.00 USD'),
            ],
          ),
          SizedBox(height: 4),
          Text('01/01/2024 12:00'),
          SizedBox(height: 4),
          Align(
            alignment: Alignment.centerRight,
            child: _StatusBadgeSkeleton(),
          ),
        ],
      ),
    );
  }

  Widget _buildHorizontalLayout() {
    return const ListTile(
      leading: CircleAvatar(child: Icon(Icons.swap_horiz)),
      title: Text('Transferencia'),
      subtitle: Text('01/01/2024 12:00'),
      trailing: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [Text('0.00 USD'), _StatusBadgeSkeleton()],
      ),
    );
  }
}

class _StatusBadgeSkeleton extends StatelessWidget {
  const _StatusBadgeSkeleton();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: Colors.grey.shade300,
        borderRadius: BorderRadius.circular(12),
      ),
      child: const Text('COMPLETED', style: TextStyle(fontSize: 10)),
    );
  }
}
