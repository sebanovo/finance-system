import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/payment_request.dart';

class CreatePaymentUseCase {
  final TransactionRepository repository;

  CreatePaymentUseCase(this.repository);

  Future<Transaction> call(PaymentRequest request) =>
      repository.createPayment(request);
}
