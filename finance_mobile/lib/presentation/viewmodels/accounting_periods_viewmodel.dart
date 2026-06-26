import 'package:flutter/material.dart';

import '../../domain/entities/accounting_period.dart';
import '../../domain/usecases/close_accounting_period_usecase.dart';
import '../../domain/usecases/create_accounting_period_usecase.dart';
import '../../domain/usecases/get_accounting_periods_usecase.dart';

class AccountingPeriodsViewModel extends ChangeNotifier {
  final GetAccountingPeriodsUseCase getAccountingPeriodsUseCase;
  final CreateAccountingPeriodUseCase createAccountingPeriodUseCase;
  final CloseAccountingPeriodUseCase closeAccountingPeriodUseCase;

  AccountingPeriodsViewModel({
    required this.getAccountingPeriodsUseCase,
    required this.createAccountingPeriodUseCase,
    required this.closeAccountingPeriodUseCase,
  });

  List<AccountingPeriod> _items = [];
  bool _loading = false;
  bool _saving = false;
  String? _errorMessage;
  int _page = 0;
  final int _size = 20;
  int _totalPages = 0;
  int _totalElements = 0;

  List<AccountingPeriod> get items => _items;
  bool get loading => _loading;
  bool get saving => _saving;
  String? get errorMessage => _errorMessage;
  int get page => _page;
  int get size => _size;
  int get totalPages => _totalPages;
  int get totalElements => _totalElements;
  bool get hasPreviousPage => _page > 0;
  bool get hasNextPage => _page + 1 < _totalPages;

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  Future<void> loadPeriods({int page = 0}) async {
    _loading = true;
    _page = page;
    _errorMessage = null;
    notifyListeners();
    try {
      final result = await getAccountingPeriodsUseCase(page: page, size: _size);
      _items = result.items;
      _totalPages = result.totalPages;
      _totalElements = result.totalElements;
      _page = result.number;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<bool> createPeriod({
    required String periodCode,
    required String periodType,
    required String startDate,
    required String endDate,
    String? description,
  }) async {
    _saving = true;
    notifyListeners();
    try {
      await createAccountingPeriodUseCase(
        periodCode: periodCode,
        periodType: periodType,
        startDate: startDate,
        endDate: endDate,
        description: description,
      );
      await loadPeriods(page: _page);
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<bool> closePeriod(String id, {String? reason}) async {
    _saving = true;
    notifyListeners();
    try {
      await closeAccountingPeriodUseCase(id, reason: reason);
      await loadPeriods(page: _page);
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }
}
