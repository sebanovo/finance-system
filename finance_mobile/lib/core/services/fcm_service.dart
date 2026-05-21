import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

class FcmService {
  static Future<void> initialize() async {
    final fcm = FirebaseMessaging.instance;
    // Solicitar permiso
    await fcm.requestPermission();
    // Obtener token (puedes guardarlo localmente)
    final token = await fcm.getToken();
    debugPrint('FCM Token: $token');

    // Manejo cuando la app está en primer plano
    FirebaseMessaging.onMessage.listen((message) {
      // Mostrar snackbar o actualizar badge local
    });

    // Manejo cuando el usuario toca la notificación y la app está en segundo plano/cerrada
    FirebaseMessaging.onMessageOpenedApp.listen((message) {
      // Navegar según actionUrl o datos
    });
  }
}
