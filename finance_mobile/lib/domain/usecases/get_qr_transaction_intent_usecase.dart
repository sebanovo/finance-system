import '../entities/qr_transaction_intent.dart';
import '../repositories/transaction_repository.dart';

class GetQrTransactionIntentUseCase {
  final TransactionRepository repository;

  GetQrTransactionIntentUseCase(this.repository);

  Future<QrTransactionIntent> call(String id) => repository.getQrIntent(id);
}
