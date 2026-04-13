class LoginResponse {
  final bool success;
  final String message;
  final LoginData? data;

  LoginResponse({required this.success, required this.message, this.data});

  factory LoginResponse.fromJson(Map<String, dynamic> json) {
    return LoginResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      data: json['data'] != null ? LoginData.fromJson(json['data']) : null,
    );
  }
}

class LoginData {
  final String tokenType;
  final String accessToken;
  final String refreshToken;
  final int accessExpiresInMs;

  LoginData({
    required this.tokenType,
    required this.accessToken,
    required this.refreshToken,
    required this.accessExpiresInMs,
  });

  factory LoginData.fromJson(Map<String, dynamic> json) {
    return LoginData(
      tokenType: json['tokenType'] ?? '',
      accessToken: json['accessToken'] ?? '',
      refreshToken: json['refreshToken'] ?? '',
      accessExpiresInMs: json['accessExpiresInMs'] ?? 0,
    );
  }
}
