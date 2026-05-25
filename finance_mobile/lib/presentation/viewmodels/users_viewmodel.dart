import 'package:flutter/material.dart';
import '../../../domain/entities/user.dart';
import '../../../domain/entities/role.dart';
import '../../../domain/usecases/get_users_usecase.dart';
import '../../../domain/usecases/get_user_roles_usecase.dart';
import '../../../domain/usecases/assign_role_usecase.dart';
import '../../../domain/usecases/create_user_usecase.dart';
import '../../../domain/usecases/get_available_roles_usecase.dart';

class UsersViewModel extends ChangeNotifier {
  final GetUsersUseCase getUsersUseCase;
  final GetUserRolesUseCase getUserRolesUseCase;
  final AssignRoleUseCase assignRoleUseCase;
  final CreateUserUseCase createUserUseCase;
  final GetAvailableRolesUseCase getAvailableRolesUseCase;

  List<User> _users = [];
  final Map<String, List<Role>> _rolesMap = {};
  final Map<String, bool> _loadingRolesMap = {};
  List<Role> _availableRoles = [];
  bool _loading = true;
  bool _loadingRolesList = true;
  String? _errorMessage;

  UsersViewModel({
    required this.getUsersUseCase,
    required this.getUserRolesUseCase,
    required this.assignRoleUseCase,
    required this.createUserUseCase,
    required this.getAvailableRolesUseCase,
  }) {
    loadAvailableRoles();
  }

  // Getters
  List<User> get users => _users;
  Map<String, List<Role>> get rolesMap => _rolesMap;
  Map<String, bool> get loadingRolesMap => _loadingRolesMap;
  List<Role> get availableRoles => _availableRoles;
  bool get loading => _loading;
  bool get loadingRolesList => _loadingRolesList;
  String? get errorMessage => _errorMessage;

  Future<void> loadAvailableRoles() async {
    _loadingRolesList = true;
    notifyListeners();
    try {
      _availableRoles = await getAvailableRolesUseCase();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingRolesList = false;
      notifyListeners();
    }
    if (_errorMessage == null) {
      await loadUsers();
    }
  }

  Future<void> loadUsers() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      _users = await getUsersUseCase();
      // ✅ Cargar roles sin notificar por cada uno
      await _loadAllUserRoles();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  // ✅ Nuevo método: cargar todos los roles de una vez sin notificaciones intermedias
  Future<void> _loadAllUserRoles() async {
    // Marcar todos como cargando
    for (var user in _users) {
      _loadingRolesMap[user.id] = true;
    }
    // ✅ Notificar solo una vez al inicio
    notifyListeners();

    // Cargar roles en paralelo
    final futures = _users.map((user) async {
      try {
        final roles = await getUserRolesUseCase(user.id);
        _rolesMap[user.id] = roles;
      } catch (e) {
        _rolesMap[user.id] = [];
      } finally {
        _loadingRolesMap[user.id] = false;
      }
    });

    await Future.wait(futures);

    // ✅ Notificar solo una vez al final
    notifyListeners();
  }

  Future<void> loadUserRoles(String userId) async {
    // ✅ No notificar al inicio
    _loadingRolesMap[userId] = true;

    try {
      final roles = await getUserRolesUseCase(userId);
      _rolesMap[userId] = roles;
    } catch (e) {
      _rolesMap[userId] = [];
    } finally {
      _loadingRolesMap[userId] = false;
      // ✅ Notificar solo una vez al final
      notifyListeners();
    }
  }

  Future<void> assignRole(String userId, String roleId) async {
    try {
      await assignRoleUseCase(userId, roleId);
      await loadUserRoles(userId);
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
      rethrow;
    }
  }

  Future<void> createUser({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    try {
      await createUserUseCase(email, password, firstName, lastName);
      await loadUsers(); // recarga toda la lista y roles
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
      rethrow;
    }
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
