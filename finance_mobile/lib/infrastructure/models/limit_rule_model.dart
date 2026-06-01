import 'package:finance_mobile/domain/entities/limit_rule.dart';

class LimitRuleModel {
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

  const LimitRuleModel({
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

  factory LimitRuleModel.fromJson(Map<String, dynamic> json) {
    double? parseMoney(dynamic value) => value == null ? null : (value as num).toDouble();
    int? parseCount(dynamic value) => value == null ? null : (value as num).toInt();

    DateTime? parseDate(dynamic value) {
      if (value == null) return null;
      return DateTime.parse(value.toString());
    }

    return LimitRuleModel(
      id: json['id'] ?? '',
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      limitType: json['limitType'] ?? '',
      scopeType: json['scopeType'] ?? '',
      period: json['period'] ?? '',
      transactionType: json['transactionType'],
      accountType: json['accountType'],
      currency: json['currency'],
      minAmount: parseMoney(json['minAmount']),
      maxAmount: parseMoney(json['maxAmount']),
      maxCount: parseCount(json['maxCount']),
      active: json['active'] ?? false,
      requireReviewExceed: json['requireReviewExceed'] ?? false,
      scopeDescription: json['scopeDescription'] ?? '',
      createdAt: parseDate(json['createdAt']),
      updatedAt: parseDate(json['updatedAt']),
    );
  }

  LimitRule toEntity() {
    return LimitRule(
      id: id,
      code: code,
      name: name,
      description: description,
      limitType: limitType,
      scopeType: scopeType,
      period: period,
      transactionType: transactionType,
      accountType: accountType,
      currency: currency,
      minAmount: minAmount,
      maxAmount: maxAmount,
      maxCount: maxCount,
      active: active,
      requireReviewExceed: requireReviewExceed,
      scopeDescription: scopeDescription,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}

class LimitRulesPageModel {
  final List<LimitRuleModel> content;
  final int totalElements;
  final int totalPages;
  final int number;
  final int size;
  final int numberOfElements;
  final bool first;
  final bool last;
  final bool empty;

  const LimitRulesPageModel({
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

  factory LimitRulesPageModel.fromJson(Map<String, dynamic> json) {
    final list = (json['content'] as List<dynamic>? ?? [])
        .map((item) => LimitRuleModel.fromJson(item as Map<String, dynamic>))
        .toList();

    return LimitRulesPageModel(
      content: list,
      totalElements: (json['totalElements'] ?? 0) as int,
      totalPages: (json['totalPages'] ?? 0) as int,
      number: (json['number'] ?? 0) as int,
      size: (json['size'] ?? list.length) as int,
      numberOfElements: (json['numberOfElements'] ?? list.length) as int,
      first: json['first'] ?? true,
      last: json['last'] ?? true,
      empty: json['empty'] ?? list.isEmpty,
    );
  }

  LimitRulesPage toEntity() {
    return LimitRulesPage(
      content: content.map((item) => item.toEntity()).toList(),
      totalElements: totalElements,
      totalPages: totalPages,
      number: number,
      size: size,
      numberOfElements: numberOfElements,
      first: first,
      last: last,
      empty: empty,
    );
  }
}
