class LoanModel {
  final String id;
  final String userId;
  final String accountId;
  final double principal;
  final String currency;
  final double annualInterestRate;
  final int termMonths;
  final String interestMethod;
  final String status;
  final String? purpose;
  final double totalDue;
  final double totalPaid;
  final double outstandingBalance;

  LoanModel({
    required this.id,
    required this.userId,
    required this.accountId,
    required this.principal,
    required this.currency,
    required this.annualInterestRate,
    required this.termMonths,
    required this.interestMethod,
    required this.status,
    this.purpose,
    required this.totalDue,
    required this.totalPaid,
    required this.outstandingBalance,
  });

  static double _toDouble(dynamic v) =>
      v == null ? 0.0 : (v is num ? v.toDouble() : double.tryParse(v.toString()) ?? 0.0);

  factory LoanModel.fromJson(Map<String, dynamic> json) {
    return LoanModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      accountId: json['accountId'] ?? '',
      principal: _toDouble(json['principal']),
      currency: json['currency'] ?? '',
      annualInterestRate: _toDouble(json['annualInterestRate']),
      termMonths: (json['termMonths'] ?? 0) as int,
      interestMethod: json['interestMethod'] ?? 'FLAT',
      status: json['status'] ?? '',
      purpose: json['purpose'],
      totalDue: _toDouble(json['totalDue']),
      totalPaid: _toDouble(json['totalPaid']),
      outstandingBalance: _toDouble(json['outstandingBalance']),
    );
  }
}

class LoanInstallmentModel {
  final int number;
  final String dueDate;
  final double principalDue;
  final double interestDue;
  final double totalDue;
  final double paidAmount;
  final String status;

  LoanInstallmentModel({
    required this.number,
    required this.dueDate,
    required this.principalDue,
    required this.interestDue,
    required this.totalDue,
    required this.paidAmount,
    required this.status,
  });

  factory LoanInstallmentModel.fromJson(Map<String, dynamic> json) {
    return LoanInstallmentModel(
      number: (json['number'] ?? 0) as int,
      dueDate: json['dueDate'] ?? '',
      principalDue: LoanModel._toDouble(json['principalDue']),
      interestDue: LoanModel._toDouble(json['interestDue']),
      totalDue: LoanModel._toDouble(json['totalDue']),
      paidAmount: LoanModel._toDouble(json['paidAmount']),
      status: json['status'] ?? '',
    );
  }
}
