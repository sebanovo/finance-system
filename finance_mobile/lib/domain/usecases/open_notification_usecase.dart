import '../repositories/notification_repository.dart';
import '../entities/notification.dart';

class OpenNotificationUseCase {
  final NotificationRepository repository;

  OpenNotificationUseCase(this.repository);

  Future<AppNotification> call(String notificationId) {
    return repository.markAsOpened(notificationId);
  }
}
