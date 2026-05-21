import '../repositories/notification_repository.dart';
import '../entities/notification.dart';

class ArchiveNotificationUseCase {
  final NotificationRepository repository;

  ArchiveNotificationUseCase(this.repository);

  Future<AppNotification> call(String notificationId) {
    return repository.archive(notificationId);
  }
}
