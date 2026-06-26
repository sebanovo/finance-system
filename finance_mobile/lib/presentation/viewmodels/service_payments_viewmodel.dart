import 'package:flutter/material.dart';

import '../../domain/entities/account.dart';
import '../../domain/entities/service_bills_query_result.dart';
import '../../domain/entities/service_enrollment.dart';
import '../../domain/entities/service_payment.dart';
import '../../domain/entities/service_provider_catalog.dart';
import '../../domain/entities/service_provider.dart';
import '../../domain/usecases/create_service_enrollment_usecase.dart';
import '../../domain/usecases/create_service_payment_usecase.dart';
import '../../domain/usecases/delete_service_enrollment_usecase.dart';
import '../../domain/usecases/get_accounts_usecase.dart';
import '../../domain/usecases/get_service_enrollments_usecase.dart';
import '../../domain/usecases/get_service_provider_catalog_usecase.dart';
import '../../domain/usecases/get_service_payment_usecase.dart';
import '../../domain/usecases/get_service_payments_usecase.dart';
import '../../domain/usecases/get_service_providers_usecase.dart';
import '../../domain/usecases/query_service_bills_usecase.dart';

class ServicePaymentsViewModel extends ChangeNotifier {
  final GetServiceProvidersUseCase getServiceProvidersUseCase;
  final GetServiceProviderCatalogUseCase getServiceProviderCatalogUseCase;
  final GetServiceEnrollmentsUseCase getServiceEnrollmentsUseCase;
  final CreateServiceEnrollmentUseCase createServiceEnrollmentUseCase;
  final DeleteServiceEnrollmentUseCase deleteServiceEnrollmentUseCase;
  final QueryServiceBillsUseCase queryServiceBillsUseCase;
  final CreateServicePaymentUseCase createServicePaymentUseCase;
  final GetServicePaymentsUseCase getServicePaymentsUseCase;
  final GetServicePaymentUseCase getServicePaymentUseCase;
  final GetAccountsUseCase getAccountsUseCase;

  List<ServiceProvider> _providers = [];
  List<ServiceEnrollment> _enrollments = [];
  List<ServicePayment> _payments = [];
  List<Account> _accounts = [];
  List<ServiceProviderCatalog> _providerCatalog = [];
  ServiceBillsQueryResult? _currentBillsQuery;
  ServicePayment? _lastCreatedPayment;
  bool _loadingProviders = false;
  bool _loadingEnrollments = false;
  bool _loadingPayments = false;
  bool _loadingAccounts = false;
  bool _queryingBills = false;
  bool _creatingEnrollment = false;
  bool _deletingEnrollment = false;
  bool _creatingPayment = false;
  bool _loadingMorePayments = false;
  int _paymentsPage = 0;
  int _paymentsPageSize = 20;
  bool _hasMorePayments = false;
  String? _errorMessage;
  bool _enrollmentCreated = false;
  bool _enrollmentDeleted = false;
  bool _paymentCreated = false;

  ServicePaymentsViewModel({
    required this.getServiceProvidersUseCase,
    required this.getServiceProviderCatalogUseCase,
    required this.getServiceEnrollmentsUseCase,
    required this.createServiceEnrollmentUseCase,
    required this.deleteServiceEnrollmentUseCase,
    required this.queryServiceBillsUseCase,
    required this.createServicePaymentUseCase,
    required this.getServicePaymentsUseCase,
    required this.getServicePaymentUseCase,
    required this.getAccountsUseCase,
  });

  List<ServiceProvider> get providers => _providers;
  List<ServiceEnrollment> get enrollments => _enrollments;
  List<ServicePayment> get payments => _payments;
  List<Account> get accounts => _accounts;
  List<ServiceProviderCatalog> get providerCatalog => _providerCatalog;
  ServiceBillsQueryResult? get currentBillsQuery => _currentBillsQuery;
  bool get loadingProviders => _loadingProviders;
  bool get loadingEnrollments => _loadingEnrollments;
  bool get loadingPayments => _loadingPayments;
  bool get loadingAccounts => _loadingAccounts;
  bool get queryingBills => _queryingBills;
  bool get creatingEnrollment => _creatingEnrollment;
  bool get deletingEnrollment => _deletingEnrollment;
  bool get creatingPayment => _creatingPayment;
  bool get loadingMorePayments => _loadingMorePayments;
  bool get hasMorePayments => _hasMorePayments;
  String? get errorMessage => _errorMessage;
  bool get enrollmentCreated => _enrollmentCreated;
  bool get enrollmentDeleted => _enrollmentDeleted;
  bool get paymentCreated => _paymentCreated;
  ServicePayment? get lastCreatedPayment => _lastCreatedPayment;

  bool get hasProviders => _providers.isNotEmpty;
  bool get hasEnrollments => _enrollments.isNotEmpty;
  bool get hasAccounts => _accounts.isNotEmpty;

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  void clearEnrollmentCreated() {
    _enrollmentCreated = false;
  }

  void clearEnrollmentDeleted() {
    _enrollmentDeleted = false;
  }

  void clearPaymentCreated() {
    _paymentCreated = false;
  }

