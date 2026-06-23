import '../entities/service_enrollment.dart';
import '../repositories/service_payments_repository.dart';

class GetServiceEnrollmentsUseCase {
  final ServicePaymentsRepository repository;

  GetServiceEnrollmentsUseCase(this.repository);

  Future<List<ServiceEnrollment>> call({
    String? providerId,
    String? category,
    String? status,
    String? search,
    int page = 0,
    int size = 50,
  }) {
    return repository.getServiceEnrollments(
      providerId: providerId,
      category: category,
      status: status,
      search: search,
      page: page,
      size: size,
    );
  }
}
