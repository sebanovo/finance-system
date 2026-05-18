class CreateAccountRequest {
  final String accountName;
  final String accountType;
  final String currency;
  final String customAlias;

  CreateAccountRequest({
    required this.accountName,
    required this.accountType,
    required this.currency,
    required this.customAlias,
  });

  Map<String, dynamic> toJson() => {
    'accountName': accountName,
    'accountType': accountType,
    'currency': currency,
    'customAlias': customAlias,
  };
}
