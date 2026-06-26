class AccountingPeriod {
  final String id;
  final String periodCode;
  final String periodType;
  final String status;
  final DateTime startDate;
  final DateTime endDate;
  final DateTime? closedAt;
  final String? description;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const AccountingPeriod({
    required this.id,
    required this.periodCode,
    required this.periodType,
    required this.status,
    required this.startDate,
    required this.endDate,
    required this.closedAt,
    required this.description,
    required this.createdAt,
    required this.updatedAt,
  });
}

class AccountingPeriodsPage {
  final List<AccountingPeriod> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const AccountingPeriodsPage({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });
}
