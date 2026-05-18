import '../../../domain/entities/transaction.dart';

class TransactionModel {
  final String id;
  final String type;
  final String status;
  final String channel;
  final double amount;
  final String currency;
  final String? sourceAccountId;
  final String? sourceAccountNumber;
  final String? sourceAccountDisplayName;
  final String? targetAccountId;
  final String? targetAccountNumber;
  final String? targetAccountDisplayName;
  final String? externalReference;
  final String? idempotencyKey;
  final String? description;
  final String? failureReason;
  final String? requestedByUserId;
  final String? approvedByUserId;
  final DateTime processedAt;
  final DateTime createdAt;
  final DateTime updatedAt;
  final FxDetailModel? fxDetail;
  final List<MovementModel> movements;

  TransactionModel({
    required this.id,
    required this.type,
    required this.status,
    required this.channel,
    required this.amount,
    required this.currency,
    this.sourceAccountId,
    this.sourceAccountNumber,
    this.sourceAccountDisplayName,
    this.targetAccountId,
    this.targetAccountNumber,
    this.targetAccountDisplayName,
    this.externalReference,
    this.idempotencyKey,
    this.description,
    this.failureReason,
    this.requestedByUserId,
    this.approvedByUserId,
    required this.processedAt,
    required this.createdAt,
    required this.updatedAt,
    this.fxDetail,
    required this.movements,
  });

  factory TransactionModel.fromJson(Map<String, dynamic> json) {
    return TransactionModel(
      id: json['id'] ?? '',
      type: json['type'] ?? '',
      status: json['status'] ?? '',
      channel: json['channel'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      currency: json['currency'] ?? '',
      sourceAccountId: json['sourceAccountId'],
      sourceAccountNumber: json['sourceAccountNumber'],
      sourceAccountDisplayName: json['sourceAccountDisplayName'],
      targetAccountId: json['targetAccountId'],
      targetAccountNumber: json['targetAccountNumber'],
      targetAccountDisplayName: json['targetAccountDisplayName'],
      externalReference: json['externalReference'],
      idempotencyKey: json['idempotencyKey'],
      description: json['description'],
      failureReason: json['failureReason'],
      requestedByUserId: json['requestedByUserId'],
      approvedByUserId: json['approvedByUserId'],
      processedAt: DateTime.parse(json['processedAt']),
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      fxDetail: json['fxDetail'] != null
          ? FxDetailModel.fromJson(json['fxDetail'])
          : null,
      movements: (json['movements'] as List<dynamic>? ?? [])
          .map((m) => MovementModel.fromJson(m))
          .toList(),
    );
  }

  Transaction toEntity() {
    return Transaction(
      id: id,
      type: type,
      status: status,
      channel: channel,
      amount: amount,
      currency: currency,
      sourceAccountId: sourceAccountId,
      sourceAccountNumber: sourceAccountNumber,
      sourceAccountDisplayName: sourceAccountDisplayName,
      targetAccountId: targetAccountId,
      targetAccountNumber: targetAccountNumber,
      targetAccountDisplayName: targetAccountDisplayName,
      externalReference: externalReference,
      idempotencyKey: idempotencyKey,
      description: description,
      failureReason: failureReason,
      requestedByUserId: requestedByUserId,
      approvedByUserId: approvedByUserId,
      processedAt: processedAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
      fxDetail: fxDetail?.toEntity(),
      movements: movements.map((m) => m.toEntity()).toList(),
    );
  }
}

class FxDetailModel {
  final bool applied;
  final String? operationCode;
  final String? sourceCurrency;
  final String? targetCurrency;
  final double? sourceAmount;
  final double? targetAmountGross;
  final double? targetAmountNet;
  final double? exchangeRate;
  final double? effectiveExchangeRate;
  final double? feeAmount;
  final String? feeCurrency;
  final String? feeType;
  final String? calculationMode;
  final bool feeIncludedInRate;

  FxDetailModel({
    required this.applied,
    this.operationCode,
    this.sourceCurrency,
    this.targetCurrency,
    this.sourceAmount,
    this.targetAmountGross,
    this.targetAmountNet,
    this.exchangeRate,
    this.effectiveExchangeRate,
    this.feeAmount,
    this.feeCurrency,
    this.feeType,
    this.calculationMode,
    required this.feeIncludedInRate,
  });

  factory FxDetailModel.fromJson(Map<String, dynamic> json) {
    return FxDetailModel(
      applied: json['applied'] ?? false,
      operationCode: json['operationCode'],
      sourceCurrency: json['sourceCurrency'],
      targetCurrency: json['targetCurrency'],
      sourceAmount: json['sourceAmount']?.toDouble(),
      targetAmountGross: json['targetAmountGross']?.toDouble(),
      targetAmountNet: json['targetAmountNet']?.toDouble(),
      exchangeRate: json['exchangeRate']?.toDouble(),
      effectiveExchangeRate: json['effectiveExchangeRate']?.toDouble(),
      feeAmount: json['feeAmount']?.toDouble(),
      feeCurrency: json['feeCurrency'],
      feeType: json['feeType'],
      calculationMode: json['calculationMode'],
      feeIncludedInRate: json['feeIncludedInRate'] ?? false,
    );
  }

  FxDetail toEntity() {
    return FxDetail(
      applied: applied,
      operationCode: operationCode,
      sourceCurrency: sourceCurrency,
      targetCurrency: targetCurrency,
      sourceAmount: sourceAmount,
      targetAmountGross: targetAmountGross,
      targetAmountNet: targetAmountNet,
      exchangeRate: exchangeRate,
      effectiveExchangeRate: effectiveExchangeRate,
      feeAmount: feeAmount,
      feeCurrency: feeCurrency,
      feeType: feeType,
      calculationMode: calculationMode,
      feeIncludedInRate: feeIncludedInRate,
    );
  }
}

class MovementModel {
  final String id;
  final String accountId;
  final String accountNumber;
  final String accountDisplayName;
  final String movementType;
  final double amount;
  final String currency;
  final double balanceBefore;
  final double balanceAfter;
  final String description;
  final DateTime createdAt;

  MovementModel({
    required this.id,
    required this.accountId,
    required this.accountNumber,
    required this.accountDisplayName,
    required this.movementType,
    required this.amount,
    required this.currency,
    required this.balanceBefore,
    required this.balanceAfter,
    required this.description,
    required this.createdAt,
  });

  factory MovementModel.fromJson(Map<String, dynamic> json) {
    return MovementModel(
      id: json['id'] ?? '',
      accountId: json['accountId'] ?? '',
      accountNumber: json['accountNumber'] ?? '',
      accountDisplayName: json['accountDisplayName'] ?? '',
      movementType: json['movementType'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      currency: json['currency'] ?? '',
      balanceBefore: (json['balanceBefore'] ?? 0).toDouble(),
      balanceAfter: (json['balanceAfter'] ?? 0).toDouble(),
      description: json['description'] ?? '',
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Movement toEntity() {
    return Movement(
      id: id,
      accountId: accountId,
      accountNumber: accountNumber,
      accountDisplayName: accountDisplayName,
      movementType: movementType,
      amount: amount,
      currency: currency,
      balanceBefore: balanceBefore,
      balanceAfter: balanceAfter,
      description: description,
      createdAt: createdAt,
    );
  }
}
