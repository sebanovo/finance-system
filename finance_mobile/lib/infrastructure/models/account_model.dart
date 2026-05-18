import '../../../domain/entities/account.dart';

class AccountModel {
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

  AccountModel({
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

  factory AccountModel.fromJson(Map<String, dynamic> json) {
    return AccountModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      userEmail: json['userEmail'] ?? '',
      userFirstName: json['userFirstName'],
      userLastName: json['userLastName'],
      userFullName: json['userFullName'] ?? '',
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
      statusReason: json['statusReason'],
      active: json['active'] ?? false,
      primary: json['primary'] ?? false,
      openedAt: DateTime.parse(json['openedAt']),
      closedAt: json['closedAt'] != null
          ? DateTime.parse(json['closedAt'])
          : null,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Account toEntity() {
    return Account(
      id: id,
      userId: userId,
      userEmail: userEmail,
      userFirstName: userFirstName,
      userLastName: userLastName,
      userFullName: userFullName,
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
      statusReason: statusReason,
      active: active,
      primary: primary,
      openedAt: openedAt,
      closedAt: closedAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
