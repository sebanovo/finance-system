import '../entities/service_enrollment.dart';
import '../repositories/service_payments_repository.dart';

class DeleteServiceEnrollmentUseCase {
  final ServicePaymentsRepository repository;

  DeleteServiceEnrollmentUseCase(this.repository);

  Future<ServiceEnrollment> call(String enrollmentId) {
    return repository.deleteServiceEnrollment(enrollmentId);
  }
}
