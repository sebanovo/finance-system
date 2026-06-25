import '../entities/service_bills_query_result.dart';
import '../entities/service_provider_catalog.dart';
import '../entities/service_enrollment.dart';
import '../entities/service_payment.dart';
import '../entities/service_provider.dart';

abstract class ServicePaymentsRepository {
  Future<List<ServiceProvider>> getServiceProviders({
    String? search,
    String? category,
    String? status,
    int page,
    int size,
  });

  Future<List<ServiceProviderCatalog>> getServiceProviderCatalog();

  Future<List<ServiceEnrollment>> getServiceEnrollments({
    String? providerId,
    String? category,
    String? status,
    String? search,
    int page,
    int size,
  });

  Future<ServiceEnrollment> createServiceEnrollment({
    required String providerId,
    required String serviceCustomerCode,
    String? alias,
  });

  Future<ServiceEnrollment> deleteServiceEnrollment(String enrollmentId);

  Future<ServiceBillsQueryResult> queryServiceBills({
    String? enrollmentId,
    String? providerId,
    String? serviceCustomerCode,
  });

  Future<ServicePayment> createServicePayment({
    required String sourceAccountNumber,
    required String providerId,
    required String serviceCustomerCode,
    required String billId,
    String? enrollmentId,
    required String idempotencyKey,
  });

  Future<List<ServicePayment>> getServicePayments({
    String? providerId,
    String? receiptNumber,
    String? billId,
    int page,
    int size,
  });

  Future<ServicePayment> getServicePayment(String paymentId);
}
