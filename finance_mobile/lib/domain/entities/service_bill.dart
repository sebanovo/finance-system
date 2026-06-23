import 'service_provider.dart';

class ServiceBill {
  final String billId;
  final ServiceProvider provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String billingPeriod;
  final double amount;
  final String currency;
  final DateTime? dueDate;
  final String status;

  const ServiceBill({
    required this.billId,
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.billingPeriod,
    required this.amount,
    required this.currency,
    required this.dueDate,
    required this.status,
  });

  String get id => billId;

  bool get isPaid => status.toUpperCase() == 'PAID';
  bool get isPending => status.toUpperCase() == 'PENDING';

  String get formattedAmount => '${amount.toStringAsFixed(2)} $currency';
}
