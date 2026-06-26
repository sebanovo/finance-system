import '../entities/fx_exchange_rate.dart';
import '../repositories/fx_repository.dart';

class GetFxRatesUseCase {
  final FxRepository repository;
  GetFxRatesUseCase(this.repository);

  Future<FxExchangeRatesPage> call({int page = 0, int size = 20}) {
    return repository.listRates(page: page, size: size);
  }
}
