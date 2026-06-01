import '../entities/notification.dart';
import '../entities/notification_device.dart';
import '../entities/notification_preference.dart';

abstract class NotificationRepository {
  // Notificaciones
  Future<List<AppNotification>> getNotifications({
    int limit = 50,
    int offset = 0,
  });
  Future<int> getUnreadCount();
  Future<AppNotification> markAsRead(String notificationId);
  Future<List<AppNotification>> markAllAsRead();
  Future<AppNotification> archive(String notificationId);
  Future<AppNotification> markAsOpened(String notificationId);

  // Dispositivos
  Future<NotificationDevice> registerDevice({
    required String deviceId,
    required String fcmToken,
    required String platform,
    required String deviceName,
    required String appVersion,
    required String osVersion,
  });
  Future<List<NotificationDevice>> getDevices();
  Future<void> deactivateDevice(String deviceId);
  Future<NotificationDevice> revokeDevice(String deviceId);

  // Preferencias
  Future<List<NotificationPreference>> getPreferences();
  Future<NotificationPreference> upsertPreference({
    required String category,
    required bool pushEnabled,
    required bool inAppEnabled,
    required bool emailEnabled,
    required bool smsEnabled,
  });
}
