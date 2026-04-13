import 'package:finance_mobile/domain/entities/role.dart';

class RoleModel {
  final String id;
  final String name;
  final String description;
  final bool active;
  final List<String> permissionsCodes;

  RoleModel({
    required this.id,
    required this.name,
    required this.description,
    required this.active,
    required this.permissionsCodes,
  });
  factory RoleModel.fromJson(Map<String, dynamic> json) {
    return RoleModel(
      id: json["id"],
      name: json["name"],
      description: json["description"],
      active: json["active"],
      permissionsCodes: json["permissionsCodes"] ?? [],
    );
  }

  Role toEntity() {
    return Role(
      id: id,
      name: name,
      description: description,
      active: active,
      permissionsCodes: permissionsCodes,
    );
  }
}
