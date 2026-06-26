import 'package:flutter/material.dart';

import '../../domain/entities/operation_fee.dart';
import '../../domain/usecases/create_fx_fee_usecase.dart';
import '../../domain/usecases/delete_fx_fee_usecase.dart';
import '../../domain/usecases/get_fx_fees_usecase.dart';
import '../../domain/usecases/update_fx_fee_usecase.dart';

class FxFeesViewModel extends ChangeNotifier {
  final GetFxFeesUseCase getFxFeesUseCase;
  final CreateFxFeeUseCase createFxFeeUseCase;
  final UpdateFxFeeUseCase updateFxFeeUseCase;
  final DeleteFxFeeUseCase deleteFxFeeUseCase;

  FxFeesViewModel({
    required this.getFxFeesUseCase,
    required this.createFxFeeUseCase,
    required this.updateFxFeeUseCase,
    required this.deleteFxFeeUseCase,
  });

  List<OperationFee> _items = [];
  bool _loading = false;
  bool _saving = false;
  String? _errorMessage;
  int _page = 0;
  final int _size = 20;
  int _totalPages = 0;
  int _totalElements = 0;

  List<OperationFee> get items => _items;
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

  Future<void> loadFees({int page = 0}) async {
    _loading = true;
    _page = page;
    _errorMessage = null;
    notifyListeners();
    try {
      final result = await getFxFeesUseCase(page: page, size: _size);
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

  Future<bool> createFee({
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  }) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await createFxFeeUseCase(
        operationCode: operationCode,
        feeType: feeType,
        feeValue: feeValue,
        calculationMode: calculationMode,
        active: active,
        description: description,
      );
      await loadFees(page: _page);
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

  Future<bool> updateFee(
    String id, {
    required String operationCode,
    required String feeType,
    required double feeValue,
    required String calculationMode,
    required bool active,
    String? description,
  }) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await updateFxFeeUseCase(
        id,
        operationCode: operationCode,
        feeType: feeType,
        feeValue: feeValue,
        calculationMode: calculationMode,
        active: active,
        description: description,
      );
      await loadFees(page: _page);
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

  Future<bool> deleteFee(String id) async {
    _saving = true;
    _errorMessage = null;
    notifyListeners();
    try {
      await deleteFxFeeUseCase(id);
      await loadFees(page: _page);
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
