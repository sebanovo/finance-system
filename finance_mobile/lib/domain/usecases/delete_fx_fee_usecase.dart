import '../repositories/fx_repository.dart';

class DeleteFxFeeUseCase {
  final FxRepository repository;
  DeleteFxFeeUseCase(this.repository);

  Future<void> call(String id) => repository.deleteFee(id);
}