  void clearLastCreatedPayment() {
    _lastCreatedPayment = null;
  }

  void clearCurrentBillsQuery() {
    _currentBillsQuery = null;
    notifyListeners();
  }

  Future<void> loadData({
    bool includeEnrollments = true,
    bool includePayments = true,
  }) async {
    await Future.wait([
      loadProviders(),
      loadProviderCatalog(),
      if (includeEnrollments) loadEnrollments(),
      loadAccounts(),
      if (includePayments) loadPayments(),
    ]);
  }

  Future<void> loadProviders() async {
    _loadingProviders = true;
    notifyListeners();
    try {
      _providers = await getServiceProvidersUseCase();
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingProviders = false;
      notifyListeners();
    }
  }

  Future<void> loadProviderCatalog() async {
    try {
      _providerCatalog = await getServiceProviderCatalogUseCase();
    } catch (e) {
      _providerCatalog = [];
    } finally {
      notifyListeners();
    }
  }

  Future<void> loadEnrollments() async {
    _loadingEnrollments = true;
    notifyListeners();
    try {
      _enrollments = await getServiceEnrollmentsUseCase(status: 'ACTIVE');
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingEnrollments = false;
      notifyListeners();
    }
  }

  Future<void> loadPayments({
    String? providerId,
    String? receiptNumber,
    String? accountNumber,
    String? userId,
    String? billId,
    int page = 0,
    int size = 20,
    bool append = false,
  }) async {
    _loadingPayments = !append;
    notifyListeners();
    try {
      final items = await getServicePaymentsUseCase(
        providerId: providerId,
        receiptNumber: receiptNumber,
        accountNumber: accountNumber,
        userId: userId,
        billId: billId,
        page: page,
        size: size,
      );
      _paymentsPage = page;
      _paymentsPageSize = size;
      _hasMorePayments = items.length >= size;
      if (append) {
        _payments = [..._payments, ...items];
      } else {
        _payments = items;
      }
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingPayments = false;
      notifyListeners();
    }
  }

  Future<void> loadMorePayments({
    String? providerId,
    String? receiptNumber,
    String? accountNumber,
    String? userId,
    String? billId,
  }) async {
    if (_loadingPayments || _loadingMorePayments || !_hasMorePayments) {
      return;
    }
    _loadingMorePayments = true;
    notifyListeners();
    try {
      await loadPayments(
        providerId: providerId,
        receiptNumber: receiptNumber,
        accountNumber: accountNumber,
        userId: userId,
        billId: billId,
        page: _paymentsPage + 1,
        size: _paymentsPageSize,
        append: true,
      );
    } finally {
      _loadingMorePayments = false;
      notifyListeners();
    }
  }

  Future<void> loadAccounts() async {
    _loadingAccounts = true;
    notifyListeners();
    try {
      _accounts = await getAccountsUseCase();
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loadingAccounts = false;
      notifyListeners();
    }
  }

  Future<void> createEnrollment({
    required String providerId,
    required String serviceCustomerCode,
    String? alias,
  }) async {
    _creatingEnrollment = true;
    _errorMessage = null;
    _enrollmentCreated = false;
    notifyListeners();

    try {
      await createServiceEnrollmentUseCase(
        providerId: providerId,
        serviceCustomerCode: serviceCustomerCode,
        alias: alias,
      );
      _enrollmentCreated = true;
      await loadEnrollments();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creatingEnrollment = false;
      notifyListeners();
    }
  }

  Future<void> deleteEnrollment(String enrollmentId) async {
    _deletingEnrollment = true;
    _errorMessage = null;
    _enrollmentDeleted = false;
    notifyListeners();

    try {
      await deleteServiceEnrollmentUseCase(enrollmentId);
      _enrollmentDeleted = true;
      await loadEnrollments();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _deletingEnrollment = false;
      notifyListeners();
    }
  }

  Future<void> queryBills({
    String? enrollmentId,
    String? providerId,
    String? serviceCustomerCode,
  }) async {
    _queryingBills = true;
    _errorMessage = null;
    _currentBillsQuery = null;
    notifyListeners();

    try {
      _currentBillsQuery = await queryServiceBillsUseCase(
        enrollmentId: enrollmentId,
        providerId: providerId,
        serviceCustomerCode: serviceCustomerCode,
      );
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _queryingBills = false;
      notifyListeners();
    }
  }

  Future<void> createPayment({
    required String sourceAccountNumber,
    required String providerId,
    required String serviceCustomerCode,
    required String billId,
    String? enrollmentId,
    required String idempotencyKey,
  }) async {
    _creatingPayment = true;
    _errorMessage = null;
    _paymentCreated = false;
    notifyListeners();

    try {
      _lastCreatedPayment = await createServicePaymentUseCase(
        sourceAccountNumber: sourceAccountNumber,
        providerId: providerId,
        serviceCustomerCode: serviceCustomerCode,
        billId: billId,
        enrollmentId: enrollmentId,
        idempotencyKey: idempotencyKey,
      );
      _paymentCreated = true;
      await loadPayments();
      await loadEnrollments();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    } finally {
      _creatingPayment = false;
      notifyListeners();
    }
  }
}
