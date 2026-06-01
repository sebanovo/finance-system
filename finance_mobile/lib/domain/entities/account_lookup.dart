class AccountLookup {
  final String id;
  final String accountNumber;
  final String accountName;
  final String accountNameLabel;
  final String? customAlias;
  final String displayName;
  final String accountType;
  final String currency;
  final String status;
  final bool active;

  AccountLookup({
    required this.id,
    required this.accountNumber,
    required this.accountName,
    required this.accountNameLabel,
    this.customAlias,
    required this.displayName,
    required this.accountType,
    required this.currency,
    required this.status,
    required this.active,
  });
}
