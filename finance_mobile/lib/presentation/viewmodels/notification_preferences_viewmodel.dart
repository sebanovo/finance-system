import 'package:flutter/material.dart';
import '../../domain/entities/notification_preference.dart';
import '../../domain/usecases/get_notification_preferences_usecase.dart';
import '../../domain/usecases/upsert_notification_preference_usecase.dart';

class NotificationPreferencesViewModel extends ChangeNotifier {
  final GetNotificationPreferencesUseCase getPreferencesUseCase;
  final UpsertNotificationPreferenceUseCase upsertPreferenceUseCase;

  List<NotificationPreference> _preferences = [];
  bool _loading = false;
  bool _saving = false;
  String? _errorMessage;
  String? _successMessage;

  NotificationPreferencesViewModel({
    required this.getPreferencesUseCase,
    required this.upsertPreferenceUseCase,
  });

  List<NotificationPreference> get preferences => _preferences;
  bool get loading => _loading;
  bool get saving => _saving;
  String? get errorMessage => _errorMessage;
  String? get successMessage => _successMessage;

  static const List<String> categories = [
    'TRANSACTIONS',
    'PAYMENTS',
    'ACCOUNTS',
    'SECURITY',
    'SYSTEM',
    'FX',
    'LIMITS',
  ];

  Future<void> loadPreferences() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      _preferences = await getPreferencesUseCase();
      _successMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  NotificationPreference preferenceFor(String category) {
    return _preferences.firstWhere(
      (preference) => preference.category == category,
      orElse: () => NotificationPreference(
        id: '',
        userId: '',
        category: category,
        pushEnabled: false,
        inAppEnabled: true,
        emailEnabled: false,
        smsEnabled: false,
      ),
    );
  }

  Future<void> savePreference(NotificationPreference preference) async {
    _saving = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
    try {
      final updated = await upsertPreferenceUseCase(
        category: preference.category,
        pushEnabled: preference.pushEnabled,
        inAppEnabled: preference.inAppEnabled,
        emailEnabled: preference.emailEnabled,
        smsEnabled: preference.smsEnabled,
      );

      final index = _preferences.indexWhere(
        (item) => item.category == updated.category,
      );
      if (index == -1) {
        _preferences.add(updated);
      } else {
        _preferences[index] = updated;
      }
      _successMessage = 'Preferencia guardada';
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  void clearMessages() {
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
  }
}
