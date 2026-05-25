import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class AccountDetailSkeleton extends StatelessWidget {
  const AccountDetailSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    return Skeletonizer(
      enabled: true,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildInfoCardSkeleton(),
            const SizedBox(height: 24),
            _buildDetailsCardSkeleton(),
            const SizedBox(height: 24),
            _buildTransactionsHeader(),
            const SizedBox(height: 12),
            _buildTransactionSkeleton(),
            _buildTransactionSkeleton(),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCardSkeleton() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: const Padding(
        padding: EdgeInsets.all(20),
        child: Column(
          children: [
            Text(
              'Cuenta de ahorro',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text('CHK-USD-2026-000001'),
            SizedBox(height: 16),
            Text(
              '0.00 USD',
              style: TextStyle(fontSize: 36, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 16),
            _StatusBadgeSkeleton(),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailsCardSkeleton() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: const Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            _InfoRowSkeleton(
              label: 'Tipo de cuenta',
              value: 'Cuenta de ahorro',
            ),
            Divider(),
            _InfoRowSkeleton(label: 'Moneda', value: 'USD'),
            Divider(),
            _InfoRowSkeleton(label: 'Fecha de apertura', value: '10/4/2026'),
          ],
        ),
      ),
    );
  }

  Widget _buildTransactionsHeader() {
    return const Text(
      'Movimientos recientes',
      style: TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        color: Color(0xFF2E7D32),
      ),
    );
  }

  Widget _buildTransactionSkeleton() {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: const ListTile(
        leading: CircleAvatar(child: Icon(Icons.swap_horiz)),
        title: Text('Transferencia'),
        subtitle: Text('01/01/2024 12:00'),
        trailing: Text('0.00 USD'),
      ),
    );
  }
}

class _StatusBadgeSkeleton extends StatelessWidget {
  const _StatusBadgeSkeleton();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: Colors.grey.shade300,
        borderRadius: BorderRadius.circular(20),
      ),
      child: const Text('ACTIVO'),
    );
  }
}

class _InfoRowSkeleton extends StatelessWidget {
  final String label;
  final String value;

  const _InfoRowSkeleton({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.grey)),
          Text(value),
        ],
      ),
    );
  }
}
