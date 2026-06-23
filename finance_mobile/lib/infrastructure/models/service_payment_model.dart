import '../../domain/entities/service_payment.dart';
import 'model_parsers.dart';
import 'service_provider_model.dart';

class ServicePaymentModel {
  final String paymentId;
  final String billId;
  final String? transactionId;
  final String receiptNumber;
  final ServiceProviderModel provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String billingPeriod;
  final double amount;
  final String currency;
  final String sourceAccountNumber;
  final String status;
  final DateTime? paidAt;

  ServicePaymentModel({
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

  factory ServicePaymentModel.fromJson(Map<String, dynamic> json) {
    return ServicePaymentModel(
      paymentId: asString(json['paymentId']),
      billId: asString(json['billId']),
      transactionId: asNullableString(json['transactionId']),
      receiptNumber: asString(json['receiptNumber']),
      provider: ServiceProviderModel.fromJson(
        asMap(json['provider']) ?? <String, dynamic>{},
      ),
      serviceCustomerCode: asString(json['serviceCustomerCode']),
      serviceCustomerName: asString(json['serviceCustomerName']),
      billingPeriod: asString(json['billingPeriod']),
      amount: asDouble(json['amount']),
      currency: asString(json['currency']),
      sourceAccountNumber: asString(json['sourceAccountNumber']),
      status: asString(json['status']),
      paidAt: asDateTime(json['paidAt']),
    );
  }

  ServicePayment toEntity() {
    return ServicePayment(
      paymentId: paymentId,
      billId: billId,
      transactionId: transactionId,
      receiptNumber: receiptNumber,
      provider: provider.toEntity(),
      serviceCustomerCode: serviceCustomerCode,
      serviceCustomerName: serviceCustomerName,
      billingPeriod: billingPeriod,
      amount: amount,
      currency: currency,
      sourceAccountNumber: sourceAccountNumber,
      status: status,
      paidAt: paidAt,
    );
  }
}
