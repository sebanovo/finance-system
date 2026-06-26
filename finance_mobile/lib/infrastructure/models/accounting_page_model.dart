import '../../domain/entities/accounting_period.dart';
import '../../domain/entities/journal_entry.dart';
import 'model_parsers.dart';
import 'accounting_period_model.dart';
import 'journal_entry_model.dart';

class AccountingPeriodsPageModel {
  final List<AccountingPeriodModel> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const AccountingPeriodsPageModel({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });

  factory AccountingPeriodsPageModel.fromJson(Map<String, dynamic> json) {
    final rawItems = json['content'] ?? json['items'];
    final list = extractMapList(rawItems).map(AccountingPeriodModel.fromJson).toList();
    return AccountingPeriodsPageModel(
      items: list,
      totalPages: _asInt(json['totalPages']),
      totalElements: _asInt(json['totalElements']),
      number: _asInt(json['number']),
      size: _asInt(json['size']),
      first: json['first'] == true,
      last: json['last'] == true,
    );
  }

  AccountingPeriodsPage toEntity() {
    return AccountingPeriodsPage(
      items: items.map((item) => item.toEntity()).toList(),
      totalPages: totalPages,
      totalElements: totalElements,
      number: number,
      size: size,
      first: first,
      last: last,
    );
  }
}

class JournalEntriesPageModel {
  final List<JournalEntryModel> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const JournalEntriesPageModel({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });

  factory JournalEntriesPageModel.fromJson(Map<String, dynamic> json) {
    final rawItems = json['content'] ?? json['items'];
    final list = extractMapList(rawItems).map(JournalEntryModel.fromJson).toList();
    return JournalEntriesPageModel(
      items: list,
      totalPages: _asInt(json['totalPages']),
      totalElements: _asInt(json['totalElements']),
      number: _asInt(json['number']),
      size: _asInt(json['size']),
      first: json['first'] == true,
      last: json['last'] == true,
    );
  }

  JournalEntriesPage toEntity() {
    return JournalEntriesPage(
      items: items.map((item) => item.toEntity()).toList(),
      totalPages: totalPages,
      totalElements: totalElements,
      number: number,
      size: size,
      first: first,
      last: last,
    );
  }
}

int _asInt(dynamic value) {
  if (value is int) return value;
  if (value is num) return value.toInt();
  return int.tryParse(value?.toString() ?? '') ?? 0;
}
