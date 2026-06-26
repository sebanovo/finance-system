import 'package:finance_mobile/core/network/api_client.dart';

import '../../domain/entities/customer_dashboard.dart';
import '../../domain/entities/tenant_summary.dart';

abstract class DashboardRemoteDataSource {
  Future<CustomerDashboard> getCustomerDashboard();
  Future<TenantSummary> getTenantSummary();
}

class DashboardRemoteDataSourceImpl implements DashboardRemoteDataSource {
  final ApiClient apiClient;

  DashboardRemoteDataSourceImpl(this.apiClient);

  @override
  Future<CustomerDashboard> getCustomerDashboard() async {
    final response = await apiClient.get('/api/me/dashboard/summary');
    return _parseCustomerDashboardResponse(response.data, response.statusCode ?? 0);
  }

  @override
  Future<TenantSummary> getTenantSummary() async {
    final response = await apiClient.get('/api/dashboard/tenant/summary');
    return _parseTenantSummaryResponse(response.data, response.statusCode ?? 0);
  }

  CustomerDashboard _parseCustomerDashboardResponse(dynamic raw, int statusCode) {
    if (statusCode == 200) {
      final root = raw as Map<String, dynamic>;
      if (root['success'] == true) {
        final data = root['data'];
        if (data is Map<String, dynamic>) {
          return _mapCustomerDashboard(data);
        }
        throw Exception('Formato inválido del dashboard de cliente');
      }
      throw Exception(root['message'] ?? 'Error al obtener dashboard');
    } else if (statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error $statusCode');
  }

  TenantSummary _parseTenantSummaryResponse(dynamic raw, int statusCode) {
    if (statusCode == 200) {
      final root = raw as Map<String, dynamic>;
      if (root['success'] == true) {
        final data = root['data'];
        if (data is Map<String, dynamic>) {
          return TenantSummary(
            totalUsers: _int(data['totalUsers']),
            maxUsers: _int(data['maxUsers']),
            activePlan: _string(data['activePlan']),
            trialDaysLeft: _int(data['trialDaysLeft']),
          );
        }
        throw Exception('Formato inválido del resumen del tenant');
      }
      throw Exception(root['message'] ?? 'Error al obtener resumen del tenant');
    } else if (statusCode == 401) {
      throw Exception('Sesión expirada');
    }
    throw Exception('Error $statusCode');
  }

  CustomerDashboard _mapCustomerDashboard(Map<String, dynamic> json) {
    return CustomerDashboard(
      metadata: CustomerDashboardMetadata(
        generatedAt: _dateTime(json['metadata']?['generatedAt']),
        timezone: _string(json['metadata']?['timezone']),
        tenantSlug: _string(json['metadata']?['tenantSlug']),
        generatedBy: _string(json['metadata']?['generatedBy']),
        baseCurrency: _string(json['metadata']?['baseCurrency']),
        dataCompleteness: _string(json['metadata']?['dataCompleteness']),
      ),
      summary: CustomerDashboardSummary(
        accounts: _int(json['summary']?['accounts']),
        totalBalance: _customerMoney(json['summary']?['totalBalance']),
        monthlyIncome: _customerMoney(json['summary']?['monthlyIncome']),
        monthlyExpenses: _customerMoney(json['summary']?['monthlyExpenses']),
        pendingTransactions: _int(json['summary']?['pendingTransactions']),
        unreadNotifications: _int(json['summary']?['unreadNotifications']),
      ),
      accounts: CustomerDashboardAccountsSection(
        items: _mapList(
          json['accounts']?['items'],
          (item) => CustomerDashboardAccountItem(
            id: _string(item['id']),
            accountNumber: _string(item['accountNumber']),
            label: _string(item['label']),
            type: _string(item['type']),
            currency: _string(item['currency']),
            status: _string(item['status']),
            balance: _customerMoney(item['balance']),
            createdAt: _dateTime(item['createdAt']),
          ),
        ),
      ),
      balances: CustomerDashboardBalancesSection(
        byCurrency: _mapList(
          json['balances']?['byCurrency'],
          (item) => CustomerDashboardCurrencyBalanceItem(
            currency: _string(item['currency']),
            balance: _customerMoney(item['balance']),
          ),
        ),
      ),
      transactions: CustomerDashboardTransactionsSection(
        monthlyVolume: _mapList(
          json['transactions']?['monthlyVolume'],
          (item) => CustomerDashboardDailyMoneyPoint(
            date: _dateTime(item['date']),
            amount: _customerMoney(item['amount']),
          ),
        ),
        byType: _mapList(
          json['transactions']?['byType'],
          (item) => CustomerDashboardTransactionAggregateItem(
            type: _string(item['type']),
            total: _int(item['total']),
            amount: _customerMoney(item['amount']),
          ),
        ),
        recent: _customerBucket(
          json['transactions']?['recent'],
          (item) => CustomerDashboardTransactionItem(
            id: _string(item['id']),
            reference: _string(item['reference']),
            type: _string(item['type']),
            status: _string(item['status']),
            amount: _customerMoney(item['amount']),
            description: _nullableString(item['description']),
            sourceAccountNumber: _nullableString(item['sourceAccountNumber']),
            targetAccountNumber: _nullableString(item['targetAccountNumber']),
            createdAt: _dateTime(item['createdAt']),
          ),
        ),
        pending: _customerBucket(
          json['transactions']?['pending'],
          (item) => CustomerDashboardTransactionItem(
            id: _string(item['id']),
            reference: _string(item['reference']),
            type: _string(item['type']),
            status: _string(item['status']),
            amount: _customerMoney(item['amount']),
            description: _nullableString(item['description']),
            sourceAccountNumber: _nullableString(item['sourceAccountNumber']),
            targetAccountNumber: _nullableString(item['targetAccountNumber']),
            createdAt: _dateTime(item['createdAt']),
          ),
        ),
      ),
      limits: CustomerDashboardLimitsSection(
        transfer: CustomerDashboardTransferLimits(
          daily: _customerLimitUsage(json['limits']?['transfer']?['daily']),
          monthly: _customerLimitUsage(json['limits']?['transfer']?['monthly']),
        ),
        withdrawal: CustomerDashboardWithdrawalLimits(
          daily: _customerLimitUsage(json['limits']?['withdrawal']?['daily']),
        ),
        activeRules: _mapList(
          json['limits']?['activeRules'],
          (item) => CustomerDashboardLimitRuleItem(
            code: _string(item['code']),
            name: _string(item['name']),
            limitType: _string(item['limitType']),
            scopeType: _string(item['scopeType']),
            period: _nullableString(item['period']),
            transactionType: _nullableString(item['transactionType']),
            currency: _nullableString(item['currency']),
            minAmount: _doubleNullable(item['minAmount']),
            maxAmount: _doubleNullable(item['maxAmount']),
            maxCount: _intNullable(item['maxCount']),
            requireReviewExceed: _bool(item['requireReviewExceed']),
            active: _bool(item['active']),
          ),
        ),
      ),
      notifications: CustomerDashboardNotificationsSection(
        unread: _int(json['notifications']?['unread']),
        items: _mapList(
          json['notifications']?['items'],
          (item) => CustomerDashboardNotificationItem(
            id: _string(item['id']),
            type: _string(item['type']),
            title: _string(item['title']),
            status: _string(item['status']),
            createdAt: _dateTime(item['createdAt']),
          ),
        ),
      ),
      alerts: _mapList(
        json['alerts'],
        (item) => CustomerDashboardAlertItem(
          code: _string(item['code']),
          severity: _string(item['severity']),
          title: _string(item['title']),
          message: _string(item['message']),
          count: _int(item['count']),
          action: _string(item['action']),
        ),
      ),
      insights: _mapList(
        json['insights'],
        (item) => CustomerDashboardInsightItem(
          code: _string(item['code']),
          severity: _string(item['severity']),
          title: _string(item['title']),
          message: _string(item['message']),
          trend: _string(item['trend']),
          value: _string(item['value']),
        ),
      ),
    );
  }

  CustomerDashboardSectionBucket<T> _customerBucket<T>(
    dynamic raw,
    T Function(Map<String, dynamic>) mapper,
  ) {
    final map = raw is Map<String, dynamic> ? raw : <String, dynamic>{};
    return CustomerDashboardSectionBucket<T>(
      total: _int(map['total']),
      items: _mapList(map['items'], mapper),
    );
  }

  CustomerDashboardLimitWindowUsage _customerLimitUsage(dynamic raw) {
    final map = raw is Map<String, dynamic> ? raw : <String, dynamic>{};
    return CustomerDashboardLimitWindowUsage(
      period: _string(map['period']),
      used: _customerMoney(map['used']),
      limit: _customerMoney(map['limit']),
      usedCount: _int(map['usedCount']),
      limitCount: _intNullable(map['limitCount']),
      activeRules: _int(map['activeRules']),
      requiresReview: _bool(map['requiresReview']),
      applicable: _bool(map['applicable']),
    );
  }

  List<T> _mapList<T>(dynamic raw, T Function(Map<String, dynamic>) mapper) {
    if (raw is! List) return const [];
    return raw.whereType<Map>().map((item) => mapper(Map<String, dynamic>.from(item))).toList();
  }

  CustomerDashboardMoney _customerMoney(dynamic raw) {
    final map = raw is Map<String, dynamic> ? raw : <String, dynamic>{};
    return CustomerDashboardMoney(
      amount: _double(map['amount']),
      currency: _string(map['currency']),
    );
  }

  String _string(dynamic value) => value?.toString() ?? '';

  String? _nullableString(dynamic value) => value?.toString();

  double _double(dynamic value) {
    if (value is num) return value.toDouble();
    if (value is String) return double.tryParse(value) ?? 0;
    return 0;
  }

  double? _doubleNullable(dynamic value) {
    if (value == null) return null;
    return _double(value);
  }

  int _int(dynamic value) {
    if (value is num) return value.toInt();
    if (value is String) return int.tryParse(value) ?? 0;
    return 0;
  }

  int? _intNullable(dynamic value) {
    if (value == null) return null;
    if (value is num) return value.toInt();
    if (value is String) return int.tryParse(value);
    return null;
  }

  bool _bool(dynamic value) => value == true;

  DateTime? _dateTime(dynamic value) {
    if (value == null) return null;
    try {
      return DateTime.parse(value.toString());
    } catch (_) {
      return null;
    }
  }
}
