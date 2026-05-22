import '../repositories/notification_repository.dart';
import '../entities/notification.dart';

class MarkAllNotificationsAsReadUseCase {
  final NotificationRepository repository;

  MarkAllNotificationsAsReadUseCase(this.repository);

  Future<List<AppNotification>> call() {
    return repository.markAllAsRead();
  }
}
