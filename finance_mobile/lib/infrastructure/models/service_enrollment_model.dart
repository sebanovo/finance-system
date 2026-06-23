import '../../domain/entities/service_enrollment.dart';
import 'model_parsers.dart';
import 'service_provider_model.dart';

class ServiceEnrollmentModel {
  final String enrollmentId;
  final ServiceProviderModel provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String? alias;
  final String status;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  ServiceEnrollmentModel({
    required this.enrollmentId,
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.alias,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
  });

  factory ServiceEnrollmentModel.fromJson(Map<String, dynamic> json) {
    return ServiceEnrollmentModel(
      enrollmentId: asString(json['enrollmentId']),
      provider: ServiceProviderModel.fromJson(
        asMap(json['provider']) ?? <String, dynamic>{},
      ),
      serviceCustomerCode: asString(json['serviceCustomerCode']),
      serviceCustomerName: asString(json['serviceCustomerName']),
      alias: asNullableString(json['alias']),
      status: asString(json['status']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  ServiceEnrollment toEntity() {
    return ServiceEnrollment(
      enrollmentId: enrollmentId,
      provider: provider.toEntity(),
      serviceCustomerCode: serviceCustomerCode,
      serviceCustomerName: serviceCustomerName,
      alias: alias,
      status: status,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
