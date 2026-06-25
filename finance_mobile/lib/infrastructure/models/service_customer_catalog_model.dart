import '../../domain/entities/service_customer_catalog.dart';
import 'model_parsers.dart';

class ServiceCustomerCatalogModel {
  final String id;
  final String serviceCustomerCode;
  final String customerName;
  final String? status;

  ServiceCustomerCatalogModel({
    required this.id,
    required this.serviceCustomerCode,
    required this.customerName,
    required this.status,
  });

  factory ServiceCustomerCatalogModel.fromJson(Map<String, dynamic> json) {
    return ServiceCustomerCatalogModel(
      id: asString(json['id']),
      serviceCustomerCode: asString(json['serviceCustomerCode']),
      customerName: asString(json['customerName']),
      status: asNullableString(json['status']),
    );
  }

  ServiceCustomerCatalog toEntity() {
    return ServiceCustomerCatalog(
      id: id,
      serviceCustomerCode: serviceCustomerCode,
      customerName: customerName,
      status: status,
    );
  }
}
