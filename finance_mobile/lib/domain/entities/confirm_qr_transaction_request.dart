class ConfirmQrTransactionRequest {
  final String sourceAccountId;
  final String idempotencyKey;

  const ConfirmQrTransactionRequest({
    required this.sourceAccountId,
    required this.idempotencyKey,
  });
}
