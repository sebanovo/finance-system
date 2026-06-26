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
  final String? statusReason;
  final double totalDue;
  final double totalPaid;
  final double outstandingBalance;
  final String? disbursedAt;
  final String? closedAt;
  final String? createdAt;
  final String? updatedAt;

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
    this.statusReason,
    required this.totalDue,
    required this.totalPaid,
    required this.outstandingBalance,
    this.disbursedAt,
    this.closedAt,
    this.createdAt,
    this.updatedAt,
  });

  static double _toDouble(dynamic v) =>
      v == null ? 0.0 : (v is num ? v.toDouble() : double.tryParse(v.toString()) ?? 0.0);

  static int _toInt(dynamic v) => v == null ? 0 : (v is num ? v.toInt() : int.tryParse(v.toString()) ?? 0);

  factory LoanModel.fromJson(Map<String, dynamic> json) {
    return LoanModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      accountId: json['accountId'] ?? '',
      principal: _toDouble(json['principal']),
      currency: json['currency'] ?? '',
      annualInterestRate: _toDouble(json['annualInterestRate']),
      termMonths: _toInt(json['termMonths']),
      interestMethod: json['interestMethod'] ?? 'FLAT',
      status: json['status'] ?? '',
      purpose: json['purpose'],
      statusReason: json['statusReason'],
      totalDue: _toDouble(json['totalDue']),
      totalPaid: _toDouble(json['totalPaid']),
      outstandingBalance: _toDouble(json['outstandingBalance']),
      disbursedAt: json['disbursedAt']?.toString(),
      closedAt: json['closedAt']?.toString(),
      createdAt: json['createdAt']?.toString(),
      updatedAt: json['updatedAt']?.toString(),
    );
  }
}

class LoanInstallmentModel {
  final String? id;
  final int number;
  final String dueDate;
  final double principalDue;
  final double interestDue;
  final double totalDue;
  final double paidAmount;
  final String status;
  final String? paidAt;

  LoanInstallmentModel({
    this.id,
    required this.number,
    required this.dueDate,
    required this.principalDue,
    required this.interestDue,
    required this.totalDue,
    required this.paidAmount,
    required this.status,
    this.paidAt,
  });

  factory LoanInstallmentModel.fromJson(Map<String, dynamic> json) {
    return LoanInstallmentModel(
      id: json['id']?.toString(),
      number: LoanModel._toInt(json['number']),
      dueDate: json['dueDate'] ?? '',
      principalDue: LoanModel._toDouble(json['principalDue']),
      interestDue: LoanModel._toDouble(json['interestDue']),
      totalDue: LoanModel._toDouble(json['totalDue']),
      paidAmount: LoanModel._toDouble(json['paidAmount']),
      status: json['status'] ?? '',
      paidAt: json['paidAt']?.toString(),
    );
  }
}
