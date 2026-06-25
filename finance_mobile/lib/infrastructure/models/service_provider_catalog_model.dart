import '../../domain/entities/service_provider_catalog.dart';
import 'model_parsers.dart';
import 'service_customer_catalog_model.dart';
import 'service_provider_model.dart';

class ServiceProviderCatalogModel {
  final ServiceProviderModel provider;
  final List<ServiceCustomerCatalogModel> serviceCustomers;

  ServiceProviderCatalogModel({
    required this.provider,
    required this.serviceCustomers,
  });

  factory ServiceProviderCatalogModel.fromJson(Map<String, dynamic> json) {
    return ServiceProviderCatalogModel(
      provider: ServiceProviderModel.fromJson(json),
      serviceCustomers: extractMapList(json['serviceCustomers'])
          .map(ServiceCustomerCatalogModel.fromJson)
          .toList(),
    );
  }

  ServiceProviderCatalog toEntity() {
    return ServiceProviderCatalog(
      provider: provider.toEntity(),
      serviceCustomers: serviceCustomers.map((item) => item.toEntity()).toList(),
    );
  }
}
