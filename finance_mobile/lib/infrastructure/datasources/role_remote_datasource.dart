import 'package:dio/dio.dart';
import '../../../../core/network/api_client.dart';
import '../models/role.dart';

abstract class RoleRemoteDataSource {
  Future<List<RoleModel>> getRoles();
}

class RoleRemoteDataSourceImpl implements RoleRemoteDataSource {
  final ApiClient apiClient;

  RoleRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<RoleModel>> getRoles() async {
    try {
      final response = await apiClient.get('/api/access/roles');
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        final List<dynamic> data = decoded['data'] ?? [];
        return data.map((json) => RoleModel.fromJson(json)).toList();
      } else {
        throw Exception(decoded['message'] ?? 'Error al obtener roles');
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
