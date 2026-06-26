import '../../domain/entities/backup_page.dart';
import 'backup_record_model.dart';
import 'model_parsers.dart';

class BackupPageModel {
  final List<BackupRecordModel> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const BackupPageModel({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });

  factory BackupPageModel.fromJson(Map<String, dynamic> json) {
    final rawItems = json['content'] ?? json['items'];
    final content = extractMapList(rawItems).map(BackupRecordModel.fromJson).toList();
    return BackupPageModel(
      items: content,
      totalPages: _asInt(json['totalPages']),
      totalElements: _asInt(json['totalElements']),
      number: _asInt(json['number']),
      size: _asInt(json['size']),
      first: json['first'] == true,
      last: json['last'] == true,
    );
  }

  BackupPage toEntity() {
    return BackupPage(
      items: items.map((item) => item.toEntity()).toList(),
      totalPages: totalPages,
      totalElements: totalElements,
      number: number,
      size: size,
      first: first,
      last: last,
    );
  }

  static int _asInt(dynamic value) {
    if (value is int) return value;
    if (value is num) return value.toInt();
    return int.tryParse(value?.toString() ?? '') ?? 0;
  }
}
