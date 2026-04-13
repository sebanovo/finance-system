class SignupRequest {
  final String companyName;
  final String tenantSlug;
  final String adminEmail;
  final String password;
  final String firstName;
  final String lastName;

  SignupRequest({
    required this.companyName,
    required this.tenantSlug,
    required this.adminEmail,
    required this.password,
    required this.firstName,
    required this.lastName,
  });

  Map<String, dynamic> toJson() => {
    'companyName': companyName,
    'tenantSlug': tenantSlug,
    'adminEmail': adminEmail,
    'password': password,
    'firstName': firstName,
    'lastName': lastName,
  };
}
