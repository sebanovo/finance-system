class NotificationDevice {
  final String id;
  final String userId;
  final String deviceId;
  final String platform;
  final String deviceName;
  final String appVersion;
  final String osVersion;
  final String status;
  final DateTime lastSeenAt;
  final DateTime createdAt;
  final DateTime? updatedAt;

  NotificationDevice({
    required this.id,
    required this.userId,
    required this.deviceId,
    required this.platform,
    required this.deviceName,
    required this.appVersion,
    required this.osVersion,
    required this.status,
    required this.lastSeenAt,
    required this.createdAt,
    this.updatedAt, // ✅ Ahora es nullable
  });

  bool get isActive => status == 'ACTIVE';

  String get platformIcon {
    switch (platform) {
      case 'ANDROID':
        return '📱';
      case 'IOS':
        return '🍎';
      case 'WEB':
        return '🌐';
      default:
        return '💻';
    }
  }
}
