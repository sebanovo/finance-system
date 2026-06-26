import '../entities/customer_dashboard.dart';
import '../entities/tenant_summary.dart';

abstract class DashboardRepository {
  Future<CustomerDashboard> getCustomerDashboard();
  Future<TenantSummary> getTenantSummary();
}
