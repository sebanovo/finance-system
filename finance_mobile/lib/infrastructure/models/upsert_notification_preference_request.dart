class UpsertNotificationPreferenceRequest {
  final String category;
  final bool pushEnabled;
  final bool inAppEnabled;
  final bool emailEnabled;
  final bool smsEnabled;

  UpsertNotificationPreferenceRequest({
    required this.category,
    required this.pushEnabled,
    required this.inAppEnabled,
    required this.emailEnabled,
    required this.smsEnabled,
  });

  Map<String, dynamic> toJson() {
    return {
      'category': category,
      'pushEnabled': pushEnabled,
      'inAppEnabled': inAppEnabled,
      'emailEnabled': emailEnabled,
      'smsEnabled': smsEnabled,
    };
  }
}
