import 'package:dio/dio.dart';

import '../../core/network/api_client.dart';
import '../models/model_parsers.dart';
import '../models/service_bill_model.dart';
import '../models/service_provider_catalog_model.dart';
import '../models/service_enrollment_model.dart';
import '../models/service_payment_model.dart';
import '../models/service_provider_model.dart';

abstract class ServicePaymentsRemoteDataSource {
  Future<List<ServiceProviderModel>> getServiceProviders({
    String? search,
    String? category,
    String? status,
    int page,
    int size,
  });

  Future<List<ServiceProviderCatalogModel>> getServiceProviderCatalog();

  Future<List<ServiceEnrollmentModel>> getServiceEnrollments({
    String? providerId,
    String? category,
    String? status,
    String? search,
    int page,
    int size,
  });

  Future<ServiceEnrollmentModel> createServiceEnrollment(
    Map<String, dynamic> request,
  );

  Future<ServiceEnrollmentModel> deleteServiceEnrollment(String enrollmentId);

  Future<ServiceBillsQueryResultModel> queryServiceBills(
    Map<String, dynamic> request,
  );

  Future<ServicePaymentModel> createServicePayment(
    Map<String, dynamic> request,
  );

  Future<List<ServicePaymentModel>> getServicePayments({
    String? providerId,
    String? receiptNumber,
    String? billId,
    int page,
    int size,
  });

  Future<ServicePaymentModel> getServicePayment(String paymentId);
}

class ServicePaymentsRemoteDataSourceImpl
    implements ServicePaymentsRemoteDataSource {
  final ApiClient apiClient;

  ServicePaymentsRemoteDataSourceImpl(this.apiClient);

  @override
  Future<List<ServiceProviderModel>> getServiceProviders({
    String? search,
    String? category,
    String? status,
    int page = 0,
    int size = 50,
  }) async {
    final response = await apiClient.get(
      '/api/me/service-providers',
      queryParameters: {
        if (search != null && search.trim().isNotEmpty) 'search': search.trim(),
        if (category != null && category.trim().isNotEmpty) 'category': category.trim(),
        if (status != null && status.trim().isNotEmpty) 'status': status.trim(),
        'page': page,
        'size': size,
      },
    );
    return _parseListResponse(
      response,
      (json) => ServiceProviderModel.fromJson(json),
      fallbackMessage: 'Error al obtener proveedores de servicios',
    );
  }

  @override
  Future<List<ServiceProviderCatalogModel>> getServiceProviderCatalog() async {
    final response = await apiClient.get('/api/me/service-providers/catalog');
    return _parseListResponse(
      response,
      (json) => ServiceProviderCatalogModel.fromJson(json),
      fallbackMessage: 'Error al obtener catálogo de proveedores de servicios',
    );
  }

  @override
  Future<List<ServiceEnrollmentModel>> getServiceEnrollments({
    String? providerId,
    String? category,
    String? status,
    String? search,
    int page = 0,
    int size = 50,
  }) async {
    final response = await apiClient.get(
      '/api/me/service-enrollments',
      queryParameters: {
        if (providerId != null && providerId.trim().isNotEmpty) 'providerId': providerId.trim(),
        if (category != null && category.trim().isNotEmpty) 'category': category.trim(),
        if (status != null && status.trim().isNotEmpty) 'status': status.trim(),
        if (search != null && search.trim().isNotEmpty) 'search': search.trim(),
        'page': page,
        'size': size,
      },
    );
    return _parseListResponse(
      response,
      (json) => ServiceEnrollmentModel.fromJson(json),
      fallbackMessage: 'Error al obtener afiliaciones de servicios',
    );
  }

  @override
  Future<ServiceEnrollmentModel> createServiceEnrollment(
    Map<String, dynamic> request,
  ) async {
    final response = await apiClient.post('/api/me/service-enrollments', request);
    return _parseSingleResponse(
      response,
      (json) => ServiceEnrollmentModel.fromJson(json),
      fallbackMessage: 'Error al afiliar servicio',
    );
  }

  @override
  Future<ServiceEnrollmentModel> deleteServiceEnrollment(
    String enrollmentId,
  ) async {
    final response = await apiClient.delete('/api/me/service-enrollments/$enrollmentId');
    return _parseSingleResponse(
      response,
      (json) => ServiceEnrollmentModel.fromJson(json),
      fallbackMessage: 'Error al eliminar afiliación',
    );
  }

  @override
  Future<ServiceBillsQueryResultModel> queryServiceBills(
    Map<String, dynamic> request,
  ) async {
    final response = await apiClient.post('/api/me/service-bills/query', request);
    return _parseSingleResponse(
      response,
      (json) => ServiceBillsQueryResultModel.fromJson(json),
      fallbackMessage: 'Error al consultar deudas de servicios',
    );
  }

  @override
  Future<ServicePaymentModel> createServicePayment(
    Map<String, dynamic> request,
  ) async {
    final response = await apiClient.post('/api/me/service-payments', request);
    return _parseSingleResponse(
      response,
      (json) => ServicePaymentModel.fromJson(json),
      fallbackMessage: 'Error al completar pago de servicio',
    );
  }

  @override
  Future<List<ServicePaymentModel>> getServicePayments({
    String? providerId,
    String? receiptNumber,
    String? billId,
    int page = 0,
    int size = 50,
  }) async {
    final response = await apiClient.get(
      '/api/me/service-payments',
      queryParameters: {
        if (providerId != null && providerId.trim().isNotEmpty) 'providerId': providerId.trim(),
        if (receiptNumber != null && receiptNumber.trim().isNotEmpty) 'receiptNumber': receiptNumber.trim(),
        if (billId != null && billId.trim().isNotEmpty) 'billId': billId.trim(),
        'page': page,
        'size': size,
      },
    );
    return _parseListResponse(
      response,
      (json) => ServicePaymentModel.fromJson(json),
      fallbackMessage: 'Error al obtener pagos de servicios',
    );
  }

  @override
  Future<ServicePaymentModel> getServicePayment(String paymentId) async {
    final response = await apiClient.get('/api/me/service-payments/$paymentId');
    return _parseSingleResponse(
      response,
      (json) => ServicePaymentModel.fromJson(json),
      fallbackMessage: 'Error al obtener detalle del pago',
    );
  }

  List<T> _parseListResponse<T>(
    Response response,
    T Function(Map<String, dynamic>) mapper, {
    required String fallbackMessage,
  }) {
    if (response.statusCode == 200) {
      final body = asMap(response.data);
      if (body != null && body['success'] == true) {
        final payload = body['data'];
        return extractMapList(payload).map(mapper).toList();
      }
      throw Exception(body?['message'] ?? fallbackMessage);
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception(fallbackMessage);
    }
  }

  T _parseSingleResponse<T>(
    Response response,
    T Function(Map<String, dynamic>) mapper, {
    required String fallbackMessage,
  }) {
    if (response.statusCode == 200 || response.statusCode == 201) {
      final body = asMap(response.data);
      if (body != null && body['success'] == true) {
        final payload = asMap(body['data']);
        if (payload != null) {
          return mapper(payload);
        }
      }
      throw Exception(body?['message'] ?? fallbackMessage);
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else if (response.statusCode == 404) {
      throw Exception('Recurso no encontrado');
    } else {
      final body = asMap(response.data);
      throw Exception(body?['message'] ?? fallbackMessage);
    }
  }
}
