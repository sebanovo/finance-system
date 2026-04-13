class Subscription {
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

  Subscription({
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
}
