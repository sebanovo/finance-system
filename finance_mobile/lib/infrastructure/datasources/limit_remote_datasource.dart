import '../../../../core/network/api_client.dart';
import '../models/limit_rule_model.dart';
import '../models/limit_evaluation_model.dart';

abstract class LimitRemoteDataSource {
  Future<LimitRulesPageModel> listRules({int page = 0, int size = 50});
  Future<LimitEvaluationModel> evaluate(LimitEvaluationRequestModel request);
}

class LimitRemoteDataSourceImpl implements LimitRemoteDataSource {
  final ApiClient apiClient;

  LimitRemoteDataSourceImpl(this.apiClient);

  @override
  Future<LimitRulesPageModel> listRules({int page = 0, int size = 50}) async {
    final response = await apiClient.get('/api/limits/rules?page=$page&size=$size');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return LimitRulesPageModel.fromJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'Error al obtener límites');
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error ${response.statusCode}');
  }

  @override
  Future<LimitEvaluationModel> evaluate(LimitEvaluationRequestModel request) async {
    final response = await apiClient.post('/api/limits/evaluate', request.toJson());
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        return LimitEvaluationModel.fromJson(data['data'] as Map<String, dynamic>);
      }
      throw Exception(data['message'] ?? 'Error al evaluar límite');
    }
    if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    final data = response.data;
    if (data is Map<String, dynamic>) {
      throw Exception(data['message'] ?? 'Error ${response.statusCode}');
    }
    throw Exception('Error ${response.statusCode}');
  }
}
