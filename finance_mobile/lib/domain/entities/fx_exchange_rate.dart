class FxExchangeRate {
  final String id;
  final String sourceCurrency;
  final String targetCurrency;
  final double rate;
  final bool active;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const FxExchangeRate({
    required this.id,
    required this.sourceCurrency,
    required this.targetCurrency,
    required this.rate,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });
}

class FxExchangeRatesPage {
  final List<FxExchangeRate> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const FxExchangeRatesPage({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });
}
