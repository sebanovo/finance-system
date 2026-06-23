import '../../domain/entities/service_bill.dart';
import '../../domain/entities/service_bills_query_result.dart';
import 'model_parsers.dart';
import 'service_provider_model.dart';

class ServiceBillModel {
  final String billId;
  final ServiceProviderModel provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String billingPeriod;
  final double amount;
  final String currency;
  final DateTime? dueDate;
  final String status;

  ServiceBillModel({
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

  factory ServiceBillModel.fromJson(Map<String, dynamic> json) {
    return ServiceBillModel(
      billId: asString(json['billId']),
      provider: ServiceProviderModel.fromJson(
        asMap(json['provider']) ?? <String, dynamic>{},
      ),
      serviceCustomerCode: asString(json['serviceCustomerCode']),
      serviceCustomerName: asString(json['serviceCustomerName']),
      billingPeriod: asString(json['billingPeriod']),
      amount: asDouble(json['amount']),
      currency: asString(json['currency']),
      dueDate: asDateTime(json['dueDate']),
      status: asString(json['status']),
    );
  }

  ServiceBill toEntity() {
    return ServiceBill(
      billId: billId,
      provider: provider.toEntity(),
      serviceCustomerCode: serviceCustomerCode,
      serviceCustomerName: serviceCustomerName,
      billingPeriod: billingPeriod,
      amount: amount,
      currency: currency,
      dueDate: dueDate,
      status: status,
    );
  }
}

class ServiceBillsQueryResultModel {
  final ServiceProviderModel provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final List<ServiceBillModel> bills;

  ServiceBillsQueryResultModel({
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.bills,
  });

  factory ServiceBillsQueryResultModel.fromJson(Map<String, dynamic> json) {
    final bills = extractMapList(json['bills'])
        .map(ServiceBillModel.fromJson)
        .toList();

    return ServiceBillsQueryResultModel(
      provider: ServiceProviderModel.fromJson(
        asMap(json['provider']) ?? <String, dynamic>{},
      ),
      serviceCustomerCode: asString(json['serviceCustomerCode']),
      serviceCustomerName: asString(json['serviceCustomerName']),
      bills: bills,
    );
  }

  ServiceBillsQueryResult toEntity() {
    return ServiceBillsQueryResult(
      provider: provider.toEntity(),
      serviceCustomerCode: serviceCustomerCode,
      serviceCustomerName: serviceCustomerName,
      bills: bills.map((item) => item.toEntity()).toList(),
    );
  }
}
