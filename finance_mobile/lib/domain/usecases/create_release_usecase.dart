import '../repositories/transaction_repository.dart';
import '../entities/transaction.dart';
import '../entities/release_request.dart';

class CreateReleaseUseCase {
  final TransactionRepository repository;

  CreateReleaseUseCase(this.repository);

  Future<Transaction> call(ReleaseRequest request) =>
      repository.createRelease(request);
}
