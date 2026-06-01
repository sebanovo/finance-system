import 'package:equatable/equatable.dart';

class QrTransactionIntent extends Equatable {
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

  const QrTransactionIntent({
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

  bool get isPending => status == 'PENDING';
  bool get isConfirmed => status == 'CONFIRMED';
  bool get isCancelled => status == 'CANCELLED';
  bool get isExpired => status == 'EXPIRED';

  @override
  List<Object?> get props => [
        id,
        status,
        channel,
        amount,
        currency,
        targetAccountId,
        externalReference,
        description,
        idempotencyKey,
        confirmedTransactionId,
        expiresAt,
        confirmedAt,
        cancelledAt,
        cancelledByUserId,
        payerAccountId,
        paidAmount,
        paidCurrency,
        qrPayload,
        qrSignature,
        createdAt,
        updatedAt,
      ];
}
