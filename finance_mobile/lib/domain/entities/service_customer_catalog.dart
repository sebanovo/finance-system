class ServiceCustomerCatalog {
  final String id;
  final String serviceCustomerCode;
  final String customerName;
  final String? status;

  const ServiceCustomerCatalog({
    required this.id,
    required this.serviceCustomerCode,
    required this.customerName,
    required this.status,
  });
}
