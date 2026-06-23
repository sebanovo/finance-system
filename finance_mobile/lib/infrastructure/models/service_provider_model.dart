import '../../domain/entities/service_provider.dart';
import 'model_parsers.dart';

class ServiceProviderModel {
  final String id;
  final String code;
  final String name;
  final String category;
  final String? serviceCustomerCodeLabel;
  final String? status;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  ServiceProviderModel({
    required this.id,
    required this.code,
    required this.name,
    required this.category,
    this.serviceCustomerCodeLabel,
    this.status,
    this.createdAt,
    this.updatedAt,
  });

  factory ServiceProviderModel.fromJson(Map<String, dynamic> json) {
    return ServiceProviderModel(
      id: asString(json['id']),
      code: asString(json['code']),
      name: asString(json['name']),
      category: asString(json['category']),
      serviceCustomerCodeLabel: asNullableString(json['serviceCustomerCodeLabel']),
      status: asNullableString(json['status']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  ServiceProvider toEntity() {
    return ServiceProvider(
      id: id,
      code: code,
      name: name,
      category: category,
      serviceCustomerCodeLabel: serviceCustomerCodeLabel,
      status: status,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
