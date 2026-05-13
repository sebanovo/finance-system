import 'package:dio/dio.dart';
import '../../constants/env.dart';

class ApiClient {
  final Dio dio;

  String? token;
  String? tenantSlug;

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
      ),
    );
  }

  void setToken(String token) {
    this.token = token;
  }

  void setTenant(String slug) {
    tenantSlug = slug;
  }

  // GET
  Future<Response> get(String path) {
    return dio.get(path);
  }

  // POST
  Future<Response> post(String path, dynamic data) {
    return dio.post(path, data: data);
  }

  // PUT
  Future<Response> put(String path, dynamic data) {
    return dio.put(path, data: data);
  }

  // PATCH
  Future<Response> patch(String path, dynamic data) {
    return dio.patch(path, data: data);
  }

  // DELETE
  Future<Response> delete(String path) {
    return dio.delete(path);
  }
}
