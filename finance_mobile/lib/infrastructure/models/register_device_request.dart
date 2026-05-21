class RegisterDeviceRequest {
  final String deviceId;
  final String fcmToken;
  final String platform;
  final String deviceName;
  final String appVersion;
  final String osVersion;

  RegisterDeviceRequest({
    required this.deviceId,
    required this.fcmToken,
    required this.platform,
    required this.deviceName,
    required this.appVersion,
    required this.osVersion,
  });

  Map<String, dynamic> toJson() {
    final json = {
      'deviceId': deviceId,
      'fcmToken': fcmToken,
      'platform': platform,
      'deviceName': deviceName,
      'appVersion': appVersion,
      'osVersion': osVersion,
    };

    return json;
  }
}
