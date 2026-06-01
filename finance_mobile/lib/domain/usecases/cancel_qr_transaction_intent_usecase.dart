import '../entities/qr_transaction_intent.dart';
import '../repositories/transaction_repository.dart';

class CancelQrTransactionIntentUseCase {
  final TransactionRepository repository;

  CancelQrTransactionIntentUseCase(this.repository);

  Future<QrTransactionIntent> call(String id) => repository.cancelQrIntent(id);
}
