import 'package:finance_mobile/domain/entities/account_lookup.dart';

class AccountLookupModel {
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

  AccountLookupModel({
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

  factory AccountLookupModel.fromJson(Map<String, dynamic> json) {
    return AccountLookupModel(
      id: json['id'] ?? '',
      accountNumber: json['accountNumber'] ?? '',
      accountName: json['accountName'] ?? '',
      accountNameLabel: json['accountNameLabel'] ?? '',
      customAlias: json['customAlias'],
      displayName: json['displayName'] ?? '',
      accountType: json['accountType'] ?? '',
      currency: json['currency'] ?? '',
      status: json['status'] ?? '',
      active: json['active'] ?? false,
    );
  }

  AccountLookup toEntity() {
    return AccountLookup(
      id: id,
      accountNumber: accountNumber,
      accountName: accountName,
      accountNameLabel: accountNameLabel,
      customAlias: customAlias,
      displayName: displayName,
      accountType: accountType,
      currency: currency,
      status: status,
      active: active,
    );
  }
}
