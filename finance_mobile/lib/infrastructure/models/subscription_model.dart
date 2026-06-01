import '../../../domain/entities/subscription.dart';

class SubscriptionModel {
  final String id;
  final String tenantId;
  final String tenantName;
  final String tenantSlug;
  final String planId;
  final String planCode;
  final String planName;
  final String planType;
  final int maxUsers;
  final int maxRoles;
  final String status;
  final bool trial;
  final bool currentSubscription;
  final DateTime startedAt;
  final DateTime expiresAt;
  final int remainingDays;
  final DateTime createdAt;
  final DateTime updatedAt;

  SubscriptionModel({
    required this.id,
    required this.tenantId,
    required this.tenantName,
    required this.tenantSlug,
    required this.planId,
    required this.planCode,
    required this.planName,
    required this.planType,
    required this.maxUsers,
    required this.maxRoles,
    required this.status,
    required this.trial,
    required this.currentSubscription,
    required this.startedAt,
    required this.expiresAt,
    required this.remainingDays,
    required this.createdAt,
    required this.updatedAt,
  });

  static SubscriptionModel? tryFromJson(Map<String, dynamic> json) {
    final startedAt = _dateTime(json['startedAt']);
    final expiresAt = _dateTime(json['expiresAt']);
    final createdAt = _dateTime(json['createdAt']);
    final updatedAt = _dateTime(json['updatedAt']);

    if (startedAt == null ||
        expiresAt == null ||
        createdAt == null ||
        updatedAt == null) {
      return null;
    }

    return SubscriptionModel(
      id: _string(json['id']),
      tenantId: _string(json['tenantId']),
      tenantName: _string(json['tenantName']),
      tenantSlug: _string(json['tenantSlug']),
      planId: _string(json['planId']),
      planCode: _string(json['planCode']),
      planName: _string(json['planName']),
      planType: _string(json['planType']),
      maxUsers: _int(json['maxUsers']),
      maxRoles: _int(json['maxRoles']),
      status: _string(json['status']),
      trial: json['trial'] ?? false,
      currentSubscription: json['currentSubscription'] ?? false,
      startedAt: startedAt,
      expiresAt: expiresAt,
      remainingDays: _int(json['remainingDays']),
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }

  factory SubscriptionModel.fromJson(Map<String, dynamic> json) =>
      tryFromJson(json)!;

  static String _string(dynamic value) => value?.toString() ?? '';

  static int _int(dynamic value) {
    if (value is int) return value;
    if (value is num) return value.toInt();
    return int.tryParse(value?.toString() ?? '') ?? 0;
  }

  static DateTime? _dateTime(dynamic value) {
    if (value == null) return null;
    final text = value.toString();
    if (text.isEmpty) return null;
    return DateTime.tryParse(text);
  }

  Subscription toEntity() {
    return Subscription(
      id: id,
      tenantId: tenantId,
      tenantName: tenantName,
      tenantSlug: tenantSlug,
      planId: planId,
      planCode: planCode,
      planName: planName,
      planType: planType,
      maxUsers: maxUsers,
      maxRoles: maxRoles,
      status: status,
      trial: trial,
      currentSubscription: currentSubscription,
      startedAt: startedAt,
      expiresAt: expiresAt,
      remainingDays: remainingDays,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
