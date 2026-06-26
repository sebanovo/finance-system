import '../entities/fx_exchange_rate.dart';
import '../repositories/fx_repository.dart';

class UpdateFxRateUseCase {
  final FxRepository repository;
  UpdateFxRateUseCase(this.repository);

  Future<FxExchangeRate> call(
    String id, {
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  }) {
    return repository.updateRate(
      id,
      sourceCurrency: sourceCurrency,
      targetCurrency: targetCurrency,
      rate: rate,
      active: active,
      description: description,
    );
  }
}
