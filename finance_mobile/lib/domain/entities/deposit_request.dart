class DepositRequest {
  final double amount;
  final String currency;
  final String idempotencyKey;
  final String targetAccountId;
  final String method;
  final String? externalReference;
  final String? description;

  DepositRequest({
    required this.amount,
    required this.currency,
    required this.idempotencyKey,
    required this.targetAccountId,
    required this.method,
    this.externalReference,
    this.description,
  });
}
