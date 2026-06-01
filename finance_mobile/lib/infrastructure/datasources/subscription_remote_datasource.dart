import '../../../../core/network/api_client.dart';
import '../models/subscription_model.dart';

abstract class SubscriptionRemoteDataSource {
  Future<SubscriptionModel?> getCurrentSubscription();
}

class SubscriptionRemoteDataSourceImpl implements SubscriptionRemoteDataSource {
  final ApiClient apiClient;

  SubscriptionRemoteDataSourceImpl(this.apiClient);

  @override
  Future<SubscriptionModel?> getCurrentSubscription() async {
    final response = await apiClient.get('/api/subscription/current');
    if (response.statusCode == 200) {
      final data = response.data as Map<String, dynamic>;
      if (data['success'] == true) {
        final payload = data['data'];
        if (payload is! Map<String, dynamic>) {
          return null;
        }
        return SubscriptionModel.tryFromJson(payload);
      } else {
        throw Exception(data['message'] ?? 'Error al obtener suscripción');
      }
    } else if (response.statusCode == 401) {
      throw Exception('Sesión expirada');
    } else {
      throw Exception('Error ${response.statusCode}');
    }
  }
}
