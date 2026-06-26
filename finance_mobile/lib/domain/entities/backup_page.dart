import 'backup_record.dart';

class BackupPage {
  final List<BackupRecord> items;
  final int totalPages;
  final int totalElements;
  final int number;
  final int size;
  final bool first;
  final bool last;

  const BackupPage({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.number,
    required this.size,
    required this.first,
    required this.last,
  });
}
