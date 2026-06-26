import 'package:flutter/material.dart';

import '../../domain/entities/fx_exchange_rate.dart';
import '../../domain/usecases/create_fx_rate_usecase.dart';
import '../../domain/usecases/delete_fx_rate_usecase.dart';
import '../../domain/usecases/get_fx_rates_usecase.dart';
import '../../domain/usecases/update_fx_rate_usecase.dart';

class FxRatesViewModel extends ChangeNotifier {
  final GetFxRatesUseCase getFxRatesUseCase;
  final CreateFxRateUseCase createFxRateUseCase;
  final UpdateFxRateUseCase updateFxRateUseCase;
  final DeleteFxRateUseCase deleteFxRateUseCase;

  FxRatesViewModel({
    required this.getFxRatesUseCase,
    required this.createFxRateUseCase,
    required this.updateFxRateUseCase,
    required this.deleteFxRateUseCase,
  });

  List<FxExchangeRate> _items = [];
  bool _loading = false;
  bool _saving = false;
  String? _errorMessage;
  int _page = 0;
  final int _size = 20;
  int _totalPages = 0;
  int _totalElements = 0;

  List<FxExchangeRate> get items => _items;
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

  Future<void> loadRates({int page = 0}) async {
    _loading = true;
    _page = page;
    _errorMessage = null;
    notifyListeners();
    try {
      final result = await getFxRatesUseCase(page: page, size: _size);
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

  Future<bool> createRate({
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  }) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await createFxRateUseCase(
        sourceCurrency: sourceCurrency,
        targetCurrency: targetCurrency,
        rate: rate,
        active: active,
        description: description,
      );
      await loadRates(page: _page);
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceFirst('Exception: ', '');
      notifyListeners();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<bool> updateRate(
    String id, {
    required String sourceCurrency,
    required String targetCurrency,
    required double rate,
    required bool active,
    String? description,
  }) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await updateFxRateUseCase(
        id,
        sourceCurrency: sourceCurrency,
        targetCurrency: targetCurrency,
        rate: rate,
        active: active,
        description: description,
      );
      await loadRates(page: _page);
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceFirst('Exception: ', '');
      notifyListeners();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<bool> deleteRate(String id) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await deleteFxRateUseCase(id);
      await loadRates(page: _page);
      return true;
    } catch (e) {
      _errorMessage = e.toString().replaceFirst('Exception: ', '');
      notifyListeners();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }
}
