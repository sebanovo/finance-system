class PaymentRequest {
  final double amount;
  final String currency;
  final String idempotencyKey;
  final String sourceAccountId;
  final String method;
  final String? externalReference;
  final String? description;

  PaymentRequest({
    required this.amount,
    required this.currency,
    required this.idempotencyKey,
    required this.sourceAccountId,
    required this.method,
    this.externalReference,
    this.description,
  });
}
