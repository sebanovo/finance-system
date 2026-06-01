class LimitEvaluationInput {
  final String actorUserId;
  final String? sourceAccountId;
  final String? targetAccountId;
  final String? sourceAccountType;
  final String? targetAccountType;
  final double? sourceAvailableBalance;
  final double? targetAvailableBalance;
  final String transactionType;
  final String currency;
  final double amount;

  const LimitEvaluationInput({
    required this.actorUserId,
    this.sourceAccountId,
    this.targetAccountId,
    this.sourceAccountType,
    this.targetAccountType,
    this.sourceAvailableBalance,
    this.targetAvailableBalance,
    required this.transactionType,
    required this.currency,
    required this.amount,
  });
}

class LimitEvaluation {
  final bool allowed;
  final bool requiresReview;
  final String reason;
  final String transactionType;
  final double amount;
  final String currency;
  final List<LimitEvaluationRuleCheck> checks;

  const LimitEvaluation({
    required this.allowed,
    required this.requiresReview,
    required this.reason,
    required this.transactionType,
    required this.amount,
    required this.currency,
    required this.checks,
  });
}

class LimitEvaluationRuleCheck {
  final String ruleId;
  final String code;
  final String name;
  final String limitType;
  final String scopeType;
  final String period;
  final bool matched;
  final bool allowed;
  final bool requiresReview;
  final String reason;
  final double? currentAmount;
  final int? currentCount;
  final double? remainingAmount;
  final int? remainingCount;
  final double? maxAmount;
  final int? maxCount;

  const LimitEvaluationRuleCheck({
    required this.ruleId,
    required this.code,
    required this.name,
    required this.limitType,
    required this.scopeType,
    required this.period,
    required this.matched,
    required this.allowed,
    required this.requiresReview,
    required this.reason,
    this.currentAmount,
    this.currentCount,
    this.remainingAmount,
    this.remainingCount,
    this.maxAmount,
    this.maxCount,
  });
}
