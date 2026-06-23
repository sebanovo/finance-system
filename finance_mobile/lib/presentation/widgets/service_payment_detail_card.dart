import 'package:flutter/material.dart';

import '../../domain/entities/service_payment.dart';

class ServicePaymentDetailCard extends StatelessWidget {
  final ServicePayment payment;

  const ServicePaymentDetailCard({super.key, required this.payment});

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            _row(context, 'Estado', _friendlyStatus(payment.status)),
            const Divider(),
            _row(context, 'Proveedor', payment.provider.name),
            const Divider(),
            _row(context, 'Código', payment.serviceCustomerCode),
            const Divider(),
            _row(context, 'Servicio', payment.serviceCustomerName),
            const Divider(),
            _row(context, 'Periodo', payment.billingPeriod),
            const Divider(),
            _row(context, 'Monto', payment.formattedAmount),
            const Divider(),
            _row(context, 'Cuenta', payment.sourceAccountNumber),
            const Divider(),
            _row(context, 'Recibo', payment.receiptNumber),
            if (payment.paidAt != null) ...[
              const Divider(),
              _row(context, 'Fecha', _formatDateTime(payment.paidAt!)),
            ],
            if (payment.transactionId != null && payment.transactionId!.isNotEmpty) ...[
              const Divider(),
              _row(context, 'Transacción', payment.transactionId!),
            ],
          ],
        ),
      ),
    );
  }

  Widget _row(BuildContext context, String label, String value) {
    final isSmallScreen = MediaQuery.of(context).size.width < 500;

    if (isSmallScreen) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label, style: TextStyle(color: Colors.grey.shade600)),
            const SizedBox(height: 4),
            Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
          ],
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(label, style: TextStyle(color: Colors.grey.shade600)),
          ),
          Expanded(
            child: Text(value, style: const TextStyle(fontWeight: FontWeight.w500)),
          ),
        ],
      ),
    );
  }

  String _formatDateTime(DateTime date) {
    final local = date.toLocal();
    final day = local.day.toString().padLeft(2, '0');
    final month = local.month.toString().padLeft(2, '0');
    final year = local.year;
    final hour = local.hour.toString().padLeft(2, '0');
    final minute = local.minute.toString().padLeft(2, '0');
    return '$day/$month/$year $hour:$minute';
  }

  String _friendlyStatus(String status) {
    switch (status.toUpperCase()) {
      case 'COMPLETED':
        return 'Completado';
      case 'PENDING':
        return 'Pendiente';
      case 'FAILED':
        return 'Fallido';
      default:
        return status;
    }
  }
}
