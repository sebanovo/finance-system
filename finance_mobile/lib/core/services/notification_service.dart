// lib/core/services/notification_service.dart
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:universal_html/js.dart' as js;

class NotificationService {
  static final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  static final FlutterLocalNotificationsPlugin _localNotifications =
      FlutterLocalNotificationsPlugin();

  static Future<void> initialize() async {
    // Inicializar solo si NO es Web
    if (!kIsWeb) {
      const AndroidInitializationSettings androidSettings =
          AndroidInitializationSettings('@mipmap/ic_launcher');
      const DarwinInitializationSettings iosSettings =
          DarwinInitializationSettings();
      const InitializationSettings settings = InitializationSettings(
        android: androidSettings,
        iOS: iosSettings,
      );
      await _localNotifications.initialize(settings: settings);
    }

    // Solicitar permisos FCM
    await _fcm.requestPermission(alert: true, badge: true, sound: true);

    final token = await _fcm.getToken();
    debugPrint('📱 FCM Token: ${token?.substring(0, 20)}...');
  }

  static Future<void> showNotification({
    required String title,
    required String body,
    String? payload,
  }) async {
    if (kIsWeb) {
      // ✅ Llamar función JavaScript para Web
      js.context.callMethod('showNotification', [title, body, payload]);
    } else {
      await _showMobileNotification(title, body, payload);
    }
  }

  // 📱 Notificación para móvil/desktop
  static Future<void> _showMobileNotification(
    String title,
    String body,
    String? payload,
  ) async {
    try {
      const AndroidNotificationDetails androidDetails =
          AndroidNotificationDetails(
            'finance_channel',
            'Notificaciones Financieras',
            channelDescription: 'Canal para notificaciones financieras',
            importance: Importance.high,
            priority: Priority.high,
          );
      const DarwinNotificationDetails iosDetails = DarwinNotificationDetails();
      const NotificationDetails details = NotificationDetails(
        android: androidDetails,
        iOS: iosDetails,
      );

      await _localNotifications.show(
        id: DateTime.now().millisecondsSinceEpoch ~/ 1000,
        title: title,
        body: body,
        notificationDetails: details,
        payload: payload,
      );
      debugPrint('📱 Notificación móvil mostrada: $title');
    } catch (e) {
      debugPrint('❌ Error mostrando notificación móvil: $e');
    }
  }

  static Future<String?> getToken() async {
    return await _fcm.getToken();
  }

  static void onMessage(Function(RemoteMessage) callback) {
    FirebaseMessaging.onMessage.listen((message) {
      debugPrint('🔔 Mensaje FCM recibido: ${message.notification?.title}');

      showNotification(
        title: message.notification?.title ?? 'Nueva notificación',
        body: message.notification?.body ?? '',
        payload: message.data['actionUrl'],
      );

      callback(message);
    });
  }

  static void onMessageOpenedApp(Function(RemoteMessage) callback) {
    FirebaseMessaging.onMessageOpenedApp.listen(callback);
  }

  static void onBackgroundMessage(BackgroundMessageHandler callback) {
    FirebaseMessaging.onBackgroundMessage(callback);
  }
}
