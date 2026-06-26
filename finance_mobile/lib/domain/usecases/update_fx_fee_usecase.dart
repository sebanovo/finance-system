import '../entities/operation_fee.dart';
import '../repositories/fx_repository.dart';

class UpdateFxFeeUseCase {
  final FxRepository repository;
  UpdateFxFeeUseCase(this.repository);

  Future<OperationFee> call(
    String id, {
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  }) {
    return repository.updateFee(
      id,
      operationCode: operationCode,
      feeType: feeType,
      feeValue: feeValue,
      calculationMode: calculationMode,
      active: active,
      description: description,
    );
  }
}
