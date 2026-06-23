import '../entities/service_payment.dart';
import '../repositories/service_payments_repository.dart';

class GetServicePaymentsUseCase {
  final ServicePaymentsRepository repository;

  GetServicePaymentsUseCase(this.repository);

  Future<List<ServicePayment>> call({
    String? providerId,
    String? receiptNumber,
    String? billId,
    int page = 0,
    int size = 50,
  }) {
    return repository.getServicePayments(
      providerId: providerId,
      receiptNumber: receiptNumber,
      billId: billId,
      page: page,
      size: size,
    );
  }
}
