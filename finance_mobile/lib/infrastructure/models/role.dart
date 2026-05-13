import 'package:finance_mobile/domain/entities/role.dart';

class RoleModel {
  final String id;
  final String name;
  final String description;
  final bool active;
  final List<String> permissionCodes;
  final DateTime? createdAt;

  RoleModel({
    required this.id,
    required this.name,
    required this.description,
    required this.active,
    required this.permissionCodes,
    this.createdAt,
  });

  factory RoleModel.fromJson(Map<String, dynamic> json) {
    return RoleModel(
      id: json["id"],
      name: json["name"],
      description: json["description"],
      active: json["active"],
      permissionCodes:
          (json["permissionCodes"] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
      createdAt: json["createdAt"] != null
          ? DateTime.tryParse(json["createdAt"])
          : null,
    );
  }

  Role toEntity() {
    return Role(
      id: id,
      name: name,
      description: description,
      active: active,
      permissionCodes: permissionCodes,
      createdAt: createdAt,
    );
  }
}
