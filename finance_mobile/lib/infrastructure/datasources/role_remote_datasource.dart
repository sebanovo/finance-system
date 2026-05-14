import 'package:dio/dio.dart';
import '../../../../core/network/api_client.dart';
import '../models/role.dart';
import '../../../domain/usecases/create_role_usecase.dart';
import '../../../domain/usecases/update_role_usecase.dart';

abstract class RoleRemoteDataSource {
  Future<List<RoleModel>> getRoles();
  Future<RoleModel> createRole(CreateRoleParams params);
  Future<RoleModel> activateRole(String id);
  Future<RoleModel> deactivateRole(String id);
  Future<RoleModel> getRole(String id);
  Future<RoleModel> updateRole(UpdateRoleParams params);
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

  @override
  Future<RoleModel> createRole(CreateRoleParams params) async {
    try {
      final response = await apiClient.post('/api/access/roles', {
        'name': params.name,
        'description': params.description,
        'permissionCodes': params.permissionCodes,
      });
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        return RoleModel.fromJson(decoded['data']);
      } else {
        throw Exception(decoded['message'] ?? 'Error al crear rol');
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

  @override
  Future<RoleModel> activateRole(String id) async {
    try {
      final response = await apiClient.patch(
        '/api/access/roles/$id/activate',
        null,
      );
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        return RoleModel.fromJson(decoded['data']);
      } else {
        throw Exception(decoded['message'] ?? 'Error al activar rol');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) throw Exception('Sesión expirada');
      final serverError = e.response?.data is Map
          ? e.response?.data['message']
          : null;
      throw Exception(serverError ?? 'Error de conexión: ${e.message}');
    } catch (e) {
      throw Exception('Error inesperado: $e');
    }
  }

  @override
  Future<RoleModel> deactivateRole(String id) async {
    try {
      final response = await apiClient.patch(
        '/api/access/roles/$id/deactivate',
        null,
      );
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        return RoleModel.fromJson(decoded['data']);
      } else {
        throw Exception(decoded['message'] ?? 'Error al desactivar rol');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) throw Exception('Sesión expirada');
      final serverError = e.response?.data is Map
          ? e.response?.data['message']
          : null;
      throw Exception(serverError ?? 'Error de conexión: ${e.message}');
    } catch (e) {
      throw Exception('Error inesperado: $e');
    }
  }

  @override
  Future<RoleModel> getRole(String id) async {
    try {
      final response = await apiClient.get('/api/access/roles/$id');
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        return RoleModel.fromJson(decoded['data']);
      } else {
        throw Exception(decoded['message'] ?? 'Error al obtener rol');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) throw Exception('Sesión expirada');
      throw Exception('Error de conexión: ${e.message}');
    } catch (e) {
      throw Exception('Error inesperado: $e');
    }
  }

  @override
  Future<RoleModel> updateRole(UpdateRoleParams params) async {
    try {
      final response = await apiClient.put('/api/access/roles/${params.id}', {
        'name': params.name,
        'description': params.description,
        'permissionCodes': params.permissionCodes,
      });
      final Map<String, dynamic> decoded = response.data;
      if (decoded['success'] == true) {
        return RoleModel.fromJson(decoded['data']);
      } else {
        throw Exception(decoded['message'] ?? 'Error al actualizar rol');
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) throw Exception('Sesión expirada');
      final serverError = e.response?.data is Map
          ? e.response?.data['message']
          : null;
      throw Exception(serverError ?? 'Error de conexión: ${e.message}');
    } catch (e) {
      throw Exception('Error inesperado: $e');
    }
  }
}
