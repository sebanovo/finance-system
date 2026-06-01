import 'package:flutter/material.dart';

import '../../domain/entities/account.dart';
import '../../domain/entities/account_lookup.dart';
import '../../domain/entities/limit_evaluation.dart';
import '../../domain/entities/limit_rule.dart';
import '../../domain/entities/user_info.dart';
import '../../domain/usecases/evaluate_limit_usecase.dart';
import '../../domain/usecases/get_account_by_number_usecase.dart';
import '../../domain/usecases/get_accounts_usecase.dart';
import '../../domain/usecases/get_limit_rules_usecase.dart';
import '../../domain/usecases/get_user_info_usecase.dart';

class LimitsViewModel extends ChangeNotifier {
  final GetLimitRulesUseCase getLimitRulesUseCase;
  final EvaluateLimitUseCase evaluateLimitUseCase;
  final GetAccountsUseCase getAccountsUseCase;
  final GetUserInfoUseCase getUserInfoUseCase;
  final GetAccountByNumberUseCase getAccountByNumberUseCase;

  List<LimitRule> _rules = [];
  List<Account> _accounts = [];
  UserInfo? _userInfo;
  LimitEvaluation? _evaluation;
  bool _loading = false;
  bool _loadingEvaluation = false;
  String? _errorMessage;
  String? _evaluationErrorMessage;

  LimitsViewModel({
    required this.getLimitRulesUseCase,
    required this.evaluateLimitUseCase,
    required this.getAccountsUseCase,
    required this.getUserInfoUseCase,
    required this.getAccountByNumberUseCase,
  });

  List<LimitRule> get rules => _rules;
  List<Account> get accounts => _accounts;
  UserInfo? get userInfo => _userInfo;
  LimitEvaluation? get evaluation => _evaluation;
  bool get loading => _loading;
  bool get loadingEvaluation => _loadingEvaluation;
  String? get errorMessage => _errorMessage;
  String? get evaluationErrorMessage => _evaluationErrorMessage;

  bool get canReadRules => true;
  bool get canEvaluate => true;

  void clearError() {
    _errorMessage = null;
    _evaluationErrorMessage = null;
    notifyListeners();
  }

  Future<void> loadInitial() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await Future.wait([
        _loadRules(),
        _loadAccounts(),
        _loadUserInfo(),
      ]);
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> refresh() => loadInitial();

  Future<void> _loadRules() async {
    final page = await getLimitRulesUseCase(page: 0, size: 50);
    _rules = page.content;
  }

  Future<void> _loadAccounts() async {
    _accounts = await getAccountsUseCase();
  }

  Future<void> _loadUserInfo() async {
    _userInfo = await getUserInfoUseCase();
  }

  Future<void> evaluateOperation({
    required String sourceAccountId,
    required String transactionType,
    required String amount,
    String? targetAccountNumber,
  }) async {
    final sourceAccount = _accounts.where((account) => account.id == sourceAccountId).firstOrNull;
    if (sourceAccount == null) {
      _evaluationErrorMessage = 'Selecciona una cuenta origen válida';
      notifyListeners();
      return;
    }

    final parsedAmount = _parseAmount(amount);
    if (parsedAmount == null || parsedAmount <= 0) {
      _evaluationErrorMessage = 'Ingresa un monto válido';
      notifyListeners();
      return;
    }

    Account? targetOwnAccount;
    AccountLookup? targetLookup;
    if (transactionType == 'TRANSFER') {
      if (targetAccountNumber == null || targetAccountNumber.trim().isEmpty) {
        _evaluationErrorMessage = 'Ingresa el número de cuenta destino';
        notifyListeners();
        return;
      }

      final normalizedTarget = targetAccountNumber.trim();
      targetOwnAccount = _accounts.where((account) => account.accountNumber == normalizedTarget).firstOrNull;
      try {
        targetLookup = await getAccountByNumberUseCase(normalizedTarget);
        if (!targetLookup.active) {
          _evaluationErrorMessage = 'La cuenta destino no está activa';
          notifyListeners();
          return;
        }
      } catch (e) {
        _evaluationErrorMessage = e.toString();
        notifyListeners();
        return;
      }
    }

    final actor = _userInfo;
    if (actor == null || actor.id.isEmpty) {
      _evaluationErrorMessage = 'No se pudo resolver el usuario actual';
      notifyListeners();
      return;
    }

    _loadingEvaluation = true;
    _evaluationErrorMessage = null;
    notifyListeners();
    try {
      final request = LimitEvaluationInput(
        actorUserId: actor.id,
        sourceAccountId: sourceAccount.id,
        targetAccountId: targetLookup?.id ?? targetOwnAccount?.id,
        sourceAccountType: sourceAccount.accountType,
        targetAccountType: targetLookup?.accountType ?? targetOwnAccount?.accountType,
        sourceAvailableBalance: sourceAccount.availableBalance,
        targetAvailableBalance: targetOwnAccount?.availableBalance,
        transactionType: transactionType,
        currency: sourceAccount.currency,
        amount: parsedAmount,
      );
      _evaluation = await evaluateLimitUseCase(request);
    } catch (e) {
      _evaluationErrorMessage = e.toString();
    } finally {
      _loadingEvaluation = false;
      notifyListeners();
    }
  }

  double? _parseAmount(String value) {
    final normalized = value.replaceAll(',', '.').trim();
    return double.tryParse(normalized);
  }
}

extension<T> on Iterable<T> {
  T? get firstOrNull => isEmpty ? null : first;
}
