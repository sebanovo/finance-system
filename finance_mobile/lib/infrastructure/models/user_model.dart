import '../../../domain/entities/user.dart';

class UserModel {
  final String id;
  final String email;
  final String? firstName;
  final String? lastName;
  final bool active;

  UserModel({
    required this.id,
    required this.email,
    this.firstName,
    this.lastName,
    required this.active,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      firstName: json['firstName'],
      lastName: json['lastName'],
      active: json['active'] ?? false,
    );
  }

  User toEntity() {
    return User(
      id: id,
      email: email,
      firstName: firstName,
      lastName: lastName,
      active: active,
    );
  }
}
