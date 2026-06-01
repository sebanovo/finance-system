import '../entities/notification_preference.dart';
import '../repositories/notification_repository.dart';

class UpsertNotificationPreferenceUseCase {
  final NotificationRepository repository;

  UpsertNotificationPreferenceUseCase(this.repository);

  Future<NotificationPreference> call({
    required String category,
    required bool pushEnabled,
    required bool inAppEnabled,
    required bool emailEnabled,
    required bool smsEnabled,
  }) {
    return repository.upsertPreference(
      category: category,
      pushEnabled: pushEnabled,
      inAppEnabled: inAppEnabled,
      emailEnabled: emailEnabled,
      smsEnabled: smsEnabled,
    );
  }
}
