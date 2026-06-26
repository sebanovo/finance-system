import 'package:dio/dio.dart';

import '../../core/network/api_client.dart';
import '../models/accounting_page_model.dart';
import '../models/accounting_period_model.dart';
import '../models/journal_entry_model.dart';
import '../models/model_parsers.dart';

abstract class AccountingRemoteDataSource {
  Future<AccountingPeriodsPageModel> listPeriods({int page = 0, int size = 20});

  Future<AccountingPeriodModel> createPeriod(Map<String, dynamic> body);

  Future<AccountingPeriodModel> closePeriod(String id, Map<String, dynamic> body);

  Future<JournalEntriesPageModel> listJournalEntries({int page = 0, int size = 20});

  Future<JournalEntryModel> getJournalEntryById(String id);
}

class AccountingRemoteDataSourceImpl implements AccountingRemoteDataSource {
  final ApiClient apiClient;

  AccountingRemoteDataSourceImpl(this.apiClient);

  @override
  Future<AccountingPeriodsPageModel> listPeriods({int page = 0, int size = 20}) async {
    final response = await apiClient.get(
      '/api/accounting/periods',
      queryParameters: {'page': page, 'size': size},
    );
    return _parsePage(
      response,
      (json) => AccountingPeriodsPageModel.fromJson(json),
      'Error al obtener períodos contables',
    );
  }

  @override
  Future<AccountingPeriodModel> createPeriod(Map<String, dynamic> body) async {
    final response = await apiClient.post('/api/accounting/periods', body);
    return _parseSingle(
      response,
      (json) => AccountingPeriodModel.fromJson(json),
      'Error al crear período contable',
    );
  }

  @override
  Future<AccountingPeriodModel> closePeriod(String id, Map<String, dynamic> body) async {
    final response = await apiClient.patch('/api/accounting/periods/$id/close', body);
    return _parseSingle(
      response,
      (json) => AccountingPeriodModel.fromJson(json),
      'Error al cerrar período contable',
    );
  }

  @override
  Future<JournalEntriesPageModel> listJournalEntries({int page = 0, int size = 20}) async {
    final response = await apiClient.get(
      '/api/accounting/journal-entries',
      queryParameters: {'page': page, 'size': size},
    );
    return _parsePage(
      response,
      (json) => JournalEntriesPageModel.fromJson(json),
      'Error al obtener asientos contables',
    );
  }

  @override
  Future<JournalEntryModel> getJournalEntryById(String id) async {
    final response = await apiClient.get('/api/accounting/journal-entries/$id');
    return _parseSingle(
      response,
      (json) => JournalEntryModel.fromJson(json),
      'Error al obtener detalle del asiento',
    );
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
}
