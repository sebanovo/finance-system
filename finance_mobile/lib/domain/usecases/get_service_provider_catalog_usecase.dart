import '../entities/service_provider_catalog.dart';
import '../repositories/service_payments_repository.dart';

class GetServiceProviderCatalogUseCase {
  final ServicePaymentsRepository repository;

  GetServiceProviderCatalogUseCase(this.repository);

  Future<List<ServiceProviderCatalog>> call() {
    return repository.getServiceProviderCatalog();
  }
}
