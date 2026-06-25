import '../../../../core/network/api_client.dart';
import '../models/user_model.dart';
import '../models/role.dart';
import '../models/create_user_request.dart';
import '../models/user_info_model.dart';

abstract class UserRemoteDataSource {
  Future<List<UserModel>> getUsers();
  Future<List<RoleModel>> getUserRoles(String userId);
  Future<List<RoleModel>> getAvailableRoles();
  Future<void> assignRole(String userId, List<String> roleIds);
  Future<void> createUser(CreateUserRequest request);
  Future<void> toggleUserStatus(String userId, bool currentlyActive);
  Future<UserInfoModel> getUserInfo();
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
  Future<void> assignRole(String userId, List<String> roleIds) async {
    final response = await apiClient.put('/api/access/users/$userId/roles', {
      'roleIds': roleIds,
    });
    if (response.statusCode != 200) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al asignar rol');
    }
  }

  @override
  Future<void> toggleUserStatus(String userId, bool currentlyActive) async {
    final endpoint = currentlyActive ? '/api/users/$userId/deactivate' : '/api/users/$userId/activate';
    final response = await apiClient.patch(endpoint, {});
    if (response.statusCode != 200) {
      final error = response.data as Map<String, dynamic>;
      throw Exception(error['message'] ?? 'Error al cambiar el estado del usuario');
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

  // @override
  // Future<UserInfoModel> getUserInfo() async {
  //   final response = await apiClient.get('/api/secure/me');
  //   if (response.statusCode == 200) {
  //     final data = response.data as Map<String, dynamic>;
  //     if (data['success'] == true) {
  //       return UserInfoModel.fromJson(data['data']);
  //     } else {
  //       throw Exception(data['message'] ?? 'Error al obtener información');
  //     }
  //   } else {
  //     throw Exception('Error ${response.statusCode}');
  //   }
  // }
  @override
  Future<UserInfoModel> getUserInfo() async {
    final meResponse = await apiClient.get('/api/auth/me');
    if (meResponse.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    if (meResponse.statusCode != 200) {
      throw Exception('Error ${meResponse.statusCode}');
    }

    final meData = meResponse.data as Map<String, dynamic>;
    if (meData['success'] != true) {
      throw Exception(meData['message'] ?? 'Error al obtener información');
    }

    final profileResponse = await apiClient.get('/api/auth/profile');
    Map<String, dynamic>? profileData;
    if (profileResponse.statusCode == 200) {
      final data = profileResponse.data as Map<String, dynamic>;
      if (data['success'] == true && data['data'] is Map<String, dynamic>) {
        profileData = data['data'] as Map<String, dynamic>;
      }
    }

    final merged = <String, dynamic>{
      ...meData['data'] as Map<String, dynamic>,
      if (profileData != null) ...profileData,
    };

    return UserInfoModel.fromMergedJson(merged);
  }
}
