import 'dart:io';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:finance_mobile/domain/entities/notification.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:package_info_plus/package_info_plus.dart';
import '../../../domain/usecases/get_notifications_usecase.dart';
import '../../domain/usecases/get_unread_count_usecase.dart';
import '../../domain/usecases/mark_notification_as_read_usecase.dart';
import '../../domain/usecases/mark_all_notifications_as_read_usecase.dart';
import '../../domain/usecases/archive_notification_usecase.dart';
import '../../domain/usecases/register_device_usecase.dart';

// ✅ Importaciones específicas para Web
// ignore: avoid_web_libraries_in_flutter, unused_import, deprecated_member_use
import 'dart:html' as html;

class NotificationsViewModel extends ChangeNotifier {
  final GetNotificationsUseCase getNotificationsUseCase;
  final GetUnreadCountUseCase getUnreadCountUseCase;
  final MarkNotificationAsReadUseCase markAsReadUseCase;
  final MarkAllNotificationsAsReadUseCase markAllAsReadUseCase;
  final ArchiveNotificationUseCase archiveUseCase;
  final RegisterDeviceUseCase registerDeviceUseCase;

  List<AppNotification> _notifications = [];
  bool _loading = false;
  bool _loadingMore = false;
  int _unreadCount = 0;
  String? _errorMessage;
  bool _hasMore = true;
  int _currentOffset = 0;
  static const int _pageSize = 20;

  NotificationsViewModel({
    required this.getNotificationsUseCase,
    required this.getUnreadCountUseCase,
    required this.markAsReadUseCase,
    required this.markAllAsReadUseCase,
    required this.archiveUseCase,
    required this.registerDeviceUseCase,
  });

  List<AppNotification> get notifications => _notifications;
  bool get loading => _loading;
  bool get loadingMore => _loadingMore;
  int get unreadCount => _unreadCount;
  String? get errorMessage => _errorMessage;

  String? _currentUserRole;

  void setCurrentUserRole(String role) {
    _currentUserRole = role;
  }

  bool get canRegisterDevice {
    return _currentUserRole == 'USER';
  }

