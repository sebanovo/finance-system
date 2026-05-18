class AccountBalance {
  final String accountId;
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
  final bool active;

  AccountBalance({
    required this.accountId,
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
    required this.active,
  });

  String get formattedAvailableBalance {
    return '${availableBalance.toStringAsFixed(2)} $currency';
  }

  String get formattedTotalBalance {
    return '${totalBalance.toStringAsFixed(2)} $currency';
  }
}
