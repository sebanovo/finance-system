import '../../domain/entities/service_bills_query_result.dart';
import '../../domain/entities/service_enrollment.dart';
import '../../domain/entities/service_payment.dart';
import '../../domain/entities/service_provider_catalog.dart';
import '../../domain/entities/service_provider.dart';
import '../../domain/repositories/service_payments_repository.dart';
import '../datasources/service_payments_remote_datasource.dart';

class ServicePaymentsRepositoryImpl implements ServicePaymentsRepository {
  final ServicePaymentsRemoteDataSource remoteDataSource;

  ServicePaymentsRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<ServiceProvider>> getServiceProviders({
    String? search,
    String? category,
    String? status,
    int page = 0,
    int size = 50,
  }) async {
    final models = await remoteDataSource.getServiceProviders(
      search: search,
      category: category,
      status: status,
      page: page,
      size: size,
    );
    return models.map((item) => item.toEntity()).toList();
  }

  @override
  Future<List<ServiceProviderCatalog>> getServiceProviderCatalog() async {
    final models = await remoteDataSource.getServiceProviderCatalog();
    return models.map((item) => item.toEntity()).toList();
  }

  @override
  Future<List<ServiceEnrollment>> getServiceEnrollments({
    String? providerId,
    String? category,
    String? status,
    String? search,
    int page = 0,
    int size = 50,
  }) async {
    final models = await remoteDataSource.getServiceEnrollments(
      providerId: providerId,
      category: category,
      status: status,
      search: search,
      page: page,
      size: size,
    );
    return models.map((item) => item.toEntity()).toList();
  }

  @override
  Future<ServiceEnrollment> createServiceEnrollment({
    required String providerId,
    required String serviceCustomerCode,
    String? alias,
  }) async {
    final model = await remoteDataSource.createServiceEnrollment({
      'providerId': providerId,
      'serviceCustomerCode': serviceCustomerCode,
      if (alias != null && alias.trim().isNotEmpty) 'alias': alias.trim(),
    });
    return model.toEntity();
  }

  @override
  Future<ServiceEnrollment> deleteServiceEnrollment(String enrollmentId) async {
    final model = await remoteDataSource.deleteServiceEnrollment(enrollmentId);
    return model.toEntity();
  }

  @override
  Future<ServiceBillsQueryResult> queryServiceBills({
    String? enrollmentId,
    String? providerId,
    String? serviceCustomerCode,
  }) async {
    final model = await remoteDataSource.queryServiceBills({
      if (enrollmentId != null && enrollmentId.trim().isNotEmpty)
        'enrollmentId': enrollmentId,
      if (providerId != null && providerId.trim().isNotEmpty) 'providerId': providerId,
      if (serviceCustomerCode != null && serviceCustomerCode.trim().isNotEmpty)
        'serviceCustomerCode': serviceCustomerCode.trim(),
    });
    return model.toEntity();
  }

  @override
  Future<ServicePayment> createServicePayment({
    required String sourceAccountNumber,
    required String providerId,
    required String serviceCustomerCode,
    required String billId,
    String? enrollmentId,
    required String idempotencyKey,
  }) async {
    final model = await remoteDataSource.createServicePayment({
      'sourceAccountNumber': sourceAccountNumber,
      'providerId': providerId,
      'serviceCustomerCode': serviceCustomerCode,
      'billId': billId,
      if (enrollmentId != null && enrollmentId.trim().isNotEmpty)
        'enrollmentId': enrollmentId,
      'idempotencyKey': idempotencyKey,
    });
    return model.toEntity();
  }

  @override
  Future<List<ServicePayment>> getServicePayments({
    String? providerId,
    String? receiptNumber,
    String? billId,
    int page = 0,
    int size = 50,
  }) async {
    final models = await remoteDataSource.getServicePayments(
      providerId: providerId,
      receiptNumber: receiptNumber,
      billId: billId,
      page: page,
      size: size,
    );
    return models.map((item) => item.toEntity()).toList();
  }

  @override
  Future<ServicePayment> getServicePayment(String paymentId) async {
    final model = await remoteDataSource.getServicePayment(paymentId);
    return model.toEntity();
  }
}
