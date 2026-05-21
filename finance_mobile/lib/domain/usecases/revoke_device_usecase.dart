import '../repositories/notification_repository.dart';
import '../entities/notification_device.dart';

class RevokeDeviceUseCase {
  final NotificationRepository repository;

  RevokeDeviceUseCase(this.repository);

  Future<NotificationDevice> call(String deviceId) {
    return repository.revokeDevice(deviceId);
  }
}
