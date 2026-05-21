import '../../../domain/entities/notification.dart';

class NotificationModel {
  final String id;
  final String userId;
  final String type;
  final String category;
  final String priority;
  final String status;
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

  NotificationModel({
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

  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    return NotificationModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      type: json['type'] ?? '',
      category: json['category'] ?? '',
      priority: json['priority'] ?? '',
      status: json['status'] ?? '',
      title: json['title'] ?? '',
      body: json['body'] ?? '',
      data: json['data'] != null
          ? Map<String, dynamic>.from(json['data'])
          : null,
      imageUrl: json['imageUrl'],
      actionUrl: json['actionUrl'],
      readAt: json['readAt'] != null
          ? DateTime.parse(json['readAt'])
          : null, // ✅ Nullable
      openedAt: json['openedAt'] != null
          ? DateTime.parse(json['openedAt'])
          : null, // ✅ Nullable
      archivedAt: json['archivedAt'] != null
          ? DateTime.parse(json['archivedAt'])
          : null, // ✅ Nullable
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'])
          : null, // ✅ Nullable
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  AppNotification toEntity() {
    return AppNotification(
      id: id,
      userId: userId,
      type: type,
      category: category,
      priority: priority,
      status: status,
      title: title,
      body: body,
      data: data,
      imageUrl: imageUrl,
      actionUrl: actionUrl,
      readAt: readAt,
      openedAt: openedAt,
      archivedAt: archivedAt,
      expiresAt: expiresAt,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
