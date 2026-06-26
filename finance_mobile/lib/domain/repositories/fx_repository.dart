import '../entities/fx_exchange_rate.dart';
import '../entities/operation_fee.dart';

abstract class FxRepository {
  Future<FxExchangeRatesPage> listRates({int page = 0, int size = 20});
  Future<FxExchangeRate> createRate({
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  });
  Future<FxExchangeRate> updateRate(
    String id, {
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  });
  Future<void> deleteRate(String id);

  Future<OperationFeesPage> listFees({int page = 0, int size = 20});
  Future<OperationFee> createFee({
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  });
  Future<OperationFee> updateFee(
    String id, {
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  });
  Future<void> deleteFee(String id);
}
