class Transaction {
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
  final FxDetail? fxDetail;
  final List<Movement> movements;

  Transaction({
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

  String get formattedAmount {
    return '${amount.toStringAsFixed(2)} $currency';
  }

  bool get isDeposit => type == 'DEPOSIT';
  bool get isWithdrawal => type == 'WITHDRAWAL';
  bool get isTransfer => type == 'TRANSFER';
  bool get isHold => type == 'HOLD';
  bool get isRelease => type == 'RELEASE';
  bool get isCompleted => status == 'COMPLETED';
  bool get isPending => status == 'PENDING';
  bool get isFailed => status == 'FAILED';
}

class FxDetail {
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

  FxDetail({
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
}

class Movement {
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

  Movement({
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

  bool get isCredit => movementType == 'CREDIT';
  bool get isDebit => movementType == 'DEBIT';
}
