import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/transfer_request.dart';

class CreateTransferUseCase {
  final TransactionRepository repository;

  CreateTransferUseCase(this.repository);

  Future<Transaction> call(TransferRequest request) =>
      repository.createTransfer(request);
}
