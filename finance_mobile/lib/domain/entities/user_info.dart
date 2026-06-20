class UserInfo {
  final String id;
  final String email;
  final String? firstName;
  final String? lastName;
  final bool active;
  final String status;
  final String tenantSlug;
  final List<String> roles;
  final String? profilePhotoUrl;
  final String? profilePhotoContentType;

  UserInfo({
    required this.id,
    required this.email,
    this.firstName,
    this.lastName,
    required this.active,
    required this.status,
    required this.tenantSlug,
    required this.roles,
    this.profilePhotoUrl,
    this.profilePhotoContentType,
  });

  String get displayName {
    if (firstName != null && lastName != null) {
      return '$firstName $lastName';
    } else if (firstName != null) {
      return firstName!;
    } else {
      return email.split('@').first;
    }
  }

  String get initial {
    if (firstName != null && firstName!.isNotEmpty) {
      return firstName![0].toUpperCase();
    }
    return email.isNotEmpty ? email[0].toUpperCase() : 'U';
  }
}
