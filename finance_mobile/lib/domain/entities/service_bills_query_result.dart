import 'service_bill.dart';
import 'service_provider.dart';

class ServiceBillsQueryResult {
  final ServiceProvider provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final List<ServiceBill> bills;

  const ServiceBillsQueryResult({
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.bills,
  });

  bool get hasBills => bills.isNotEmpty;
}
