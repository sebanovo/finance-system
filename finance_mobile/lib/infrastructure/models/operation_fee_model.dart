import '../../domain/entities/operation_fee.dart';
import 'model_parsers.dart';

class OperationFeeModel {
  final String id;
  final String operationCode;
  final String feeType;
  final double feeValue;
  final String calculationMode;
  final bool active;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const OperationFeeModel({
    required this.id,
    required this.operationCode,
    required this.feeType,
    required this.feeValue,
    required this.calculationMode,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });

  factory OperationFeeModel.fromJson(Map<String, dynamic> json) {
    return OperationFeeModel(
      id: asString(json['id']),
      operationCode: asString(json['operationCode']),
      feeType: asString(json['feeType']),
      feeValue: asDouble(json['feeValue']),
      calculationMode: asString(json['calculationMode']),
      active: json['active'] == true,
      description: asNullableString(json['description']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  OperationFee toEntity() {
    return OperationFee(
      id: id,
      operationCode: operationCode,
      feeType: feeType,
      feeValue: feeValue,
      calculationMode: calculationMode,
      active: active,
      description: description,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
