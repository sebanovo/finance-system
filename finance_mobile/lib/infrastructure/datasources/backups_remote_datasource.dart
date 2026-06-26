import 'dart:typed_data';

import 'package:dio/dio.dart';

import '../../core/network/api_client.dart';
import '../models/backup_page_model.dart';
import '../models/backup_record_model.dart';
import '../models/model_parsers.dart';

abstract class BackupsRemoteDataSource {
  Future<BackupPageModel> getBackups({int page, int size});
  Future<BackupRecordModel> createBackup({String? reason});
  Future<BackupRecordModel> restoreBackupFromFile({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  });
  Future<Uint8List> downloadBackup(String backupId);
}

class BackupsRemoteDataSourceImpl implements BackupsRemoteDataSource {
  final ApiClient apiClient;

  BackupsRemoteDataSourceImpl(this.apiClient);

  @override
  Future<BackupPageModel> getBackups({int page = 0, int size = 20}) async {
    final response = await apiClient.get(
      '/api/backups',
      queryParameters: {'page': page, 'size': size},
    );
    return _parsePageResponse(
      response,
      fallbackMessage: 'Error al obtener respaldos',
    );
  }

  @override
  Future<BackupRecordModel> createBackup({String? reason}) async {
    final response = await apiClient.post('/api/backups', {
      if (reason != null && reason.trim().isNotEmpty) 'reason': reason.trim(),
    });
    return _parseSingleResponse(
      response,
      (json) => BackupRecordModel.fromJson(json),
      fallbackMessage: 'Error al crear respaldo',
    );
  }

  @override
  Future<BackupRecordModel> restoreBackupFromFile({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  }) async {
    final formData = FormData.fromMap({
      'file': MultipartFile.fromBytes(fileBytes, filename: fileName),
      'confirmationText': confirmationText,
      if (reason != null && reason.trim().isNotEmpty) 'reason': reason.trim(),
    });

    final response = await apiClient.post(
      '/api/backups/restore/upload',
      formData,
    );
    return _parseSingleResponse(
      response,
      (json) => BackupRecordModel.fromJson(json),
      fallbackMessage: 'Error al restaurar respaldo',
    );
  }

  @override
  Future<Uint8List> downloadBackup(String backupId) async {
    final response = await apiClient.dio.get(
      '/api/backups/$backupId/download',
      options: Options(responseType: ResponseType.bytes),
    );

    if (response.statusCode == 200 && response.data is List<int>) {
      return Uint8List.fromList(List<int>.from(response.data as List));
    }

    if (response.statusCode == 200 && response.data is List) {
      return Uint8List.fromList(
        (response.data as List).whereType<int>().toList(),
      );
    }

    if (response.statusCode == 200 && response.data is Uint8List) {
      return response.data as Uint8List;
    }

    final body = asMap(response.data);
    throw Exception(body?['message'] ?? 'No se pudo descargar el respaldo');
  }

  BackupPageModel _parsePageResponse(
    Response response, {
    required String fallbackMessage,
  }) {
    if (response.statusCode == 200) {
      final body = asMap(response.data);
      if (body != null && body['success'] == true) {
        final payload = asMap(body['data']);
        if (payload != null) {
          return BackupPageModel.fromJson(payload);
        }
      }
      throw Exception(body?['message'] ?? fallbackMessage);
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception(fallbackMessage);
  }

  T _parseSingleResponse<T>(
    Response response,
    T Function(Map<String, dynamic>) mapper, {
    required String fallbackMessage,
  }) {
    if (response.statusCode == 200 || response.statusCode == 201) {
      final body = asMap(response.data);
      if (body != null && body['success'] == true) {
        final payload = asMap(body['data']);
        if (payload != null) {
          return mapper(payload);
        }
      }
      throw Exception(body?['message'] ?? fallbackMessage);
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    final body = asMap(response.data);
    throw Exception(body?['message'] ?? fallbackMessage);
  }
}
