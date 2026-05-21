import '../repositories/notification_repository.dart';
import '../entities/notification_device.dart';

class GetDevicesUseCase {
  final NotificationRepository repository;

  GetDevicesUseCase(this.repository);

  Future<List<NotificationDevice>> call() {
    return repository.getDevices();
  }
}
