import 'package:dio/dio.dart';
import '../../../../core/network/api_client.dart';
import '../models/permission.dart';

abstract class PermissionRemoteDataSource {
  Future<List<PermissionModel>> getPermissions();
}

class PermissionRemoteDataSourceImpl implements PermissionRemoteDataSource {
  final ApiClient apiClient;

  PermissionRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<PermissionModel>> getPermissions() async {
    try {
      final response = await apiClient.get('/api/access/permissions');
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        final List<dynamic> data = decoded['data'] ?? [];
        return data.map((json) => PermissionModel.fromJson(json)).toList();
      } else {
        throw Exception(decoded['message'] ?? 'Error al obtener permisos');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception('Sesión expirada');
      }
      throw Exception('Error de conexión: ${e.message}');
    } catch (e) {
      throw Exception('Error inesperado: $e');
    }
  }
}
