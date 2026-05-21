import 'package:finance_mobile/core/routes/app_routes.dart';
import 'package:finance_mobile/core/services/notification_service.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:finance_mobile/constants/env.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:timezone/data/latest.dart' as tzdata;
// import 'package:timezone/timezone.dart' as tz;

import 'core/di/injection_container.dart' as di;

// Para manejar notificaciones en primer plano
@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  // Inicializar notificaciones locales en segundo plano
  tzdata.initializeTimeZones();
  await NotificationService.initialize();
  // Aquí puedes procesar la notificación en segundo plano
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp();

  // ✅ Inicializar servicio de notificaciones locales
  await NotificationService.initialize();

  final fcm = FirebaseMessaging.instance;

  // Solicitar permiso de notificaciones
  await fcm.requestPermission(alert: true, badge: true, sound: true);

  // Registrar handler para notificaciones en segundo plano
  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

  await dotenv.load(fileName: ".env");
  await di.init();

  runApp(const MyApp());
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
