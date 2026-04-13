import '../../../../core/network/api_client.dart';
import '../models/user_model.dart';
import '../models/role.dart';
import '../models/create_user_request.dart';

abstract class UserRemoteDataSource {
  Future<List<UserModel>> getUsers();
  Future<List<RoleModel>> getUserRoles(String userId);
  Future<List<RoleModel>> getAvailableRoles();
  Future<void> assignRole(String userId, String roleId);
  Future<void> createUser(CreateUserRequest request);
}

class UserRemoteDataSourceImpl implements UserRemoteDataSource {
  final ApiClient apiClient;

  UserRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<UserModel>> getUsers() async {
    final response = await apiClient.get('/api/users');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => UserModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al obtener usuarios');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<List<RoleModel>> getUserRoles(String userId) async {
    final response = await apiClient.get('/api/access/users/$userId/roles');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final rolesData = data['data']['roles'] as List<dynamic>? ?? [];
        return rolesData.map((json) => RoleModel.fromJson(json)).toList();
      } else {
        return [];
      }
    } else {
      return [];
    }
  }

  @override
  Future<List<RoleModel>> getAvailableRoles() async {
    final response = await apiClient.get('/api/access/roles');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final list = data['data'] as List<dynamic>? ?? [];
        return list.map((json) => RoleModel.fromJson(json)).toList();
      } else {
        throw Exception(data['message'] ?? 'Error al cargar roles');
      }
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }

  @override
  Future<void> assignRole(String userId, String roleId) async {
    final response = await apiClient.put('/api/access/users/$userId/roles', {
      'roleIds': [roleId],
    });
    if (response.statusCode != 200) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al asignar rol');
    }
  }

  @override
  Future<void> createUser(CreateUserRequest request) async {
    final response = await apiClient.post('/api/users', request.toJson());
    if (response.statusCode != 200 && response.statusCode != 201) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al crear usuario');
    }
  }
}
