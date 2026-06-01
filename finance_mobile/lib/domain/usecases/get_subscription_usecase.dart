import '../repositories/subscription_repository.dart';
import '../entities/subscription.dart';

class GetSubscriptionUseCase {
  final SubscriptionRepository repository;
  GetSubscriptionUseCase(this.repository);

  Future<Subscription?> call() => repository.getCurrentSubscription();
}
