class BackupRecord {
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

  const BackupRecord({
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

  bool get isCompleted => status.toUpperCase() == 'COMPLETED' || status.toUpperCase() == 'RESTORED';
  bool get isRunning =>
      status.toUpperCase() == 'RUNNING' || status.toUpperCase() == 'PENDING' || status.toUpperCase() == 'RESTORING';
  bool get isFailed => status.toUpperCase() == 'FAILED' || status.toUpperCase() == 'RESTORE_FAILED';
  bool get canDownload => isCompleted || status.toUpperCase() == 'RESTORED' || status.toUpperCase() == 'RESTORED_WITH_WARNINGS';
  String get displayFileName => (fileName != null && fileName!.trim().isNotEmpty) ? fileName! : id;
}
