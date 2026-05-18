class HoldRequest {
  final String accountId;
  final double amount;
  final String currency;
  final String idempotencyKey;
  final String? description;

  HoldRequest({
    required this.accountId,
    required this.amount,
    required this.currency,
    required this.idempotencyKey,
    this.description,
  });
}
