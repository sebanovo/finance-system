class ServiceProvider {
  final String id;
  final String code;
  final String name;
  final String category;
  final String? serviceCustomerCodeLabel;
  final String? status;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const ServiceProvider({
    required this.id,
    required this.code,
    required this.name,
    required this.category,
    this.serviceCustomerCodeLabel,
    this.status,
    this.createdAt,
    this.updatedAt,
  });

  bool get isActive => (status ?? '').toUpperCase() == 'ACTIVE';

  String get displayCategory {
    return category.replaceAll('_', ' ');
  }
}
