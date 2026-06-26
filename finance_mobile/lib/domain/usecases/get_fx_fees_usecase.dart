import '../entities/operation_fee.dart';
import '../repositories/fx_repository.dart';

class GetFxFeesUseCase {
  final FxRepository repository;
  GetFxFeesUseCase(this.repository);

  Future<OperationFeesPage> call({int page = 0, int size = 20}) {
    return repository.listFees(page: page, size: size);
  }
}
