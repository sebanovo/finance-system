import '../../../domain/entities/subscription.dart';
import '../../../domain/repositories/subscription_repository.dart';
import '../datasources/subscription_remote_datasource.dart';

class SubscriptionRepositoryImpl implements SubscriptionRepository {
  final SubscriptionRemoteDataSource remoteDataSource;

  SubscriptionRepositoryImpl(this.remoteDataSource);

  @override
  Future<Subscription?> getCurrentSubscription() async {
    final model = await remoteDataSource.getCurrentSubscription();
    return model?.toEntity();
  }
}
