import '../../domain/entities/qr_transaction_intent.dart';

class QrTransactionIntentModel {
  final String id;
  final String status;
  final String channel;
  final double amount;
  final String currency;
  final String? targetAccountId;
  final String? externalReference;
  final String? description;
  final String? idempotencyKey;
  final String? confirmedTransactionId;
  final DateTime? expiresAt;
  final DateTime? confirmedAt;
  final DateTime? cancelledAt;
  final String? cancelledByUserId;
  final String? payerAccountId;
  final double? paidAmount;
  final String? paidCurrency;
  final String qrPayload;
  final String qrSignature;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  QrTransactionIntentModel({
    required this.id,
    required this.status,
    required this.channel,
    required this.amount,
    required this.currency,
    this.targetAccountId,
    this.externalReference,
    this.description,
    this.idempotencyKey,
    this.confirmedTransactionId,
    this.expiresAt,
    this.confirmedAt,
    this.cancelledAt,
    this.cancelledByUserId,
    this.payerAccountId,
    this.paidAmount,
    this.paidCurrency,
    required this.qrPayload,
    required this.qrSignature,
    this.createdAt,
    this.updatedAt,
  });

  factory QrTransactionIntentModel.fromJson(Map<String, dynamic> json) {
    return QrTransactionIntentModel(
      id: json['id'] ?? '',
      status: json['status'] ?? '',
      channel: json['channel'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      currency: json['currency'] ?? '',
      targetAccountId: json['targetAccountId']?.toString(),
      externalReference: json['externalReference'],
      description: json['description'],
      idempotencyKey: json['idempotencyKey'],
      confirmedTransactionId: json['confirmedTransactionId']?.toString(),
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'])
          : null,
      confirmedAt: json['confirmedAt'] != null
          ? DateTime.parse(json['confirmedAt'])
          : null,
      cancelledAt: json['cancelledAt'] != null
          ? DateTime.parse(json['cancelledAt'])
          : null,
      cancelledByUserId: json['cancelledByUserId']?.toString(),
      payerAccountId: json['payerAccountId']?.toString(),
      paidAmount: json['paidAmount'] != null
          ? (json['paidAmount'] as num).toDouble()
          : null,
      paidCurrency: json['paidCurrency'],
      qrPayload: json['qrPayload'] ?? '',
      qrSignature: json['qrSignature'] ?? '',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : null,
    );
  }

  QrTransactionIntent toEntity() {
    return QrTransactionIntent(
      id: id,
      status: status,
      channel: channel,
      amount: amount,
      currency: currency,
      targetAccountId: targetAccountId,
      externalReference: externalReference,
      description: description,
      idempotencyKey: idempotencyKey,
      confirmedTransactionId: confirmedTransactionId,
      expiresAt: expiresAt,
      confirmedAt: confirmedAt,
      cancelledAt: cancelledAt,
      cancelledByUserId: cancelledByUserId,
      payerAccountId: payerAccountId,
      paidAmount: paidAmount,
      paidCurrency: paidCurrency,
      qrPayload: qrPayload,
      qrSignature: qrSignature,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
