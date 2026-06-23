import '../entities/service_payment.dart';
import '../repositories/service_payments_repository.dart';

class CreateServicePaymentUseCase {
  final ServicePaymentsRepository repository;

  CreateServicePaymentUseCase(this.repository);

  Future<ServicePayment> call({
    required String sourceAccountNumber,
    required String providerId,
    required String serviceCustomerCode,
    required String billId,
    String? enrollmentId,
    required String idempotencyKey,
  }) {
    return repository.createServicePayment(
      sourceAccountNumber: sourceAccountNumber,
      providerId: providerId,
      serviceCustomerCode: serviceCustomerCode,
      billId: billId,
      enrollmentId: enrollmentId,
      idempotencyKey: idempotencyKey,
    );
  }
}
