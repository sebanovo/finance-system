import '../../../../core/network/api_client.dart';
import '../models/notification_model.dart';
import '../models/notification_device_model.dart';
import '../models/notification_preference_model.dart';
import '../models/upsert_notification_preference_request.dart';
import '../models/register_device_request.dart';

abstract class NotificationRemoteDataSource {
  Future<List<NotificationModel>> getNotifications({int limit, int offset});
  Future<int> getUnreadCount();
  Future<NotificationModel> markAsRead(String id);
  Future<List<NotificationModel>> markAllAsRead();
  Future<NotificationModel> archive(String id);
  Future<NotificationModel> markAsOpened(String id);
  Future<NotificationDeviceModel> registerDevice(RegisterDeviceRequest request);
  Future<List<NotificationDeviceModel>> getDevices();
  Future<void> deactivateDevice(String deviceId);
  Future<NotificationDeviceModel> revokeDevice(String deviceId);
  Future<List<NotificationPreferenceModel>> getPreferences();
  Future<NotificationPreferenceModel> upsertPreference(
    UpsertNotificationPreferenceRequest request,
  );
}

class NotificationRemoteDataSourceImpl implements NotificationRemoteDataSource {
  final ApiClient apiClient;

  NotificationRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<NotificationModel>> getNotifications({
    int limit = 50,
    int offset = 0,
  }) async {
    final response = await apiClient.get(
      '/api/me/notifications?limit=$limit&offset=$offset',
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => NotificationModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al obtener notificaciones');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<int> getUnreadCount() async {
    final response = await apiClient.get('/api/me/notifications/unread-count');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return data['data']['unreadCount'] ?? 0;
      } else {
        throw Exception(data['message'] ?? 'Error al obtener conteo');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<NotificationModel> markAsRead(String id) async {
    final response = await apiClient.patch(
      '/api/me/notifications/$id/read',
      null,
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return NotificationModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al marcar como leída');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<List<NotificationModel>> markAllAsRead() async {
    final response = await apiClient.patch(
      '/api/me/notifications/read-all',
      null,
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => NotificationModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al marcar todas');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<NotificationModel> archive(String id) async {
    final response = await apiClient.patch(
      '/api/me/notifications/$id/archive',
      null,
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return NotificationModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al archivar');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<NotificationModel> markAsOpened(String id) async {
    final response = await apiClient.patch(
      '/api/me/notifications/$id/open',
      null,
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return NotificationModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al marcar como abierta');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  // @override
  // Future<NotificationDeviceModel> registerDevice(
  //   RegisterDeviceRequest request,
  // ) async {
  //   final response = await apiClient.post(
  //     '/api/me/notification-devices',
  //     request.toJson(),
  //   );
  //   if (response.statusCode == 200 || response.statusCode == 201) {
  //     final data = response.data as Map<String, dynamic>;
  //     if (data['success'] == true) {
  //       return NotificationDeviceModel.fromJson(data['data']);
  //     } else {
  //       throw Exception(data['message'] ?? 'Error al registrar dispositivo');
  //     }
  //   } else {
  //     throw Exception('Error ${response.statusCode}');
  //   }
  // }
  @override
  Future<NotificationDeviceModel> registerDevice(
    RegisterDeviceRequest request,
  ) async {
    try {
      final response = await apiClient.post(
        '/api/me/notification-devices',
        request.toJson(),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = response.data as Map<String, dynamic>;
        if (data['success'] == true) {
          return NotificationDeviceModel.fromJson(data['data']);
        } else {
          throw Exception(data['message'] ?? 'Error al registrar dispositivo');
        }
      } else {
        throw Exception('Error ${response.statusCode}: ${response.data}');
      }
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<List<NotificationDeviceModel>> getDevices() async {
    final response = await apiClient.get('/api/me/notification-devices');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list
            .map((json) => NotificationDeviceModel.fromJson(json))
            .toList();
      } else {
        throw Exception(data['message'] ?? 'Error al obtener dispositivos');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<void> deactivateDevice(String deviceId) async {
    final response = await apiClient.patch(
      '/api/me/notification-devices/$deviceId/deactivate',
      null,
    );
    if (response.statusCode != 200) {
      final data = response.data as Map<String, dynamic>;
      throw Exception(data['message'] ?? 'Error al desactivar dispositivo');
    }
  }

  @override
  Future<NotificationDeviceModel> revokeDevice(String deviceId) async {
    final response = await apiClient.delete(
      '/api/me/notification-devices/$deviceId',
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return NotificationDeviceModel.fromJson(data['data']);
      } else {
        throw Exception(data['message'] ?? 'Error al revocar dispositivo');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<List<NotificationPreferenceModel>> getPreferences() async {
    final response = await apiClient.get('/api/me/notification-preferences');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list
            .map(
              (json) => NotificationPreferenceModel.fromJson(
                Map<String, dynamic>.from(json as Map),
              ),
            )
            .toList();
      }
      throw Exception(data['message'] ?? 'Error al obtener preferencias');
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<NotificationPreferenceModel> upsertPreference(
    UpsertNotificationPreferenceRequest request,
  ) async {
    final response = await apiClient.put(
      '/api/me/notification-preferences',
      request.toJson(),
    );
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return NotificationPreferenceModel.fromJson(
          Map<String, dynamic>.from(data['data'] as Map),
        );
      }
      throw Exception(data['message'] ?? 'Error al actualizar preferencias');
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }
}
