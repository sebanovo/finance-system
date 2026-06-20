import 'package:dio/dio.dart';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../constants/env.dart';

class ApiClient extends ChangeNotifier {
  final Dio dio;
  final Dio _refreshDio;

  String? token;
  String? tenantSlug;
  String? refreshToken;
  List<String> _roles = [];
  List<String> _permissions = [];
  Future<bool>? _refreshing;

  ApiClient()
    : dio = Dio(
        BaseOptions(
          baseUrl: Env.baseUrl,
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          validateStatus: (status) => status != null && status < 500,
        ),
      ),
      _refreshDio = Dio(
        BaseOptions(
          baseUrl: Env.baseUrl,
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          validateStatus: (status) => status != null && status < 500,
        ),
      ) {
    // Interceptor para agregar headers dinámicos
    dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          if (token != null) {
            options.headers["Authorization"] = "Bearer $token";
          }
          if (tenantSlug != null) {
            options.headers["X-Tenant-Slug"] = tenantSlug;
          }
          return handler.next(options);
        },
        onResponse: (response, handler) async {
          if (_shouldAttemptRefresh(response)) {
            final refreshed = await _refreshSession();
            if (refreshed) {
              final retryOptions = _cloneRequestOptions(
                response.requestOptions,
                skipRefresh: true,
              );
              final retryResponse = await dio.fetch(retryOptions);
              return handler.resolve(retryResponse);
            }
          }
          return handler.next(response);
        },
      ),
    );
  }

  void setToken(String token) {
    this.token = token;
    _syncClaimsFromToken(token);
    notifyListeners();
  }

  void setTenant(String slug) {
    tenantSlug = slug;
    notifyListeners();
  }

  void setSession({
    required String token,
    required String tenantSlug,
    String? refreshToken,
  }) {
    this.token = token;
    this.tenantSlug = tenantSlug;
    this.refreshToken = refreshToken;
    _syncClaimsFromToken(token);
    notifyListeners();
  }

  bool get hasSession =>
      token != null &&
      token!.isNotEmpty &&
      tenantSlug != null &&
      tenantSlug!.isNotEmpty;

  void clearSession() {
    token = null;
    tenantSlug = null;
    refreshToken = null;
    _roles = [];
    _permissions = [];
    notifyListeners();
  }

  List<String> get roles => List.unmodifiable(_roles);

  List<String> get permissions => List.unmodifiable(_permissions);

  bool get isOwnerAdmin => _roles.contains('OWNER_ADMIN');

  bool hasPermission(String code) => _permissions.contains(code);

  bool hasAnyPermissionPrefix(String prefix) {
    return _permissions.any((permission) => permission.startsWith(prefix));
  }

  Future<bool> _refreshSession() async {
    if (_refreshing != null) {
      return _refreshing!;
    }

    _refreshing = _performRefresh().whenComplete(() {
      _refreshing = null;
    });

    return _refreshing!;
  }

  Future<bool> _performRefresh() async {
    final currentRefreshToken = refreshToken;
    final currentTenantSlug = tenantSlug;

    if (currentRefreshToken == null ||
        currentRefreshToken.isEmpty ||
        currentTenantSlug == null ||
        currentTenantSlug.isEmpty) {
      return false;
    }

    try {
      final response = await _refreshDio.post(
        '/api/auth/refresh',
        data: {
          'refreshToken': currentRefreshToken,
        },
        options: Options(
          headers: {
            'X-Tenant-Slug': currentTenantSlug,
          },
        ),
      );

      if (response.statusCode != 200) {
        await _clearPersistedSession();
        return false;
      }

      final data = response.data;
      if (data is! Map<String, dynamic>) {
        await _clearPersistedSession();
        return false;
      }

      final payload = data['data'];
      if (payload is! Map<String, dynamic>) {
        await _clearPersistedSession();
        return false;
      }

      final newAccessToken = payload['accessToken'] as String?;
      final newRefreshToken = payload['refreshToken'] as String?;

      if (newAccessToken == null || newAccessToken.isEmpty) {
        await _clearPersistedSession();
        return false;
      }

      token = newAccessToken;
      _syncClaimsFromToken(newAccessToken);
      if (newRefreshToken != null && newRefreshToken.isNotEmpty) {
        refreshToken = newRefreshToken;
      }

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('accessToken', newAccessToken);
      await prefs.setString('tenantSlug', currentTenantSlug);
      if (newRefreshToken != null && newRefreshToken.isNotEmpty) {
        await prefs.setString('refreshToken', newRefreshToken);
      }

      return true;
    } catch (_) {
      await _clearPersistedSession();
      return false;
    }
  }

  Future<void> _clearPersistedSession() async {
    clearSession();
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('accessToken');
    await prefs.remove('refreshToken');
    await prefs.remove('tenantSlug');
  }

  void _syncClaimsFromToken(String jwt) {
    try {
      final parts = jwt.split('.');
      if (parts.length < 2) {
        _roles = [];
        _permissions = [];
        return;
      }

      final payload = parts[1];
      final normalized = base64Url.normalize(payload);
      final decoded = utf8.decode(base64Url.decode(normalized));
      final Map<String, dynamic> claims = jsonDecode(decoded) as Map<String, dynamic>;

      _roles = _readStringListClaim(claims, 'roles');
      _permissions = _readStringListClaim(claims, 'permissions');
    } catch (_) {
      _roles = [];
      _permissions = [];
    }
  }

  List<String> _readStringListClaim(Map<String, dynamic> claims, String key) {
    final value = claims[key];
    if (value is List) {
      return value
          .whereType<String>()
          .map((item) => item.trim())
          .where((item) => item.isNotEmpty)
          .toList();
    }
    return [];
  }

  bool _shouldAttemptRefresh(Response response) {
    if (response.statusCode != 401) {
      return false;
    }

    final path = response.requestOptions.path;
    if (path.contains('/api/auth/login') ||
        path.contains('/api/auth/face/login') ||
        path.contains('/api/auth/refresh') ||
        path.contains('/api/auth/logout') ||
        path.contains('/api/public/signup')) {
      return false;
    }

    final extra = response.requestOptions.extra;
    if (extra['skipRefresh'] == true) {
      return false;
    }

    return refreshToken != null &&
        refreshToken!.isNotEmpty &&
        tenantSlug != null &&
        tenantSlug!.isNotEmpty;
  }

  RequestOptions _cloneRequestOptions(
    RequestOptions requestOptions, {
    required bool skipRefresh,
  }) {
    return RequestOptions(
      path: requestOptions.path,
      method: requestOptions.method,
      baseUrl: requestOptions.baseUrl,
      data: requestOptions.data,
      queryParameters: requestOptions.queryParameters,
      headers: Map<String, dynamic>.from(requestOptions.headers),
      extra: {
        ...requestOptions.extra,
        'skipRefresh': skipRefresh,
      },
      contentType: requestOptions.contentType,
      responseType: requestOptions.responseType,
      followRedirects: requestOptions.followRedirects,
      validateStatus: requestOptions.validateStatus,
      receiveDataWhenStatusError: requestOptions.receiveDataWhenStatusError,
      receiveTimeout: requestOptions.receiveTimeout,
      sendTimeout: requestOptions.sendTimeout,
    );
  }

  // GET
  Future<Response> get(String path) {
    return dio.get(path);
  }

  // POST
  Future<Response> post(String path, dynamic data) {
    final options = data is FormData
        ? Options(contentType: 'multipart/form-data')
        : null;
    return dio.post(path, data: data, options: options);
  }

  // PUT
  Future<Response> put(String path, dynamic data) {
    final options = data is FormData
        ? Options(contentType: 'multipart/form-data')
        : null;
    return dio.put(path, data: data, options: options);
  }

  // PATCH
  Future<Response> patch(String path, dynamic data) {
    final options = data is FormData
        ? Options(contentType: 'multipart/form-data')
        : null;
    return dio.patch(path, data: data, options: options);
  }

  // DELETE
  Future<Response> delete(String path) {
    return dio.delete(path);
  }
}
