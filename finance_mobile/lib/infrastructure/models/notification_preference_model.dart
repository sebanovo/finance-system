import '../../domain/entities/notification_preference.dart';

class NotificationPreferenceModel {
  final String id;
  final String userId;
  final String category;
  final bool pushEnabled;
  final bool inAppEnabled;
  final bool emailEnabled;
  final bool smsEnabled;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  NotificationPreferenceModel({
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

  factory NotificationPreferenceModel.fromJson(Map<String, dynamic> json) {
    return NotificationPreferenceModel(
      id: json['id']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      category: json['category']?.toString() ?? '',
      pushEnabled: json['pushEnabled'] ?? false,
      inAppEnabled: json['inAppEnabled'] ?? false,
      emailEnabled: json['emailEnabled'] ?? false,
      smsEnabled: json['smsEnabled'] ?? false,
      createdAt: _dateTime(json['createdAt']),
      updatedAt: _dateTime(json['updatedAt']),
    );
  }

  static DateTime? _dateTime(dynamic value) {
    if (value == null) return null;
    final text = value.toString();
    if (text.isEmpty) return null;
    return DateTime.tryParse(text);
  }

  NotificationPreference toEntity() {
    return NotificationPreference(
      id: id,
      userId: userId,
      category: category,
      pushEnabled: pushEnabled,
      inAppEnabled: inAppEnabled,
      emailEnabled: emailEnabled,
      smsEnabled: smsEnabled,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
