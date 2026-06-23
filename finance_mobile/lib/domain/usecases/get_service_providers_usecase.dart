import '../entities/service_provider.dart';
import '../repositories/service_payments_repository.dart';

class GetServiceProvidersUseCase {
  final ServicePaymentsRepository repository;

  GetServiceProvidersUseCase(this.repository);

  Future<List<ServiceProvider>> call({
    String? search,
    String? category,
    String? status,
    int page = 0,
    int size = 50,
  }) {
    return repository.getServiceProviders(
      search: search,
      category: category,
      status: status,
      page: page,
      size: size,
    );
  }
}
