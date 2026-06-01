import 'package:finance_mobile/domain/entities/limit_evaluation.dart';

class LimitEvaluationRequestModel {
  final LimitEvaluationInput input;

  const LimitEvaluationRequestModel(this.input);

  Map<String, dynamic> toJson() {
    return {
      'actorUserId': input.actorUserId,
      'sourceAccountId': input.sourceAccountId,
      'targetAccountId': input.targetAccountId,
      'sourceAccountType': input.sourceAccountType,
      'targetAccountType': input.targetAccountType,
      'sourceAvailableBalance': input.sourceAvailableBalance,
      'targetAvailableBalance': input.targetAvailableBalance,
      'transactionType': input.transactionType,
      'currency': input.currency,
      'amount': input.amount,
    };
  }
}

class LimitEvaluationModel {
  final bool allowed;
  final bool requiresReview;
  final String reason;
  final String transactionType;
  final double amount;
  final String currency;
  final List<LimitEvaluationRuleCheckModel> checks;

  const LimitEvaluationModel({
    required this.allowed,
    required this.requiresReview,
    required this.reason,
    required this.transactionType,
    required this.amount,
    required this.currency,
    required this.checks,
  });

  factory LimitEvaluationModel.fromJson(Map<String, dynamic> json) {
    return LimitEvaluationModel(
      allowed: json['allowed'] ?? false,
      requiresReview: json['requiresReview'] ?? false,
      reason: json['reason'] ?? '',
      transactionType: json['transactionType'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      currency: json['currency'] ?? '',
      checks: (json['checks'] as List<dynamic>? ?? [])
          .map((item) => LimitEvaluationRuleCheckModel.fromJson(item as Map<String, dynamic>))
          .toList(),
    );
  }

  LimitEvaluation toEntity() {
    return LimitEvaluation(
      allowed: allowed,
      requiresReview: requiresReview,
      reason: reason,
      transactionType: transactionType,
      amount: amount,
      currency: currency,
      checks: checks.map((item) => item.toEntity()).toList(),
    );
  }
}

class LimitEvaluationRuleCheckModel {
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

  const LimitEvaluationRuleCheckModel({
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

  factory LimitEvaluationRuleCheckModel.fromJson(Map<String, dynamic> json) {
    double? parseMoney(dynamic value) => value == null ? null : (value as num).toDouble();
    int? parseCount(dynamic value) => value == null ? null : (value as num).toInt();

    return LimitEvaluationRuleCheckModel(
      ruleId: json['ruleId'] ?? '',
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      limitType: json['limitType'] ?? '',
      scopeType: json['scopeType'] ?? '',
      period: json['period'] ?? '',
      matched: json['matched'] ?? false,
      allowed: json['allowed'] ?? false,
      requiresReview: json['requiresReview'] ?? false,
      reason: json['reason'] ?? '',
      currentAmount: parseMoney(json['currentAmount']),
      currentCount: parseCount(json['currentCount']),
      remainingAmount: parseMoney(json['remainingAmount']),
      remainingCount: parseCount(json['remainingCount']),
      maxAmount: parseMoney(json['maxAmount']),
      maxCount: parseCount(json['maxCount']),
    );
  }

  LimitEvaluationRuleCheck toEntity() {
    return LimitEvaluationRuleCheck(
      ruleId: ruleId,
      code: code,
      name: name,
      limitType: limitType,
      scopeType: scopeType,
      period: period,
      matched: matched,
      allowed: allowed,
      requiresReview: requiresReview,
      reason: reason,
      currentAmount: currentAmount,
      currentCount: currentCount,
      remainingAmount: remainingAmount,
      remainingCount: remainingCount,
      maxAmount: maxAmount,
      maxCount: maxCount,
    );
  }
}