  Future<void> loadInitial() async {
    _loading = true;
    _errorMessage = null;
    _currentOffset = 0;
    _hasMore = true;
    notifyListeners();

    try {
      final newList = await getNotificationsUseCase(
        limit: _pageSize,
        offset: 0,
      );
      _notifications = newList;
      _hasMore = newList.length >= _pageSize;
      _currentOffset = newList.length;
      await loadUnreadCount();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> loadMore() async {
    if (_loadingMore || !_hasMore) return;
    _loadingMore = true;
    notifyListeners();
    try {
      final newList = await getNotificationsUseCase(
        limit: _pageSize,
        offset: _currentOffset,
      );
      if (newList.isNotEmpty) {
        _notifications.addAll(newList);
        _currentOffset += newList.length;
        _hasMore = newList.length >= _pageSize;
      } else {
        _hasMore = false;
      }
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingMore = false;
      notifyListeners();
    }
  }

  Future<void> loadUnreadCount() async {
    try {
      _unreadCount = await getUnreadCountUseCase();
      notifyListeners();
    } catch (e) {
      // silencioso, no romper UI
    }
  }

  Future<void> markAsRead(String id) async {
    try {
      await markAsReadUseCase(id);
      // actualizar localmente
      final index = _notifications.indexWhere((n) => n.id == id);
      if (index != -1) {
        _notifications[index] = AppNotification(
          id: _notifications[index].id,
          userId: _notifications[index].userId,
          type: _notifications[index].type,
          category: _notifications[index].category,
          priority: _notifications[index].priority,
          status: 'READ',
          title: _notifications[index].title,
          body: _notifications[index].body,
          data: _notifications[index].data,
          imageUrl: _notifications[index].imageUrl,
          actionUrl: _notifications[index].actionUrl,
          readAt: DateTime.now(),
          openedAt: _notifications[index].openedAt,
          archivedAt: _notifications[index].archivedAt,
          expiresAt: _notifications[index].expiresAt,
          createdAt: _notifications[index].createdAt,
          updatedAt: _notifications[index].updatedAt,
        );
        _unreadCount = (_unreadCount - 1).clamp(0, _unreadCount);
        notifyListeners();
      }
    } catch (e) {
      // manejo silencioso o snackbar desde UI
    }
  }

  Future<void> markAllAsRead() async {
    try {
      final updated = await markAllAsReadUseCase();
      for (var updatedNotif in updated) {
        final index = _notifications.indexWhere((n) => n.id == updatedNotif.id);
        if (index != -1) _notifications[index] = updatedNotif;
      }
      _unreadCount = 0;
      notifyListeners();
    } catch (e) {
      // error manejado en UI
    }
  }

  Future<void> archive(String id) async {
    try {
      await archiveUseCase(id);
      _notifications.removeWhere((n) => n.id == id);
      notifyListeners();
    } catch (e) {}
  }

  // Future<void> registerCurrentDevice() async {
  //   if (!canRegisterDevice) {
  //     debugdebugPrint(
  //       '❌ Usuario con rol $_currentUserRole no puede registrar dispositivos',
  //     );
  //     return;
  //   }
  //   try {
  //     // Obtener token FCM
  //     final fcmToken = await _getFcmToken();
  //     if (fcmToken == null) {
  //       debugdebugPrint('❌ No se pudo obtener FCM Token');
  //       return;
  //     }
  //     debugdebugPrint('✅ FCM Token obtenido: $fcmToken');

  //     // Obtener datos reales del dispositivo
  //     final deviceId = await _getDeviceId();
  //     final platform = _getPlatform();
  //     final deviceName = await _getDeviceName();
  //     final appVersion = await _getAppVersion();
  //     final osVersion = await _getOsVersion();

  //     // ✅ Validar que ningún valor sea nulo
  //     debugdebugPrint('📱 Datos del dispositivo:');
  //     debugdebugPrint('   deviceId: $deviceId');
  //     debugdebugPrint('   platform: $platform');
  //     debugdebugPrint('   deviceName: $deviceName');
  //     debugdebugPrint('   appVersion: $appVersion');
  //     debugdebugPrint('   osVersion: $osVersion');

  //     // ✅ Asegurar que ningún valor sea null
  //     if (deviceId.isEmpty) {
  //       throw Exception('deviceId es nulo o vacío');
  //     }
  //     if (platform.isEmpty) {
  //       throw Exception('platform es nulo o vacío');
  //     }
  //     if (deviceName.isEmpty) {
  //       throw Exception('deviceName es nulo o vacío');
  //     }
  //     if (appVersion.isEmpty) {
  //       throw Exception('appVersion es nulo o vacío');
  //     }
  //     if (osVersion.isEmpty) {
  //       throw Exception('osVersion es nulo o vacío');
  //     }

  //     await registerDeviceUseCase(
  //       deviceId: deviceId,
  //       fcmToken: fcmToken,
  //       platform: platform,
  //       deviceName: deviceName,
  //       appVersion: appVersion,
  //       osVersion: osVersion,
  //     );
  //     debugdebugPrint('✅ Dispositivo registrado exitosamente');
  //   } catch (e) {
  //     debugdebugPrint('❌ Error al registrar dispositivo: $e');
  //   }
  // }

  // Métodos auxiliares para obtener datos reales (puedes moverlos a un servicio)
  Future<String?> _getFcmToken() async {
    try {
      final token = await FirebaseMessaging.instance.getToken();
      debugPrint('🔑 FCM Token real: $token');
      return token;
    } catch (e) {
      debugPrint('❌ Error obteniendo FCM Token: $e');
      return null;
    }
  }

  // ✅ CORREGIDO - Detectar plataforma correctamente

  // ✅ CORREGIDO - Obtener nombre real del dispositivo

  Future<void> registerCurrentDevice() async {
    if (!canRegisterDevice) {
      debugPrint(
        '❌ Usuario con rol $_currentUserRole no puede registrar dispositivos',
      );
      return;
    }
    try {
      final fcmToken = await _getFcmToken();
      if (fcmToken == null) {
        debugPrint('❌ No se pudo obtener FCM Token');
        return;
      }

      debugPrint('✅ FCM Token obtenido: $fcmToken');

      // ✅ Obtener datos según la plataforma
      String deviceId;
      String platform;
      String deviceName;
      String appVersion;
      String osVersion;

      if (kIsWeb) {
        // 🌐 Web - Usar alternativas
        deviceId = await _getWebDeviceId();
        platform = 'WEB';
        deviceName = await _getWebDeviceName();
        appVersion = await _getWebAppVersion();
        osVersion = await _getWebOsVersion();
      } else {
        // 📱 Móvil/Desktop
        deviceId = await _getMobileDeviceId();
        platform = _getMobilePlatform();
        deviceName = await _getMobileDeviceName();
        appVersion = await _getMobileAppVersion();
        osVersion = await _getMobileOsVersion();
      }

      debugPrint('📱 Datos del dispositivo:');
      debugPrint('   deviceId: $deviceId');
      debugPrint('   platform: $platform');
      debugPrint('   deviceName: $deviceName');
      debugPrint('   appVersion: $appVersion');
      debugPrint('   osVersion: $osVersion');

      await registerDeviceUseCase(
        deviceId: deviceId,
        fcmToken: fcmToken,
        platform: platform,
        deviceName: deviceName,
        appVersion: appVersion,
        osVersion: osVersion,
      );
      debugPrint('✅ Dispositivo registrado exitosamente');
    } catch (e) {
      debugPrint('❌ Error al registrar dispositivo: $e');
    }
  }

  // 🌐 Métodos específicos para Web
  Future<String> _getWebDeviceId() async {
    try {
      // Usar localStorage o generar un ID único persistente
      var deviceId = html.window.localStorage['deviceId'];
      if (deviceId == null || deviceId.isEmpty) {
        deviceId =
            'web_${DateTime.now().millisecondsSinceEpoch}_${_generateRandomString(8)}';
        html.window.localStorage['deviceId'] = deviceId;
      }
      return deviceId;
    } catch (e) {
      return 'web_${DateTime.now().millisecondsSinceEpoch}';
    }
  }

  Future<String> _getWebDeviceName() async {
    try {
      final userAgent = html.window.navigator.userAgent;
      if (userAgent.contains('Chrome')) return 'Chrome Browser';
      if (userAgent.contains('Firefox')) return 'Firefox Browser';
      if (userAgent.contains('Safari')) return 'Safari Browser';
      if (userAgent.contains('Edge')) return 'Edge Browser';
      return 'Web Browser';
    } catch (e) {
      return 'Web Browser';
    }
  }

  Future<String> _getWebAppVersion() async {
    try {
      final packageInfo = await PackageInfo.fromPlatform();
      return packageInfo.version;
    } catch (e) {
      return '1.0.0';
    }
  }

  Future<String> _getWebOsVersion() async {
    try {
      final userAgent = html.window.navigator.userAgent;
      if (userAgent.contains('Windows')) return 'Windows';
      if (userAgent.contains('Mac')) return 'macOS';
      if (userAgent.contains('Linux')) return 'Linux';
      if (userAgent.contains('Android')) return 'Android';
      if (userAgent.contains('iPhone') || userAgent.contains('iPad'))
        return 'iOS';
      return 'Unknown';
    } catch (e) {
      return 'Unknown';
    }
  }

  // 📱 Métodos específicos para móvil
  Future<String> _getMobileDeviceId() async {
    try {
      final deviceInfo = DeviceInfoPlugin();
      if (Platform.isAndroid) {
        final androidInfo = await deviceInfo.androidInfo;
        return androidInfo.id;
      } else if (Platform.isIOS) {
        final iosInfo = await deviceInfo.iosInfo;
        return iosInfo.identifierForVendor ??
            'ios_${DateTime.now().millisecondsSinceEpoch}';
      }
      return 'mobile_${DateTime.now().millisecondsSinceEpoch}';
    } catch (e) {
      return 'mobile_${DateTime.now().millisecondsSinceEpoch}';
    }
  }

  String _getMobilePlatform() {
    if (Platform.isAndroid) return 'ANDROID';
    if (Platform.isIOS) return 'IOS';
    return 'MOBILE';
  }

  Future<String> _getMobileDeviceName() async {
    try {
      final deviceInfo = DeviceInfoPlugin();
      if (Platform.isAndroid) {
        final androidInfo = await deviceInfo.androidInfo;
        return androidInfo.model;
      } else if (Platform.isIOS) {
        final iosInfo = await deviceInfo.iosInfo;
        return iosInfo.name;
      }
      return 'Mobile Device';
    } catch (e) {
      return 'Mobile Device';
    }
  }

  Future<String> _getMobileAppVersion() async {
    try {
      final packageInfo = await PackageInfo.fromPlatform();
      return packageInfo.version;
    } catch (e) {
      return '1.0.0';
    }
  }

  Future<String> _getMobileOsVersion() async {
    try {
      final deviceInfo = DeviceInfoPlugin();
      if (Platform.isAndroid) {
        final androidInfo = await deviceInfo.androidInfo;
        return androidInfo.version.release;
      } else if (Platform.isIOS) {
        final iosInfo = await deviceInfo.iosInfo;
        return iosInfo.systemVersion;
      }
      return 'unknown';
    } catch (e) {
      return 'unknown';
    }
  }

  String _generateRandomString(int length) {
    const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
    return String.fromCharCodes(
      Iterable.generate(
        length,
        (_) => chars.codeUnitAt(
          DateTime.now().millisecondsSinceEpoch % chars.length,
        ),
      ),
    );
  }
}
