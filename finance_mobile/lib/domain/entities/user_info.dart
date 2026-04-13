class UserInfo {
  final String tenantSlug;
  final String subject;
  final List<String> roles;

  UserInfo({
    required this.tenantSlug,
    required this.subject,
    required this.roles,
  });

  String get displayName => subject.split('@').first;
  String get initial => subject.isNotEmpty ? subject[0].toUpperCase() : 'U';
}
