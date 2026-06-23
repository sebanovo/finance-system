import '../entities/service_bills_query_result.dart';
import '../repositories/service_payments_repository.dart';

class QueryServiceBillsUseCase {
  final ServicePaymentsRepository repository;

  QueryServiceBillsUseCase(this.repository);

  Future<ServiceBillsQueryResult> call({
    String? enrollmentId,
    String? providerId,
    String? serviceCustomerCode,
  }) {
    return repository.queryServiceBills(
      enrollmentId: enrollmentId,
      providerId: providerId,
      serviceCustomerCode: serviceCustomerCode,
    );
  }
}
