import 'package:flutter/material.dart';
import '../../../../domain/usecases/get_roles_usecase.dart';
import '../../../../domain/usecases/create_role_usecase.dart';
import '../../../../domain/entities/role.dart';

class RolesViewModel extends ChangeNotifier {
  final GetRolesUsecase getRolesUseCase;
  final CreateRoleUseCase createRoleUseCase;

  List<Role> _roles = [];
  bool _loading = false;
  bool _creating = false;
  String? _errorMessage;
  bool _roleCreated = false;

  RolesViewModel({
    required this.getRolesUseCase,
    required this.createRoleUseCase,
  });

  List<Role> get roles => _roles;
  bool get loading => _loading;
  bool get creating => _creating;
  String? get errorMessage => _errorMessage;
  bool get roleCreated => _roleCreated;

  void clearRoleCreated() {
    _roleCreated = false;
  }

  Future<void> loadRoles() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final result = await getRolesUseCase();
      _roles = result;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> createRole(CreateRoleParams params) async {
    _creating = true;
    _errorMessage = null;
    _roleCreated = false;
    notifyListeners();

    try {
      await createRoleUseCase(params);
      _roleCreated = true;
      await loadRoles();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creating = false;
      notifyListeners();
    }
  }
}
