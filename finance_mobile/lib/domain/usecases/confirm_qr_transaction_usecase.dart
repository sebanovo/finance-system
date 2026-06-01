import '../entities/confirm_qr_transaction_request.dart';
import '../entities/transaction.dart';
import '../repositories/transaction_repository.dart';

class ConfirmQrTransactionUseCase {
  final TransactionRepository repository;

  ConfirmQrTransactionUseCase(this.repository);

  Future<Transaction> call(String intentId, ConfirmQrTransactionRequest request) =>
      repository.confirmQrTransaction(intentId, request);
}
