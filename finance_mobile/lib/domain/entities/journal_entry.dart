class JournalLine {
  final String id;
  final int lineNo;
  final String accountCode;
  final String accountName;
  final String lineType;
  final double amount;
  final String currency;
  final String? description;
  final DateTime? createdAt;

  const JournalLine({
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
}

class JournalEntry {
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
  final List<JournalLine> lines;

  const JournalEntry({
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
}

class JournalEntriesPage {
  final List<JournalEntry> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const JournalEntriesPage({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });
}
