// class UserInfo {
//   final String tenantSlug;
//   final String subject;
//   final List<String> roles;

//   UserInfo({
//     required this.tenantSlug,
//     required this.subject,
//     required this.roles,
//   });

//   String get displayName => subject.split('@').first;
//   String get initial => subject.isNotEmpty ? subject[0].toUpperCase() : 'U';
// }

// lib/features/auth/domain/entities/user_info.dart
class UserInfo {
  final String id;
  final String email;
  final String? firstName;
  final String? lastName;
  final bool active;
  final String status;
  final String tenantSlug;
  final List<String> roles;

  UserInfo({
    required this.id,
    required this.email,
    this.firstName,
    this.lastName,
    required this.active,
    required this.status,
    required this.tenantSlug,
    required this.roles,
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
