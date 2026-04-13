class User {
  final String email;
  final String tenantSlug;
  final List<String> roles;

  User({required this.email, required this.tenantSlug, required this.roles});
}
