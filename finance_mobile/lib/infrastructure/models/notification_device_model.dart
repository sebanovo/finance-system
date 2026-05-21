import '../../../domain/entities/notification_device.dart';

class NotificationDeviceModel {
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

  NotificationDeviceModel({
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
    this.updatedAt,
  });

  factory NotificationDeviceModel.fromJson(Map<String, dynamic> json) {
    return NotificationDeviceModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      deviceId: json['deviceId'] ?? '',
      platform: json['platform'] ?? '',
      deviceName: json['deviceName'] ?? '',
      appVersion: json['appVersion'] ?? '',
      osVersion: json['osVersion'] ?? '',
      status: json['status'] ?? '',
      lastSeenAt: DateTime.parse(json['lastSeenAt']),
      createdAt: DateTime.parse(json['createdAt']),
      // updatedAt: DateTime.parse(json['updatedAt']),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : null, // ✅ Manejar null
    );
  }

  NotificationDevice toEntity() {
    return NotificationDevice(
      id: id,
      userId: userId,
      deviceId: deviceId,
      platform: platform,
      deviceName: deviceName,
      appVersion: appVersion,
      osVersion: osVersion,
      status: status,
      lastSeenAt: lastSeenAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
