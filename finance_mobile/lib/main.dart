import 'package:finance_mobile/core/routes/app_routes.dart';
import 'package:finance_mobile/core/services/biometric_auth_service.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:finance_mobile/constants/env.dart';
import 'package:finance_mobile/core/services/notification_service.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'core/di/injection_container.dart' as di;
import 'core/network/api_client.dart';

// Para manejar notificaciones en primer plano
@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  debugPrint(
    '🔔 Notificación en segundo plano: ${message.notification?.title}',
  );
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await dotenv.load(fileName: ".env");
  await di.init();
  await _restorePersistedSession();

  await Firebase.initializeApp();
  await NotificationService.initialize();

  // Registrar handler para notificaciones en segundo plano
  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

  runApp(const MyApp());
}

Future<void> _restorePersistedSession() async {
  final biometricService = di.sl<BiometricAuthService>();
  final biometricEnabled = await biometricService.isBiometricEnabled();
  if (biometricEnabled && !kIsWeb) {
    di.sl<ApiClient>().clearSession();
    return;
  }

  final prefs = await SharedPreferences.getInstance();
  final accessToken = prefs.getString('accessToken');
  final tenantSlug = prefs.getString('tenantSlug');
  final refreshToken = prefs.getString('refreshToken');

  if (accessToken == null ||
      accessToken.isEmpty ||
      tenantSlug == null ||
      tenantSlug.isEmpty) {
    di.sl<ApiClient>().clearSession();
    return;
  }

  final apiClient = di.sl<ApiClient>();
  apiClient.setSession(
    token: accessToken,
    tenantSlug: tenantSlug,
    refreshToken: refreshToken?.isNotEmpty == true ? refreshToken : null,
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      title: Env.systemName,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueAccent),
        useMaterial3: true,
      ),
      routerConfig: appRouter,
    );
  }
}
