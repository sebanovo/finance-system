import 'service_customer_catalog.dart';
import 'service_provider.dart';

class ServiceProviderCatalog {
  final ServiceProvider provider;
  final List<ServiceCustomerCatalog> serviceCustomers;

  const ServiceProviderCatalog({
    required this.provider,
    required this.serviceCustomers,
  });

  List<String> get serviceCustomerCodes =>
      serviceCustomers.map((item) => item.serviceCustomerCode.trim()).where((code) => code.isNotEmpty).toList();
}
