import '../entities/service_payment.dart';
import '../repositories/service_payments_repository.dart';

class GetServicePaymentUseCase {
  final ServicePaymentsRepository repository;

  GetServicePaymentUseCase(this.repository);

  Future<ServicePayment> call(String paymentId) {
    return repository.getServicePayment(paymentId);
  }
}
