import 'package:flutter/material.dart';
import '../../../../domain/usecases/get_roles_usecase.dart';
import '../../../../domain/entities/role.dart';

class RolesViewModel extends ChangeNotifier {
  final GetRolesUsecase getRolesUseCase;

  List<Role> _roles = [];
  bool _loading = false;
  String? _errorMessage;

  RolesViewModel(this.getRolesUseCase);

  List<Role> get roles => _roles;
  bool get loading => _loading;
  String? get errorMessage => _errorMessage;

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
}
