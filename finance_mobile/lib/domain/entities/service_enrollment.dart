import 'service_provider.dart';

class ServiceEnrollment {
  final String enrollmentId;
  final ServiceProvider provider;
  final String serviceCustomerCode;
  final String serviceCustomerName;
  final String? alias;
  final String status;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const ServiceEnrollment({
    required this.enrollmentId,
    required this.provider,
    required this.serviceCustomerCode,
    required this.serviceCustomerName,
    required this.alias,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
  });

  String get id => enrollmentId;

  bool get isActive => status.toUpperCase() == 'ACTIVE';
}
