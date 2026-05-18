class TransferRequest {
  final double amount;
  final String currency;
  final String idempotencyKey;
  final String sourceAccountId;
  final String targetAccountId;
  final String? description;

  TransferRequest({
    required this.amount,
    required this.currency,
    required this.idempotencyKey,
    required this.sourceAccountId,
    required this.targetAccountId,
    this.description,
  });
}
