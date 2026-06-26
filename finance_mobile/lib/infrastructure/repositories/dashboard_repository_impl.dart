import 'package:finance_mobile/domain/entities/customer_dashboard.dart';
import 'package:finance_mobile/domain/entities/tenant_summary.dart';
import 'package:finance_mobile/domain/repositories/dashboard_repository.dart';

import '../datasources/dashboard_remote_datasource.dart';

class DashboardRepositoryImpl implements DashboardRepository {
  final DashboardRemoteDataSource remoteDataSource;

  DashboardRepositoryImpl(this.remoteDataSource);

  @override
  Future<CustomerDashboard> getCustomerDashboard() {
    return remoteDataSource.getCustomerDashboard();
  }

  @override
  Future<TenantSummary> getTenantSummary() {
    return remoteDataSource.getTenantSummary();
  }
}
