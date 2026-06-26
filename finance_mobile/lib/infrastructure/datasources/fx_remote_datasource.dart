import 'package:dio/dio.dart';

import '../../core/network/api_client.dart';
import '../models/fx_page_model.dart';
import '../models/fx_exchange_rate_model.dart';
import '../models/operation_fee_model.dart';
import '../models/model_parsers.dart';

abstract class FxRemoteDataSource {
  Future<FxExchangeRatesPageModel> listRates({int page = 0, int size = 20});
  Future<FxExchangeRateModel> createRate(Map<String, dynamic> body);
  Future<FxExchangeRateModel> updateRate(String id, Map<String, dynamic> body);
  Future<void> deleteRate(String id);

  Future<OperationFeesPageModel> listFees({int page = 0, int size = 20});
  Future<OperationFeeModel> createFee(Map<String, dynamic> body);
  Future<OperationFeeModel> updateFee(String id, Map<String, dynamic> body);
  Future<void> deleteFee(String id);
}

class FxRemoteDataSourceImpl implements FxRemoteDataSource {
  final ApiClient apiClient;
  FxRemoteDataSourceImpl(this.apiClient);

  @override
  Future<FxExchangeRatesPageModel> listRates({int page = 0, int size = 20}) async {
    final response = await apiClient.get('/api/fx/rates', queryParameters: {'page': page, 'size': size});
    return _parsePage(response, (json) => FxExchangeRatesPageModel.fromJson(json), 'Error al obtener tipos de cambio');
  }

  @override
  Future<FxExchangeRateModel> createRate(Map<String, dynamic> body) async {
    final response = await apiClient.post('/api/fx/rates', body);
    return _parseSingle(response, (json) => FxExchangeRateModel.fromJson(json), 'Error al crear tasa');
  }

  @override
  Future<FxExchangeRateModel> updateRate(String id, Map<String, dynamic> body) async {
    final response = await apiClient.put('/api/fx/rates/$id', body);
    return _parseSingle(response, (json) => FxExchangeRateModel.fromJson(json), 'Error al actualizar tasa');
  }

  @override
  Future<void> deleteRate(String id) async {
    final response = await apiClient.delete('/api/fx/rates/$id');
    _ensureSuccess(response, 'Error al eliminar tasa');
  }

  @override
  Future<OperationFeesPageModel> listFees({int page = 0, int size = 20}) async {
    final response = await apiClient.get('/api/fx/fees', queryParameters: {'page': page, 'size': size});
    return _parsePage(response, (json) => OperationFeesPageModel.fromJson(json), 'Error al obtener comisiones');
  }

  @override
  Future<OperationFeeModel> createFee(Map<String, dynamic> body) async {
    final response = await apiClient.post('/api/fx/fees', body);
    return _parseSingle(response, (json) => OperationFeeModel.fromJson(json), 'Error al crear comisión');
  }

  @override
  Future<OperationFeeModel> updateFee(String id, Map<String, dynamic> body) async {
    final response = await apiClient.put('/api/fx/fees/$id', body);
    return _parseSingle(response, (json) => OperationFeeModel.fromJson(json), 'Error al actualizar comisión');
  }

  @override
  Future<void> deleteFee(String id) async {
    final response = await apiClient.delete('/api/fx/fees/$id');
    _ensureSuccess(response, 'Error al eliminar comisión');
  }

  T _parsePage<T>(
    Response response,
    T Function(Map<String, dynamic>) mapper,
    String fallbackMessage,
  ) {
    final body = asMap(response.data);
    if (response.statusCode == 200 && body != null && body['success'] == true) {
      final payload = asMap(body['data']);
      if (payload != null) return mapper(payload);
      throw Exception(fallbackMessage);
    }
    if (response.statusCode == 401) throw Exception('Sesión expirada');
    throw Exception(body?['message'] ?? fallbackMessage);
  }

  T _parseSingle<T>(
    Response response,
    T Function(Map<String, dynamic>) mapper,
    String fallbackMessage,
  ) {
    final body = asMap(response.data);
    if ((response.statusCode == 200 || response.statusCode == 201) && body != null && body['success'] == true) {
      final payload = asMap(body['data']);
      if (payload != null) return mapper(payload);
      throw Exception(fallbackMessage);
    }
    if (response.statusCode == 401) throw Exception('Sesión expirada');
    throw Exception(body?['message'] ?? fallbackMessage);
  }

  void _ensureSuccess(Response response, String fallbackMessage) {
    final body = asMap(response.data);
    if (response.statusCode == 200 || response.statusCode == 204) return;
    if (response.statusCode == 401) throw Exception('Sesión expirada');
    throw Exception(body?['message'] ?? fallbackMessage);
  }
}
