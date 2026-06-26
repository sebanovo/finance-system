import '../../domain/entities/fx_exchange_rate.dart';
import '../../domain/entities/operation_fee.dart';
import '../../domain/repositories/fx_repository.dart';
import '../datasources/fx_remote_datasource.dart';

class FxRepositoryImpl implements FxRepository {
  final FxRemoteDataSource remoteDataSource;
  FxRepositoryImpl(this.remoteDataSource);

  @override
  Future<FxExchangeRatesPage> listRates({int page = 0, int size = 20}) async {
    return (await remoteDataSource.listRates(page: page, size: size)).toEntity();
  }

  @override
  Future<FxExchangeRate> createRate({
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  }) async {
    return (await remoteDataSource.createRate({
      'sourceCurrency': sourceCurrency,
      'targetCurrency': targetCurrency,
      'rate': rate,
      'active': active,
      if (description != null && description.trim().isNotEmpty) 'description': description.trim(),
    })).toEntity();
  }

  @override
  Future<FxExchangeRate> updateRate(String id, {required String sourceCurrency, required String targetCurrency, required double rate, required bool active, String? description}) async {
    return (await remoteDataSource.updateRate(id, {
      'sourceCurrency': sourceCurrency,
      'targetCurrency': targetCurrency,
      'rate': rate,
      'active': active,
      if (description != null && description.trim().isNotEmpty) 'description': description.trim(),
    })).toEntity();
  }

  @override
  Future<void> deleteRate(String id) => remoteDataSource.deleteRate(id);

  @override
  Future<OperationFeesPage> listFees({int page = 0, int size = 20}) async {
    return (await remoteDataSource.listFees(page: page, size: size)).toEntity();
  }

  @override
  Future<OperationFee> createFee({required String operationCode, required String feeType, required double feeValue, required String calculationMode, required bool active, String? description}) async {
    return (await remoteDataSource.createFee({
      'operationCode': operationCode,
      'feeType': feeType,
      'feeValue': feeValue,
      'calculationMode': calculationMode,
      'active': active,
      if (description != null && description.trim().isNotEmpty) 'description': description.trim(),
    })).toEntity();
  }

  @override
  Future<OperationFee> updateFee(String id, {required String operationCode, required String feeType, required double feeValue, required String calculationMode, required bool active, String? description}) async {
    return (await remoteDataSource.updateFee(id, {
      'operationCode': operationCode,
      'feeType': feeType,
      'feeValue': feeValue,
      'calculationMode': calculationMode,
      'active': active,
      if (description != null && description.trim().isNotEmpty) 'description': description.trim(),
    })).toEntity();
  }

  @override
  Future<void> deleteFee(String id) => remoteDataSource.deleteFee(id);
}
