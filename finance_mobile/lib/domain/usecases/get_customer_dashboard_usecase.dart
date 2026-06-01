import '../entities/customer_dashboard.dart';
import '../repositories/dashboard_repository.dart';

class GetCustomerDashboardUseCase {
  final DashboardRepository repository;

  GetCustomerDashboardUseCase(this.repository);

  Future<CustomerDashboard> call() => repository.getCustomerDashboard();
}
