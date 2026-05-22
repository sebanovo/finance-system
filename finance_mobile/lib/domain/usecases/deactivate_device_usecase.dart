import '../repositories/notification_repository.dart';

class DeactivateDeviceUseCase {
  final NotificationRepository repository;

  DeactivateDeviceUseCase(this.repository);

  Future<void> call(String deviceId) {
    return repository.deactivateDevice(deviceId);
  }
}
