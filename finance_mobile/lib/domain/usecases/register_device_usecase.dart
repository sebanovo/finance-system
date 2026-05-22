import '../repositories/notification_repository.dart';
import '../entities/notification_device.dart';

class RegisterDeviceUseCase {
  final NotificationRepository repository;

  RegisterDeviceUseCase(this.repository);

  Future<NotificationDevice> call({
    required String deviceId,
    required String fcmToken,
    required String platform,
    required String deviceName,
    required String appVersion,
    required String osVersion,
  }) {
    return repository.registerDevice(
      deviceId: deviceId,
      fcmToken: fcmToken,
      platform: platform,
      deviceName: deviceName,
      appVersion: appVersion,
      osVersion: osVersion,
    );
  }
}
