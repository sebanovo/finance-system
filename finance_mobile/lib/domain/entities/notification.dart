class AppNotification {
  final String id;
  final String userId;
  final String type;
  final String category;
  final String priority;
  final String status; // READ, UNREAD
  final String title;
  final String body;
  final Map<String, dynamic>? data;
  final String? imageUrl;
  final String? actionUrl;
  final DateTime? readAt;
  final DateTime? openedAt;
  final DateTime? archivedAt;
  final DateTime? expiresAt;
  final DateTime createdAt;
  final DateTime updatedAt;

  AppNotification({
    required this.id,
    required this.userId,
    required this.type,
    required this.category,
    required this.priority,
    required this.status,
    required this.title,
    required this.body,
    this.data,
    this.imageUrl,
    this.actionUrl,
    this.readAt,
    this.openedAt,
    this.archivedAt,
    this.expiresAt,
    required this.createdAt,
    required this.updatedAt,
  });

  bool get isRead => status == 'READ';
  bool get isUnread => status == 'UNREAD';
}
