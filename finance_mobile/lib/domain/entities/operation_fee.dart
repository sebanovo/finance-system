class OperationFee {
  final String id;
  final String operationCode;
  final String feeType;
  final double feeValue;
  final String calculationMode;
  final bool active;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const OperationFee({
    required this.id,
    required this.operationCode,
    required this.feeType,
    required this.feeValue,
    required this.calculationMode,
    required this.active,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });
}

class OperationFeesPage {
  final List<OperationFee> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const OperationFeesPage({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });
}
