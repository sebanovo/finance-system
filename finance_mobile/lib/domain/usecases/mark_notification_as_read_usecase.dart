import '../repositories/notification_repository.dart';
import '../entities/notification.dart';

class MarkNotificationAsReadUseCase {
  final NotificationRepository repository;

  MarkNotificationAsReadUseCase(this.repository);

  Future<AppNotification> call(String notificationId) {
    return repository.markAsRead(notificationId);
  }
}
