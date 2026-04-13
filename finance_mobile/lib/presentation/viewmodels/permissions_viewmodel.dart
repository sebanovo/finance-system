import 'package:flutter/material.dart';
import '../../../../domain/usecases/get_permissions_usecase.dart';
import '../../../../domain/entities/permission.dart';

class PermissionsViewModel extends ChangeNotifier {
  final GetPermissionsUsecase getPermissionsUseCase;

  List<Permission> _permissions = [];
  bool _loading = false;
  String? _errorMessage;

  PermissionsViewModel(this.getPermissionsUseCase);

  List<Permission> get permissions => _permissions;
  bool get loading => _loading;
  String? get errorMessage => _errorMessage;

  Future<void> loadPermissions() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final result = await getPermissionsUseCase();
      _permissions = result;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Map<String, List<Permission>> get groupByModule {
    final map = <String, List<Permission>>{};
    for (var perm in _permissions) {
      final module = perm.module.isNotEmpty ? perm.module : 'otros';
      map.putIfAbsent(module, () => []).add(perm);
    }
    return map;
  }
}
