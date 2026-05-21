import '../../../domain/entities/notification.dart';
import '../../../domain/entities/notification_device.dart';
import '../../../domain/repositories/notification_repository.dart';
import '../datasources/notification_remote_datasource.dart';
import '../models/register_device_request.dart';

class NotificationRepositoryImpl implements NotificationRepository {
  final NotificationRemoteDataSource remoteDataSource;

  NotificationRepositoryImpl(this.remoteDataSource);

  @override
  Future<List<AppNotification>> getNotifications({
    int limit = 50,
    int offset = 0,
  }) async {
    final models = await remoteDataSource.getNotifications(
      limit: limit,
      offset: offset,
    );
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<int> getUnreadCount() async {
    return await remoteDataSource.getUnreadCount();
  }

  @override
  Future<AppNotification> markAsRead(String notificationId) async {
    final model = await remoteDataSource.markAsRead(notificationId);
    return model.toEntity();
  }

  @override
  Future<List<AppNotification>> markAllAsRead() async {
    final models = await remoteDataSource.markAllAsRead();
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<AppNotification> archive(String notificationId) async {
    final model = await remoteDataSource.archive(notificationId);
    return model.toEntity();
  }

  @override
  Future<AppNotification> markAsOpened(String notificationId) async {
    final model = await remoteDataSource.markAsOpened(notificationId);
    return model.toEntity();
  }

  @override
  Future<NotificationDevice> registerDevice({
    required String deviceId,
    required String fcmToken,
    required String platform,
    required String deviceName,
    required String appVersion,
    required String osVersion,
  }) async {
    final request = RegisterDeviceRequest(
      deviceId: deviceId,
      fcmToken: fcmToken,
      platform: platform,
      deviceName: deviceName,
      appVersion: appVersion,
      osVersion: osVersion,
    );
    final model = await remoteDataSource.registerDevice(request);
    return model.toEntity();
  }

  @override
  Future<List<NotificationDevice>> getDevices() async {
    final models = await remoteDataSource.getDevices();
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<void> deactivateDevice(String deviceId) async {
    await remoteDataSource.deactivateDevice(deviceId);
  }

  @override
  Future<NotificationDevice> revokeDevice(String deviceId) async {
    final model = await remoteDataSource.revokeDevice(deviceId);
    return model.toEntity();
  }
}
