class NotificationPreference {
  final String id;
  final String userId;
  final String category;
  final bool pushEnabled;
  final bool inAppEnabled;
  final bool emailEnabled;
  final bool smsEnabled;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  NotificationPreference({
    required this.id,
    required this.userId,
    required this.category,
    required this.pushEnabled,
    required this.inAppEnabled,
    required this.emailEnabled,
    required this.smsEnabled,
    this.createdAt,
    this.updatedAt,
  });

  NotificationPreference copyWith({
    String? id,
    String? userId,
    String? category,
    bool? pushEnabled,
    bool? inAppEnabled,
    bool? emailEnabled,
    bool? smsEnabled,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return NotificationPreference(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      category: category ?? this.category,
      pushEnabled: pushEnabled ?? this.pushEnabled,
      inAppEnabled: inAppEnabled ?? this.inAppEnabled,
      emailEnabled: emailEnabled ?? this.emailEnabled,
      smsEnabled: smsEnabled ?? this.smsEnabled,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
