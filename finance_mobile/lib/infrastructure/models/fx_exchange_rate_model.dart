import '../../domain/entities/fx_exchange_rate.dart';
import 'model_parsers.dart';

class FxExchangeRateModel {
  final String id;
  final String sourceCurrency;
  final String targetCurrency;
  final double rate;
  final bool active;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const FxExchangeRateModel({
    required this.id,
    required this.sourceCurrency,
    required this.targetCurrency,
    required this.rate,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FxExchangeRateModel.fromJson(Map<String, dynamic> json) {
    return FxExchangeRateModel(
      id: asString(json['id']),
      sourceCurrency: asString(json['sourceCurrency']),
      targetCurrency: asString(json['targetCurrency']),
      rate: asDouble(json['rate']),
      active: json['active'] == true,
      description: asNullableString(json['description']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
    );
  }

  FxExchangeRate toEntity() {
    return FxExchangeRate(
      id: id,
      sourceCurrency: sourceCurrency,
      targetCurrency: targetCurrency,
      rate: rate,
      active: active,
      description: description,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
