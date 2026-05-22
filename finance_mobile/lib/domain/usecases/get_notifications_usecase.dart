import '../repositories/notification_repository.dart';
import '../entities/notification.dart';

class GetNotificationsUseCase {
  final NotificationRepository repository;

  GetNotificationsUseCase(this.repository);

  Future<List<AppNotification>> call({int limit = 50, int offset = 0}) {
    return repository.getNotifications(limit: limit, offset: offset);
  }
}
