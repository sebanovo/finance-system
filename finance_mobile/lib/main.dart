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
    const primaryGreen = Color(0xFF166534);
    const primaryBlack = Color(0xFF111827);
    const surface = Color(0xFFFFFFFF);
    const surfaceVariant = Color(0xFFF9FAFB);
    const outline = Color(0xFFE5E7EB);

    return MaterialApp.router(
      debugShowCheckedModeBanner: false,
      title: Env.systemName,
      theme: ThemeData(
        colorScheme: const ColorScheme.light(
          primary: primaryGreen,
          secondary: primaryBlack,
          surface: surface,
          error: Color(0xFFDC2626),
          onPrimary: Colors.white,
          onSecondary: Colors.white,
          onSurface: primaryBlack,
          onError: Colors.white,
        ),
        scaffoldBackgroundColor: surfaceVariant,
        useMaterial3: true,
        appBarTheme: const AppBarTheme(
          backgroundColor: surface,
          foregroundColor: primaryBlack,
          elevation: 0,
          centerTitle: false,
          surfaceTintColor: surface,
        ),
        cardTheme: CardThemeData(
          color: surface,
          surfaceTintColor: surface,
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(18),
            side: const BorderSide(color: outline),
          ),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: primaryGreen,
            foregroundColor: Colors.white,
            elevation: 0,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(14),
              side: const BorderSide(color: primaryBlack),
            ),
          ),
        ),
        outlinedButtonTheme: OutlinedButtonThemeData(
          style: OutlinedButton.styleFrom(
            foregroundColor: primaryBlack,
            side: const BorderSide(color: primaryBlack, width: 1.2),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(14),
            ),
          ),
        ),
        inputDecorationTheme: InputDecorationTheme(
          filled: true,
          fillColor: surface,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: const BorderSide(color: primaryBlack),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: const BorderSide(color: primaryBlack),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(14),
            borderSide: const BorderSide(color: primaryGreen, width: 1.5),
          ),
          labelStyle: const TextStyle(color: primaryBlack),
          hintStyle: const TextStyle(color: Color(0xFF6B7280)),
        ),
        chipTheme: const ChipThemeData(
          backgroundColor: surface,
          selectedColor: Color(0xFFDCFCE7),
          side: BorderSide(color: outline),
          labelStyle: TextStyle(color: primaryBlack),
        ),
      ),
      routerConfig: appRouter,
    );
  }
}
