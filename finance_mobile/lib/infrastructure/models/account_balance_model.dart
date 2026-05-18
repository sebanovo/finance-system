import '../../../domain/entities/account_balance.dart';

class AccountBalanceModel {
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

  AccountBalanceModel({
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

  factory AccountBalanceModel.fromJson(Map<String, dynamic> json) {
    return AccountBalanceModel(
      accountId: json['accountId'] ?? '',
      accountNumber: json['accountNumber'] ?? '',
      accountName: json['accountName'] ?? '',
      accountNameLabel: json['accountNameLabel'] ?? '',
      customAlias: json['customAlias'],
      displayName: json['displayName'] ?? '',
      accountType: json['accountType'] ?? '',
      currency: json['currency'] ?? '',
      availableBalance: (json['availableBalance'] ?? 0).toDouble(),
      heldBalance: (json['heldBalance'] ?? 0).toDouble(),
      totalBalance: (json['totalBalance'] ?? 0).toDouble(),
      status: json['status'] ?? '',
      active: json['active'] ?? false,
    );
  }

  AccountBalance toEntity() {
    return AccountBalance(
      accountId: accountId,
      accountNumber: accountNumber,
      accountName: accountName,
      accountNameLabel: accountNameLabel,
      customAlias: customAlias,
      displayName: displayName,
      accountType: accountType,
      currency: currency,
      availableBalance: availableBalance,
      heldBalance: heldBalance,
      totalBalance: totalBalance,
      status: status,
      active: active,
    );
  }
}
