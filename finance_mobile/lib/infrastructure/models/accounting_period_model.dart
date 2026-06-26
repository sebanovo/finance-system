import '../../domain/entities/accounting_period.dart';
import 'model_parsers.dart';

class AccountingPeriodModel {
  final String id;
  final String periodCode;
  final String periodType;
  final String status;
  final DateTime? startDate;
  final DateTime? endDate;
  final DateTime? closedAt;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const AccountingPeriodModel({
    required this.id,
    required this.periodCode,
    required this.periodType,
    required this.status,
    required this.startDate,
    required this.endDate,
    required this.closedAt,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });

  factory AccountingPeriodModel.fromJson(Map<String, dynamic> json) {
    return AccountingPeriodModel(
      id: asString(json['id']),
      periodCode: asString(json['periodCode']),
      periodType: asString(json['periodType']),
      status: asString(json['status']),
      startDate: asDateTime(json['startDate']),
      endDate: asDateTime(json['endDate']),
      closedAt: asDateTime(json['closedAt']),
      description: asNullableString(json['description']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  AccountingPeriod toEntity() {
    return AccountingPeriod(
      id: id,
      periodCode: periodCode,
      periodType: periodType,
      status: status,
      startDate: startDate ?? DateTime.now(),
      endDate: endDate ?? DateTime.now(),
      closedAt: closedAt,
      description: description,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
