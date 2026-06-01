import '../entities/customer_dashboard.dart';

abstract class DashboardRepository {
  Future<CustomerDashboard> getCustomerDashboard();
}
