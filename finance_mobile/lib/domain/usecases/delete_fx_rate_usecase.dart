import '../repositories/fx_repository.dart';

class DeleteFxRateUseCase {
  final FxRepository repository;
  DeleteFxRateUseCase(this.repository);

  Future<void> call(String id) => repository.deleteRate(id);
}
