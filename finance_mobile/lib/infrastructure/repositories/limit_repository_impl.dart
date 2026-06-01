import 'package:finance_mobile/domain/entities/limit_evaluation.dart';
import 'package:finance_mobile/domain/entities/limit_rule.dart';
import 'package:finance_mobile/domain/repositories/limit_repository.dart';

import '../datasources/limit_remote_datasource.dart';
import '../models/limit_evaluation_model.dart';

class LimitRepositoryImpl implements LimitRepository {
  final LimitRemoteDataSource remoteDataSource;

  LimitRepositoryImpl(this.remoteDataSource);

  @override
  Future<LimitRulesPage> listRules({int page = 0, int size = 50}) async {
    final model = await remoteDataSource.listRules(page: page, size: size);
    return model.toEntity();
  }

  @override
  Future<LimitEvaluation> evaluate(LimitEvaluationInput request) async {
    final model = await remoteDataSource.evaluate(
      LimitEvaluationRequestModel(request),
    );
    return model.toEntity();
  }
}
