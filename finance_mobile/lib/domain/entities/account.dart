class Account {
  final String id;
  final String userId;
  final String userEmail;
  final String? userFirstName;
  final String? userLastName;
  final String userFullName;
  final String accountNumber;
  final String accountName;
  final String accountNameLabel;
  final String? customAlias;
  final String displayName;
  final String accountType;
  final String currency;
  final double availableBalance;
  final double heldBalance;
  final double totalBalance;
  final String status;
  final String? statusReason;
  final bool active;
  final bool primary;
  final DateTime openedAt;
  final DateTime? closedAt;
  final DateTime createdAt;
  final DateTime updatedAt;

  Account({
    required this.id,
    required this.userId,
    required this.userEmail,
    this.userFirstName,
    this.userLastName,
    required this.userFullName,
    required this.accountNumber,
    required this.accountName,
    required this.accountNameLabel,
    this.customAlias,
    required this.displayName,
    required this.accountType,
    required this.currency,
    required this.availableBalance,
    required this.heldBalance,
    required this.totalBalance,
    required this.status,
    this.statusReason,
    required this.active,
    required this.primary,
    required this.openedAt,
    this.closedAt,
    required this.createdAt,
    required this.updatedAt,
  });

  String get formattedAvailableBalance {
    return '${_formatCurrency(availableBalance)} $currency';
  }

  String get formattedTotalBalance {
    return '${_formatCurrency(totalBalance)} $currency';
  }

  String _formatCurrency(double value) {
    return value.toStringAsFixed(2);
  }
}
