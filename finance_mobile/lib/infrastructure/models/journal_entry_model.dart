import '../../domain/entities/journal_entry.dart';
import 'model_parsers.dart';

class JournalLineModel {
  final String id;
  final int lineNo;
  final String accountCode;
  final String accountName;
  final String lineType;
  final double amount;
  final String currency;
  final String? description;
  final DateTime? createdAt;

  const JournalLineModel({
    required this.id,
    required this.lineNo,
    required this.accountCode,
    required this.accountName,
    required this.lineType,
    required this.amount,
    required this.currency,
    required this.description,
    required this.createdAt,
  });

  factory JournalLineModel.fromJson(Map<String, dynamic> json) {
    return JournalLineModel(
      id: asString(json['id']),
      lineNo: (json['lineNo'] as num?)?.toInt() ?? int.tryParse(asString(json['lineNo'])) ?? 0,
      accountCode: asString(json['accountCode']),
      accountName: asString(json['accountName']),
      lineType: asString(json['lineType']),
      amount: asDouble(json['amount']),
      currency: asString(json['currency']),
      description: asNullableString(json['description']),
      createdAt: asDateTime(json['createdAt']),
    );
  }

  JournalLine toEntity() {
    return JournalLine(
      id: id,
      lineNo: lineNo,
      accountCode: accountCode,
      accountName: accountName,
      lineType: lineType,
      amount: amount,
      currency: currency,
      description: description,
      createdAt: createdAt,
    );
  }
}

class JournalEntryModel {
  final String id;
  final String entryNumber;
  final String? sourceTransactionId;
  final String entryType;
  final String status;
  final String? description;
  final String? reference;
  final double totalDebits;
  final double totalCredits;
  final bool balanced;
  final DateTime? postedAt;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final List<JournalLineModel> lines;

  const JournalEntryModel({
    required this.id,
    required this.entryNumber,
    required this.sourceTransactionId,
    required this.entryType,
    required this.status,
    required this.description,
    required this.reference,
    required this.totalDebits,
    required this.totalCredits,
    required this.balanced,
    required this.postedAt,
    required this.createdAt,
    required this.updatedAt,
    required this.lines,
  });

  factory JournalEntryModel.fromJson(Map<String, dynamic> json) {
    final rawLines = json['lines'];
    final lines = extractMapList(rawLines).map(JournalLineModel.fromJson).toList();
    return JournalEntryModel(
      id: asString(json['id']),
      entryNumber: asString(json['entryNumber']),
      sourceTransactionId: asNullableString(json['sourceTransactionId']),
      entryType: asString(json['entryType']),
      status: asString(json['status']),
      description: asNullableString(json['description']),
      reference: asNullableString(json['reference']),
      totalDebits: asDouble(json['totalDebits']),
      totalCredits: asDouble(json['totalCredits']),
      balanced: json['balanced'] == true,
      postedAt: asDateTime(json['postedAt']),
      createdAt: asDateTime(json['createdAt']),
      updatedAt: asDateTime(json['updatedAt']),
      lines: lines,
    );
  }

  JournalEntry toEntity() {
    return JournalEntry(
      id: id,
      entryNumber: entryNumber,
      sourceTransactionId: sourceTransactionId,
      entryType: entryType,
      status: status,
      description: description,
      reference: reference,
      totalDebits: totalDebits,
      totalCredits: totalCredits,
      balanced: balanced,
      postedAt: postedAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
      lines: lines.map((line) => line.toEntity()).toList(),
    );
  }
}
