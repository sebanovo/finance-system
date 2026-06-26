import '../../domain/entities/backup_record.dart';
import 'model_parsers.dart';

class BackupRecordModel {
  final String id;
  final String operationType;
  final String scope;
  final String status;
  final String? tenantSlug;
  final String? schemaName;
  final String? fileName;
  final String? format;
  final int? sizeBytes;
  final String? requestedBy;
  final String? reason;
  final String? failureReason;
  final DateTime? startedAt;
  final DateTime? finishedAt;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const BackupRecordModel({
    required this.id,
    required this.operationType,
    required this.scope,
    required this.status,
    required this.tenantSlug,
    required this.schemaName,
    required this.fileName,
    required this.format,
    required this.sizeBytes,
    required this.requestedBy,
    required this.reason,
    required this.failureReason,
    required this.startedAt,
    required this.finishedAt,
    required this.createdAt,
    required this.updatedAt,
  });

  factory BackupRecordModel.fromJson(Map<String, dynamic> json) {
    final size = json['sizeBytes'];
    return BackupRecordModel(
      id: asString(json['id']),
      operationType: asString(json['operationType']),
      scope: asString(json['scope']),
      status: asString(json['status']),
      tenantSlug: asNullableString(json['tenantSlug']),
      schemaName: asNullableString(json['schemaName']),
      fileName: asNullableString(json['fileName']),
      format: asNullableString(json['format']),
      sizeBytes: size is num ? size.toInt() : int.tryParse(size?.toString() ?? ''),
      requestedBy: asNullableString(json['requestedBy']),
      reason: asNullableString(json['reason']),
      failureReason: asNullableString(json['failureReason']),
      startedAt: asDateTime(json['startedAt']),
      finishedAt: asDateTime(json['finishedAt']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  BackupRecord toEntity() {
    return BackupRecord(
      id: id,
      operationType: operationType,
      scope: scope,
      status: status,
      tenantSlug: tenantSlug,
      schemaName: schemaName,
      fileName: fileName,
      format: format,
      sizeBytes: sizeBytes,
      requestedBy: requestedBy,
      reason: reason,
      failureReason: failureReason,
      startedAt: startedAt,
      finishedAt: finishedAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
