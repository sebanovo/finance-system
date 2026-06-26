import '../entities/operation_fee.dart';
import '../repositories/fx_repository.dart';

class CreateFxFeeUseCase {
  final FxRepository repository;
  CreateFxFeeUseCase(this.repository);

  Future<OperationFee> call({
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  }) {
    return repository.createFee(
      operationCode: operationCode,
      feeType: feeType,
      feeValue: feeValue,
      calculationMode: calculationMode,
      active: active,
      description: description,
    );
  }
}
