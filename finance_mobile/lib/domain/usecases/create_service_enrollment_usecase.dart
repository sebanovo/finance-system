import '../entities/service_enrollment.dart';
import '../repositories/service_payments_repository.dart';

class CreateServiceEnrollmentUseCase {
  final ServicePaymentsRepository repository;

  CreateServiceEnrollmentUseCase(this.repository);

  Future<ServiceEnrollment> call({
    required String providerId,
    required String serviceCustomerCode,
    String? alias,
  }) {
    return repository.createServiceEnrollment(
      providerId: providerId,
      serviceCustomerCode: serviceCustomerCode,
      alias: alias,
    );
  }
}
