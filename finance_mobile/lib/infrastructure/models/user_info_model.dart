import '../../../domain/entities/user_info.dart';

class UserInfoModel {
  final String tenantSlug;
  final String subject;
  final List<String> roles;

  UserInfoModel({
    required this.tenantSlug,
    required this.subject,
    required this.roles,
  });

  factory UserInfoModel.fromJson(Map<String, dynamic> json) {
    return UserInfoModel(
      tenantSlug: json['tenantSlug'] ?? '',
      subject: json['subject'] ?? '',
      roles: List<String>.from(json['roles'] ?? []),
    );
  }

  UserInfo toEntity() {
    return UserInfo(tenantSlug: tenantSlug, subject: subject, roles: roles);
  }
}
