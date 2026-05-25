import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

class TransactionDetailSkeleton extends StatelessWidget {
  const TransactionDetailSkeleton({super.key});

  @override
  Widget build(BuildContext context) {
    return Skeletonizer(
      enabled: true,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _buildHeaderSkeleton(),
            const SizedBox(height: 16),
            _buildDetailsSkeleton(),
            const SizedBox(height: 16),
            _buildMovementsSkeleton(),
          ],
        ),
      ),
    );
  }

  Widget _buildHeaderSkeleton() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: const Padding(
        padding: EdgeInsets.all(20),
        child: Column(
          children: [
            _StatusBadgeSkeleton(),
            SizedBox(height: 16),
            Text(
              '0.00 USD',
              style: TextStyle(fontSize: 36, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text('Depósito'),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailsSkeleton() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: const Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            _DetailRowSkeleton(
              label: 'ID',
              value: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
            ),
            Divider(),
            _DetailRowSkeleton(label: 'Canal', value: 'SYSTEM'),
            Divider(),
            _DetailRowSkeleton(
              label: 'Cuenta origen',
              value: 'CHK-USD-2026-000001',
            ),
            Divider(),
            _DetailRowSkeleton(
              label: 'Cuenta destino',
              value: 'CHK-USD-2026-000002',
            ),
            Divider(),
            _DetailRowSkeleton(label: 'Fecha', value: '01/01/2024 12:00'),
          ],
        ),
      ),
    );
  }

  Widget _buildMovementsSkeleton() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: const Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Movimientos'),
            SizedBox(height: 12),
            _MovementSkeleton(),
            Divider(),
            _MovementSkeleton(),
          ],
        ),
      ),
    );
  }
}

class _StatusBadgeSkeleton extends StatelessWidget {
  const _StatusBadgeSkeleton();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.grey.shade300,
        borderRadius: BorderRadius.circular(30),
      ),
      child: const Text('COMPLETED'),
    );
  }
}

class _DetailRowSkeleton extends StatelessWidget {
  final String label;
  final String value;

  const _DetailRowSkeleton({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          SizedBox(width: 100, child: Text(label)),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}

class _MovementSkeleton extends StatelessWidget {
  const _MovementSkeleton();

  @override
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.only(bottom: 12),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [Text('CREDIT'), Text('+0.00 USD')],
          ),
          SizedBox(height: 4),
          Text('Descripción del movimiento'),
          Text('Saldo: 0.00 USD'),
        ],
      ),
    );
  }
}
