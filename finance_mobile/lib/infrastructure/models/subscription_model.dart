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

  factory SubscriptionModel.fromJson(Map<String, dynamic> json) {
    return SubscriptionModel(
      id: json['id'] ?? '',
      tenantId: json['tenantId'] ?? '',
      tenantName: json['tenantName'] ?? '',
      tenantSlug: json['tenantSlug'] ?? '',
      planId: json['planId'] ?? '',
      planCode: json['planCode'] ?? '',
      planName: json['planName'] ?? '',
      planType: json['planType'] ?? '',
      maxUsers: json['maxUsers'] ?? 0,
      maxRoles: json['maxRoles'] ?? 0,
      status: json['status'] ?? '',
      trial: json['trial'] ?? false,
      currentSubscription: json['currentSubscription'] ?? false,
      startedAt: DateTime.parse(json['startedAt']),
      expiresAt: DateTime.parse(json['expiresAt']),
      remainingDays: json['remainingDays'] ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
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
