import '../entities/limit_rule.dart';
import '../repositories/limit_repository.dart';

class GetLimitRulesUseCase {
  final LimitRepository repository;

  GetLimitRulesUseCase(this.repository);

  Future<LimitRulesPage> call({int page = 0, int size = 50}) {
    return repository.listRules(page: page, size: size);
  }
}
