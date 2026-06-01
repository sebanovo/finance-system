import '../entities/qr_transaction_intent.dart';
import '../entities/qr_transaction_intent_request.dart';
import '../repositories/transaction_repository.dart';

class CreateQrTransactionIntentUseCase {
  final TransactionRepository repository;

  CreateQrTransactionIntentUseCase(this.repository);

  Future<QrTransactionIntent> call(QrTransactionIntentRequest request) =>
      repository.createQrIntent(request);
}
