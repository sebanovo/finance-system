import '../entities/fx_exchange_rate.dart';
import '../repositories/fx_repository.dart';

class CreateFxRateUseCase {
  final FxRepository repository;
  CreateFxRateUseCase(this.repository);

  Future<FxExchangeRate> call({
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  }) {
    return repository.createRate(
      sourceCurrency: sourceCurrency,
      targetCurrency: targetCurrency,
      rate: rate,
      active: active,
      description: description,
    );
  }
}
