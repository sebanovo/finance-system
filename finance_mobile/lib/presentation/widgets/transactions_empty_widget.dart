import 'package:flutter/material.dart';

class TransactionsEmptyWidget extends StatelessWidget {
  const TransactionsEmptyWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.history, size: 64, color: Colors.grey.shade400),
          const SizedBox(height: 16),
          const Text(
            'No hay movimientos',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
          ),
          const SizedBox(height: 8),
          const Text(
            'Realiza tu primer depósito o transferencia',
            style: TextStyle(color: Colors.grey),
          ),
        ],
      ),
    );
  }
}
