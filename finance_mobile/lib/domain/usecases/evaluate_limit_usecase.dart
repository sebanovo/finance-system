import '../entities/limit_evaluation.dart';
import '../repositories/limit_repository.dart';

class EvaluateLimitUseCase {
  final LimitRepository repository;

  EvaluateLimitUseCase(this.repository);

  Future<LimitEvaluation> call(LimitEvaluationInput request) {
    return repository.evaluate(request);
  }
}
