import '../entities/limit_evaluation.dart';
import '../entities/limit_rule.dart';

abstract class LimitRepository {
  Future<LimitRulesPage> listRules({int page = 0, int size = 50});
  Future<LimitEvaluation> evaluate(LimitEvaluationInput request);
}
