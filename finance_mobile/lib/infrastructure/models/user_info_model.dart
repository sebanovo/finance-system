import '../../../domain/entities/user_info.dart';

class UserInfoModel {
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

  UserInfoModel({
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

  factory UserInfoModel.fromJson(Map<String, dynamic> json) {
    final data = json['data'] as Map<String, dynamic>;
    return UserInfoModel(
      id: data['id'] ?? '',
      email: data['email'] ?? '',
      firstName: data['firstName'],
      lastName: data['lastName'],
      active: data['active'] ?? false,
      status: data['status'] ?? '',
      tenantSlug: data['tenantSlug'] ?? '',
      roles: List<String>.from(data['roles'] ?? []),
      profilePhotoUrl: data['profilePhotoUrl'],
      profilePhotoContentType: data['profilePhotoContentType'],
    );
  }

  factory UserInfoModel.fromMergedJson(Map<String, dynamic> data) {
    return UserInfoModel(
      id: data['id'] ?? '',
      email: data['email'] ?? '',
      firstName: data['firstName'],
      lastName: data['lastName'],
      active: data['active'] ?? false,
      status: data['status'] ?? '',
      tenantSlug: data['tenantSlug'] ?? '',
      roles: List<String>.from(data['roles'] ?? []),
      profilePhotoUrl: data['profilePhotoUrl'],
      profilePhotoContentType: data['profilePhotoContentType'],
    );
  }

  UserInfo toEntity() {
    return UserInfo(
      id: id,
      email: email,
      firstName: firstName,
      lastName: lastName,
      active: active,
      status: status,
      tenantSlug: tenantSlug,
      roles: roles,
      profilePhotoUrl: profilePhotoUrl,
      profilePhotoContentType: profilePhotoContentType,
    );
  }
}
