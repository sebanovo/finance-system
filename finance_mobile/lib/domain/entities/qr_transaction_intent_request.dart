class QrTransactionIntentRequest {
  final String targetAccountId;
  final double amount;
  final String currency;
  final String idempotencyKey;
  final String? externalReference;
  final String? description;

  const QrTransactionIntentRequest({
    required this.targetAccountId,
    required this.amount,
    required this.currency,
    required this.idempotencyKey,
    this.externalReference,
    this.description,
  });
}
