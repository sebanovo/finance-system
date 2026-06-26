import '../../domain/entities/fx_exchange_rate.dart';
import '../../domain/entities/operation_fee.dart';
import 'fx_exchange_rate_model.dart';
import 'operation_fee_model.dart';
import 'model_parsers.dart';

class FxExchangeRatesPageModel {
  final List<FxExchangeRateModel> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const FxExchangeRatesPageModel({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });

  factory FxExchangeRatesPageModel.fromJson(Map<String, dynamic> json) {
    final rawItems = json['content'] ?? json['items'];
    final list = extractMapList(rawItems).map(FxExchangeRateModel.fromJson).toList();
    return FxExchangeRatesPageModel(
      items: list,
      totalPages: _asInt(json['totalPages']),
      totalElements: _asInt(json['totalElements']),
      number: _asInt(json['number']),
      size: _asInt(json['size']),
      first: json['first'] == true,
      last: json['last'] == true,
    );
  }

  FxExchangeRatesPage toEntity() {
    return FxExchangeRatesPage(
      items: items.map((item) => item.toEntity()).toList(),
      totalPages: totalPages,
      totalElements: totalElements,
      number: number,
      size: size,
      first: first,
      last: last,
    );
  }
}

class OperationFeesPageModel {
  final List<OperationFeeModel> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const OperationFeesPageModel({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });

  factory OperationFeesPageModel.fromJson(Map<String, dynamic> json) {
    final rawItems = json['content'] ?? json['items'];
    final list = extractMapList(rawItems).map(OperationFeeModel.fromJson).toList();
    return OperationFeesPageModel(
      items: list,
      totalPages: _asInt(json['totalPages']),
      totalElements: _asInt(json['totalElements']),
      number: _asInt(json['number']),
      size: _asInt(json['size']),
      first: json['first'] == true,
      last: json['last'] == true,
    );
  }

  OperationFeesPage toEntity() {
    return OperationFeesPage(
      items: items.map((item) => item.toEntity()).toList(),
      totalPages: totalPages,
      totalElements: totalElements,
      number: number,
      size: size,
      first: first,
      last: last,
    );
  }
}

int _asInt(dynamic value) {
  if (value is int) return value;
  if (value is num) return value.toInt();
  return int.tryParse(value?.toString() ?? '') ?? 0;
}
