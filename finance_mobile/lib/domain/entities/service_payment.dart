import 'service_provider.dart';

class ServicePayment {
  final String paymentId;
  final String billId;
  final String? transactionId;
  final String receiptNumber;
  final ServiceProvider provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String billingPeriod;
  final double amount;
  final String currency;
  final String sourceAccountNumber;
  final String status;
  final DateTime? paidAt;

  const ServicePayment({
    required this.paymentId,
    required this.billId,
    required this.transactionId,
    required this.receiptNumber,
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.billingPeriod,
    required this.amount,
    required this.currency,
    required this.sourceAccountNumber,
    required this.status,
    required this.paidAt,
  });

  String get id => paymentId;

  bool get isCompleted => status.toUpperCase() == 'COMPLETED';
  bool get isFailed => status.toUpperCase() == 'FAILED';

  String get formattedAmount => '${amount.toStringAsFixed(2)} $currency';
}
