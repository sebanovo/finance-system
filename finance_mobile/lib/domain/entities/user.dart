class User {
  final String id;
  final String email;
  final String? firstName;
  final String? lastName;
  final bool active;

  User({
    required this.id,
    required this.email,
    this.firstName,
    this.lastName,
    required this.active,
  });

  String get fullName =>
      [firstName, lastName].where((e) => e != null && e.isNotEmpty).join(' ');
}
