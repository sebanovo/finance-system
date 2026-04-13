import '../../domain/entities/permission.dart';

class PermissionModel {
  final String code;
  final String module;
  final String description;

  PermissionModel({
    required this.code,
    required this.module,
    required this.description,
  });

  factory PermissionModel.fromJson(Map<String, dynamic> json) {
    return PermissionModel(
      code: json['code'],
      module: json['module'],
      description: json['description'],
    );
  }

  Permission toEntity() {
    return Permission(code: code, module: module, description: description);
  }
}
