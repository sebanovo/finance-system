import 'package:flutter/foundation.dart';
import 'package:local_auth/local_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';

typedef StoredBiometricCredentials = ({
  String tenantSlug,
  String email,
  String password,
});

class BiometricAuthService {
  static const String _biometricEnabledKey = 'biometricEnabled';
  static const String _biometricPromptSeenKey = 'biometricPromptSeen';
  static const String _credentialTenantKey = 'biometricTenantSlug';
  static const String _credentialEmailKey = 'biometricEmail';
  static const String _credentialPasswordKey = 'biometricPassword';

  final LocalAuthentication _localAuth = LocalAuthentication();
  StoredBiometricCredentials? _cachedCredentials;

  Future<bool> isBiometricAvailable() async {
    if (kIsWeb) {
      return false;
    }

    try {
      final isSupported = await _localAuth.isDeviceSupported();
      final canCheck = await _localAuth.canCheckBiometrics;
      return isSupported && canCheck;
    } catch (_) {
      return false;
    }
  }

  Future<bool> isBiometricEnabled() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_biometricEnabledKey) ?? false;
  }

  Future<void> setBiometricEnabled(bool enabled) async {
    final prefs = await SharedPreferences.getInstance();
    if (enabled) {
      await prefs.setBool(_biometricEnabledKey, true);
    } else {
      await prefs.remove(_biometricEnabledKey);
    }
  }

  Future<bool> hasSeenBiometricPrompt() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_biometricPromptSeenKey) ?? false;
  }

  Future<void> markBiometricPromptSeen() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_biometricPromptSeenKey, true);
  }

  Future<void> resetBiometricPrompt() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_biometricPromptSeenKey);
  }

  Future<void> saveCredentials({
    required String tenantSlug,
    required String email,
    required String password,
  }) async {
    _cachedCredentials = (
      tenantSlug: tenantSlug,
      email: email,
      password: password,
    );

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_credentialTenantKey, tenantSlug);
    await prefs.setString(_credentialEmailKey, email);
    await prefs.setString(_credentialPasswordKey, password);
  }

  Future<StoredBiometricCredentials?> readCredentials() async {
    final cached = _cachedCredentials;
    if (cached != null &&
        cached.tenantSlug.isNotEmpty &&
        cached.email.isNotEmpty &&
        cached.password.isNotEmpty) {
      return cached;
    }

    final prefs = await SharedPreferences.getInstance();
    final tenantSlug = prefs.getString(_credentialTenantKey);
    final email = prefs.getString(_credentialEmailKey);
    final password = prefs.getString(_credentialPasswordKey);

    if (tenantSlug == null ||
        tenantSlug.isEmpty ||
        email == null ||
        email.isEmpty ||
        password == null ||
        password.isEmpty) {
      return null;
    }

    final credentials = (
      tenantSlug: tenantSlug,
      email: email,
      password: password,
    );

    _cachedCredentials = credentials;
    return credentials;
  }

  Future<bool> hasStoredCredentials() async {
    return (await readCredentials()) != null;
  }

  Future<void> clearCredentials() async {
    _cachedCredentials = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_credentialTenantKey);
    await prefs.remove(_credentialEmailKey);
    await prefs.remove(_credentialPasswordKey);
  }

  Future<bool> authenticateUser({
    String localizedReason = 'Autenticá tu sesión con huella digital',
  }) async {
    final enabled = await isBiometricEnabled();
    if (!enabled) {
      return false;
    }

    final storedCredentials = await readCredentials();
    if (storedCredentials == null) {
      await setBiometricEnabled(false);
      return false;
    }

    final available = await isBiometricAvailable();
    if (!available) {
      return false;
    }

    bool authenticated = false;
    try {
      authenticated = await _localAuth.authenticate(
        localizedReason: localizedReason,
        options: const AuthenticationOptions(
          biometricOnly: true,
          stickyAuth: true,
          useErrorDialogs: true,
        ),
      );
    } catch (_) {
      return false;
    }

    if (!authenticated) {
      return false;
    }

    return true;
  }

}
