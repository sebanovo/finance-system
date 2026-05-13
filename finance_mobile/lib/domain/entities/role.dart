class Role {
  final String id;
  final String name;
  final String description;
  final bool active;
  final List<String> permissionCodes;
  final DateTime? createdAt;

  Role({
    required this.id,
    required this.name,
    required this.description,
    required this.active,
    required this.permissionCodes,
    this.createdAt,
  });
}
