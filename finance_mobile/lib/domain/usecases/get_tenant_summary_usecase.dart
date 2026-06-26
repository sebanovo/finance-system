import '../entities/tenant_summary.dart';
import '../repositories/dashboard_repository.dart';

class GetTenantSummaryUseCase {
  final DashboardRepository repository;

  GetTenantSummaryUseCase(this.repository);

  Future<TenantSummary> call() => repository.getTenantSummary();
}
