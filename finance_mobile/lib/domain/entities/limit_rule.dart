class LimitRule {
  final String id;
  final String code;
  final String name;
  final String description;
  final String limitType;
  final String scopeType;
  final String period;
  final String? transactionType;
  final String? accountType;
  final String? currency;
  final double? minAmount;
  final double? maxAmount;
  final int? maxCount;
  final bool active;
  final bool requireReviewExceed;
  final String scopeDescription;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const LimitRule({
    required this.id,
    required this.code,
    required this.name,
    required this.description,
    required this.limitType,
    required this.scopeType,
    required this.period,
    this.transactionType,
    this.accountType,
    this.currency,
    this.minAmount,
    this.maxAmount,
    this.maxCount,
    required this.active,
    required this.requireReviewExceed,
    required this.scopeDescription,
    this.createdAt,
    this.updatedAt,
  });
}

class LimitRulesPage {
  final List<LimitRule> content;
  final int totalElements;
  final int totalPages;
  final int number;
  final int size;
  final int numberOfElements;
  final bool first;
  final bool last;
  final bool empty;

  const LimitRulesPage({
    required this.content,
    required this.totalElements,
    required this.totalPages,
    required this.number,
    required this.size,
    required this.numberOfElements,
    required this.first,
    required this.last,
    required this.empty,
  });
}
