import '../entities/notification_preference.dart';
import '../repositories/notification_repository.dart';

class GetNotificationPreferencesUseCase {
  final NotificationRepository repository;

  GetNotificationPreferencesUseCase(this.repository);

  Future<List<NotificationPreference>> call() {
    return repository.getPreferences();
  }
}
