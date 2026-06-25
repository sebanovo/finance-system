import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

import '../../domain/entities/customer_dashboard.dart';

class CustomerDashboardView extends StatelessWidget {
  final CustomerDashboard? dashboard;
  final bool isLoading;
  final String? errorMessage;
  final VoidCallback onOpenAccounts;
  final VoidCallback onOpenTransactions;
  final VoidCallback? onOpenLoans;
  final VoidCallback? onOpenLimits;
  final VoidCallback onOpenNotifications;
  final VoidCallback onOpenServicePayments;

  const CustomerDashboardView({
    super.key,
    required this.dashboard,
    required this.isLoading,
    required this.errorMessage,
    required this.onOpenAccounts,
    required this.onOpenTransactions,
    this.onOpenLoans,
    this.onOpenLimits,
    required this.onOpenNotifications,
    required this.onOpenServicePayments,
  });

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Skeletonizer(
        enabled: true,
        child: _buildContent(context, _placeholderDashboard()),
      );
    }

    if (errorMessage != null) {
      return _buildErrorCard(errorMessage!);
    }

    if (dashboard == null) {
      return _buildMissingDashboardCard();
    }

    return _buildContent(context, dashboard!);
  }

  Widget _buildContent(BuildContext context, CustomerDashboard data) {
    final displayAccounts = data.accounts.items.take(3).toList();
    final displayBalances = data.balances.byCurrency.take(4).toList();
    final displayRecentTransactions = data.transactions.recent.items.take(3).toList();
    final displayPendingTransactions = data.transactions.pending.items.take(3).toList();
    final displayNotifications = data.notifications.items.take(3).toList();
    final displayAlerts = data.alerts.take(3).toList();
    final displayInsights = data.insights.take(3).toList();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeroCard(data),
        const SizedBox(height: 16),
        _buildSummaryGrid(data.summary),
        const SizedBox(height: 16),
        _buildAccountsSection(displayAccounts),
        const SizedBox(height: 16),
        _buildBalancesSection(displayBalances),
        const SizedBox(height: 16),
        _buildTransactionsSection(data.transactions, displayRecentTransactions, displayPendingTransactions),
        const SizedBox(height: 16),
        _buildLimitsSection(data.limits),
        const SizedBox(height: 16),
        _buildNotificationsSection(data.notifications, displayNotifications),
        const SizedBox(height: 16),
        _buildAlertsSection(displayAlerts),
        const SizedBox(height: 16),
        _buildInsightsSection(displayInsights),
      ],
    );
  }

  Widget _buildHeroCard(CustomerDashboard data) {
    final metadata = data.metadata;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF66BB6A)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(
            color: Colors.green.shade200.withValues(alpha: 0.45),
            blurRadius: 16,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Dashboard de cliente',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      'Datos resumidos para tu cuenta y tus movimientos más recientes.',
                      style: TextStyle(
                        color: Colors.white.withValues(alpha: 0.9),
                        fontSize: 13,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 12),
              _buildCurrencyChip(metadata.baseCurrency),
            ],
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _buildHeroInfoChip(Icons.domain, metadata.tenantSlug),
              _buildHeroInfoChip(Icons.schedule, _formatDateTime(metadata.generatedAt)),
              _buildHeroInfoChip(Icons.verified_outlined, metadata.dataCompleteness),
            ],
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: [
              _buildHeroActionButton(
                icon: Icons.account_balance_wallet_outlined,
                label: 'Mis cuentas',
                onPressed: onOpenAccounts,
              ),
              _buildHeroActionButton(
                icon: Icons.swap_horiz,
                label: 'Movimientos',
                onPressed: onOpenTransactions,
              ),
              if (onOpenLoans != null)
                _buildHeroActionButton(
                  icon: Icons.savings_outlined,
                  label: 'Mis préstamos',
                  onPressed: onOpenLoans!,
                ),
              if (onOpenLimits != null)
                _buildHeroActionButton(
                  icon: Icons.shield_outlined,
                  label: 'Límites',
                  onPressed: onOpenLimits!,
                ),
              _buildHeroActionButton(
                icon: Icons.notifications_active_outlined,
                label: 'Notificaciones',
                onPressed: onOpenNotifications,
              ),
              _buildHeroActionButton(
                icon: Icons.receipt_long_outlined,
                label: 'Pagos de servicios',
                onPressed: onOpenServicePayments,
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildSummaryGrid(CustomerDashboardSummary summary) {
    final items = [
      _MetricData('Cuentas', '${summary.accounts}', Icons.account_balance_wallet_outlined),
      _MetricData('Saldo total', _amountOnly(summary.totalBalance), Icons.savings_outlined),
      _MetricData('Ingresos mes', _amountOnly(summary.monthlyIncome), Icons.trending_up_rounded),
      _MetricData('Gastos mes', _amountOnly(summary.monthlyExpenses), Icons.trending_down_rounded),
      _MetricData('Pendientes', '${summary.pendingTransactions}', Icons.schedule_outlined),
      _MetricData('No leídas', '${summary.unreadNotifications}', Icons.mark_email_unread_outlined),
    ];

    return LayoutBuilder(
      builder: (context, constraints) {
        final columns = constraints.maxWidth >= 500 ? 3 : 2;
        final itemWidth = (constraints.maxWidth - ((columns - 1) * 10)) / columns;

        return Wrap(
          spacing: 10,
          runSpacing: 10,
          children: items
              .map(
                (item) => SizedBox(
                  width: itemWidth,
                  child: _MetricCard(data: item),
                ),
              )
              .toList(),
        );
      },
    );
  }

  Widget _buildAccountsSection(List<CustomerDashboardAccountItem> accounts) {
    return _SectionCard(
      title: 'Tus cuentas',
      subtitle: 'Solo las más relevantes',
      child: accounts.isEmpty
          ? const _EmptySection(message: 'No hay cuentas para mostrar')
          : Column(
              children: accounts
                  .map(
                    (account) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _AccountTile(account: account),
                    ),
                  )
                  .toList(),
            ),
    );
  }

  Widget _buildBalancesSection(List<CustomerDashboardCurrencyBalanceItem> balances) {
    final maxValue = balances.isEmpty
        ? 1.0
        : balances
            .map((item) => item.balance.amount)
            .reduce((left, right) => left > right ? left : right);

    return _SectionCard(
      title: 'Saldos por moneda',
      subtitle: 'Distribución compacta',
      child: balances.isEmpty
          ? const _EmptySection(message: 'Sin saldos por moneda')
          : Column(
              children: balances
                  .map(
                    (balance) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _ProgressTile(
                        label: balance.currency,
                        value: _money(balance.balance),
                        ratio: maxValue == 0 ? 0 : balance.balance.amount / maxValue,
                      ),
                    ),
                  )
                  .toList(),
            ),
    );
  }

  Widget _buildTransactionsSection(
    CustomerDashboardTransactionsSection transactions,
    List<CustomerDashboardTransactionItem> recent,
    List<CustomerDashboardTransactionItem> pending,
  ) {
    final maxValue = transactions.byType.isEmpty
        ? 1.0
        : transactions.byType
            .map((item) => item.amount.amount)
            .reduce((left, right) => left > right ? left : right);

    return _SectionCard(
      title: 'Movimientos',
      subtitle: 'Distribución por tipo y últimos eventos',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (transactions.byType.isNotEmpty) ...[
            Text(
              'Por tipo',
              style: TextStyle(
                color: Colors.green.shade900,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 10),
            ...transactions.byType.map(
              (item) => Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: _ProgressTile(
                  label: '${_labelForTransactionType(item.type)} · ${item.total}',
                  value: _money(item.amount),
                  ratio: maxValue == 0 ? 0 : item.amount.amount / maxValue,
                ),
              ),
            ),
          ] else
            const _EmptySection(message: 'Sin movimientos para mostrar'),
          if (transactions.monthlyVolume.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              'Volumen reciente',
              style: TextStyle(
                color: Colors.green.shade900,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 10),
            _CompactVolumeChart(points: transactions.monthlyVolume),
          ],
          if (recent.isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(
              'Recientes',
              style: TextStyle(
                color: Colors.green.shade900,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 10),
            ...recent.map((item) => Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: _TransactionMiniCard(item: item),
                )),
          ],
          if (pending.isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(
              'Pendientes',
              style: TextStyle(
                color: Colors.green.shade900,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 10),
            ...pending.map((item) => Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: _TransactionMiniCard(item: item),
                )),
          ],
        ],
      ),
    );
  }

  Widget _buildLimitsSection(CustomerDashboardLimitsSection limits) {
    return _SectionCard(
      title: 'Límites',
      subtitle: 'Uso resumido y reglas activas',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _LimitSection(
            title: 'Transferencias',
            daily: limits.transfer.daily,
            monthly: limits.transfer.monthly,
          ),
          const SizedBox(height: 12),
          _LimitSection(
            title: 'Retiros',
            daily: limits.withdrawal.daily,
          ),
          const SizedBox(height: 12),
          if (limits.activeRules.isNotEmpty) ...[
            Text(
              'Reglas activas',
              style: TextStyle(
                color: Colors.green.shade900,
                fontWeight: FontWeight.w700,
              ),
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: limits.activeRules
                  .take(4)
                  .map((rule) => _RuleChip(rule: rule))
                  .toList(),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildNotificationsSection(
    CustomerDashboardNotificationsSection notifications,
    List<CustomerDashboardNotificationItem> items,
  ) {
    return _SectionCard(
      title: 'Notificaciones',
      subtitle: 'Lo más reciente',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _SummaryPill(
            label: 'No leídas',
            value: '${notifications.unread}',
          ),
          const SizedBox(height: 12),
          if (items.isEmpty)
            const _EmptySection(message: 'No hay notificaciones recientes')
          else
            ...items.map(
              (item) => Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: _NotificationMiniCard(item: item),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildAlertsSection(List<CustomerDashboardAlertItem> alerts) {
    return _SectionCard(
      title: 'Alertas',
      subtitle: 'Solo lo importante',
      child: alerts.isEmpty
          ? const _EmptySection(message: 'Sin alertas activas')
          : Column(
              children: alerts
                  .map(
                    (alert) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _AlertMiniCard(alert: alert),
                    ),
                  )
                  .toList(),
            ),
    );
  }

  Widget _buildInsightsSection(List<CustomerDashboardInsightItem> insights) {
    return _SectionCard(
      title: 'Insights',
      subtitle: 'Señales útiles para tu cuenta',
      child: insights.isEmpty
          ? const _EmptySection(message: 'Sin insights disponibles')
          : Column(
              children: insights
                  .map(
                    (insight) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _InsightMiniCard(insight: insight),
                    ),
                  )
                  .toList(),
            ),
    );
  }

  Widget _buildErrorCard(String message) {
    return Card(
      color: Colors.red.shade50,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(Icons.error_outline, color: Colors.red.shade700),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                message,
                style: TextStyle(color: Colors.red.shade800),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCurrencyChip(String currency) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: Colors.white.withValues(alpha: 0.2)),
      ),
      child: Text(
        currency,
        style: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildHeroInfoChip(IconData icon, String label) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.14),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 16, color: Colors.white),
          const SizedBox(width: 6),
          Text(
            label.isEmpty ? 'N/D' : label,
            style: const TextStyle(color: Colors.white),
          ),
        ],
      ),
    );
  }

  Widget _buildHeroActionButton({
    required IconData icon,
    required String label,
    required VoidCallback onPressed,
  }) {
    return Material(
      color: Colors.white,
      borderRadius: BorderRadius.circular(16),
      child: InkWell(
        onTap: onPressed,
        borderRadius: BorderRadius.circular(16),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 18, color: const Color(0xFF2E7D32)),
              const SizedBox(width: 8),
              Text(
                label,
                style: const TextStyle(
                  color: Color(0xFF2E7D32),
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  CustomerDashboard _placeholderDashboard() {
    return CustomerDashboard(
      metadata: CustomerDashboardMetadata(
        generatedAt: DateTime(2026, 1, 1, 12, 0),
        timezone: 'UTC',
        tenantSlug: 'demo',
        generatedBy: 'system',
        baseCurrency: 'BOB',
        dataCompleteness: 'FULL',
      ),
      summary: const CustomerDashboardSummary(
        accounts: 3,
        totalBalance: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
        monthlyIncome: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
        monthlyExpenses: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
        pendingTransactions: 0,
        unreadNotifications: 0,
      ),
      accounts: const CustomerDashboardAccountsSection(items: [
        CustomerDashboardAccountItem(
          id: '1',
          accountNumber: '0000000001',
          label: 'Cuenta Demo',
          type: 'WALLET',
          currency: 'BOB',
          status: 'ACTIVE',
          balance: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
          createdAt: null,
        ),
      ]),
      balances: const CustomerDashboardBalancesSection(byCurrency: [
        CustomerDashboardCurrencyBalanceItem(
          currency: 'BOB',
          balance: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
        ),
      ]),
      transactions: const CustomerDashboardTransactionsSection(
        monthlyVolume: [
          CustomerDashboardDailyMoneyPoint(
            date: null,
            amount: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
          ),
        ],
        byType: [
          CustomerDashboardTransactionAggregateItem(
            type: 'TRANSFER',
            total: 0,
            amount: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
          ),
        ],
        recent: CustomerDashboardSectionBucket(
          total: 0,
          items: [],
        ),
        pending: CustomerDashboardSectionBucket(
          total: 0,
          items: [],
        ),
      ),
      limits: const CustomerDashboardLimitsSection(
        transfer: CustomerDashboardTransferLimits(
          daily: CustomerDashboardLimitWindowUsage(
            period: 'DAILY',
            used: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            limit: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            usedCount: 0,
            limitCount: 0,
            activeRules: 0,
            requiresReview: false,
            applicable: false,
          ),
          monthly: CustomerDashboardLimitWindowUsage(
            period: 'MONTHLY',
            used: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            limit: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            usedCount: 0,
            limitCount: 0,
            activeRules: 0,
            requiresReview: false,
            applicable: false,
          ),
        ),
        withdrawal: CustomerDashboardWithdrawalLimits(
          daily: CustomerDashboardLimitWindowUsage(
            period: 'DAILY',
            used: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            limit: CustomerDashboardMoney(amount: 0, currency: 'BOB'),
            usedCount: 0,
            limitCount: 0,
            activeRules: 0,
            requiresReview: false,
            applicable: false,
          ),
        ),
        activeRules: [],
      ),
      notifications: const CustomerDashboardNotificationsSection(
        unread: 0,
        items: [],
      ),
      alerts: const [],
      insights: const [],
    );
  }

  Widget _buildMissingDashboardCard() {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(Icons.info_outline, color: Colors.green.shade700),
            const SizedBox(width: 12),
            const Expanded(
              child: Text(
                'Este perfil no usa el dashboard de cliente. Las opciones visibles se ajustan por permisos.',
                style: TextStyle(color: Colors.black87),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _money(CustomerDashboardMoney money) {
    return '${money.amount.toStringAsFixed(2)} ${money.currency}';
  }

  String _amountOnly(CustomerDashboardMoney money) {
    return money.amount.toStringAsFixed(2);
  }

  String _formatDateTime(DateTime? value) {
    if (value == null) return 'N/D';
    final local = value.toLocal();
    final day = local.day.toString().padLeft(2, '0');
    final month = local.month.toString().padLeft(2, '0');
    final year = local.year.toString();
    final hour = local.hour.toString().padLeft(2, '0');
    final minute = local.minute.toString().padLeft(2, '0');
    return '$day/$month/$year $hour:$minute';
  }

  String _labelForTransactionType(String value) {
    switch (value) {
      case 'TRANSFER':
        return 'Transferencia';
      case 'DEPOSIT':
        return 'Depósito';
      case 'WITHDRAWAL':
        return 'Retiro';
      case 'PAYMENT':
        return 'Pago';
      case 'FEE':
        return 'Tarifa';
      case 'HOLD':
        return 'Retención';
      case 'RELEASE':
        return 'Liberación';
      case 'ADJUSTMENT':
        return 'Ajuste';
      default:
        return value;
    }
  }
}

class _MetricData {
  final String label;
  final String value;
  final IconData icon;

  const _MetricData(this.label, this.value, this.icon);
}

class _MetricCard extends StatelessWidget {
  final _MetricData data;

  const _MetricCard({required this.data});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFFD7E7D6)),
        boxShadow: [
          BoxShadow(
            color: Colors.green.shade50,
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: const Color(0xFFE8F5E9),
              borderRadius: BorderRadius.circular(14),
            ),
            child: Icon(data.icon, color: const Color(0xFF2E7D32)),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  data.label,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Colors.black54,
                    fontWeight: FontWeight.w600,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 6),
                Text(
                  data.value,
                  style: const TextStyle(
                    fontSize: 17,
                    fontWeight: FontWeight.bold,
                    color: Colors.black87,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SectionCard extends StatelessWidget {
  final String title;
  final String? subtitle;
  final Widget child;

  const _SectionCard({
    required this.title,
    required this.child,
    this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(22),
        border: Border.all(color: const Color(0xFFD7E7D6)),
        boxShadow: [
          BoxShadow(
            color: Colors.green.shade50,
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Color(0xFF1B5E20),
            ),
          ),
          if (subtitle != null) ...[
            const SizedBox(height: 4),
            Text(
              subtitle!,
              style: const TextStyle(color: Colors.black54, fontSize: 12),
            ),
          ],
          const SizedBox(height: 14),
          child,
        ],
      ),
    );
  }
}

class _EmptySection extends StatelessWidget {
  final String message;

  const _EmptySection({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFF6FBF5),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Text(
        message,
        style: const TextStyle(color: Colors.black54),
      ),
    );
  }
}

class _AccountTile extends StatelessWidget {
  final CustomerDashboardAccountItem account;

  const _AccountTile({required this.account});

  @override
  Widget build(BuildContext context) {
    final statusColor = _statusColor(account.status);

    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 20,
            backgroundColor: const Color(0xFFE8F5E9),
            child: Icon(
              account.type.toUpperCase().contains('WALLET')
                  ? Icons.account_balance_wallet_outlined
                  : Icons.credit_card_outlined,
              color: const Color(0xFF2E7D32),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  account.label.isNotEmpty ? account.label : 'Cuenta',
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 15,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  account.accountNumber,
                  style: const TextStyle(color: Colors.black54, fontSize: 12),
                ),
                const SizedBox(height: 8),
                Text(
                  '${account.balance.amount.toStringAsFixed(2)} ${account.currency}',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w700,
                    color: Color(0xFF1B5E20),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
            decoration: BoxDecoration(
              color: statusColor.withValues(alpha: 0.12),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Text(
              account.status,
              style: TextStyle(
                color: statusColor,
                fontSize: 11,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Color _statusColor(String value) {
    switch (value) {
      case 'ACTIVE':
        return const Color(0xFF2E7D32);
      case 'PENDING_APPROVAL':
      case 'PENDING_VERIFICATION':
        return Colors.orange;
      case 'FROZEN':
      case 'BLOCKED':
      case 'SUSPENDED':
        return Colors.red;
      case 'CLOSED':
        return Colors.grey;
      default:
        return const Color(0xFF2E7D32);
    }
  }
}

class _ProgressTile extends StatelessWidget {
  final String label;
  final String value;
  final double ratio;

  const _ProgressTile({
    required this.label,
    required this.value,
    required this.ratio,
  });

  @override
  Widget build(BuildContext context) {
    final normalized = ratio.isFinite ? ratio.clamp(0.0, 1.0).toDouble() : 0.0;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Text(
                label,
                style: const TextStyle(fontWeight: FontWeight.w600),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            const SizedBox(width: 12),
            Text(
              value,
              style: const TextStyle(fontSize: 12, color: Colors.black54),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: LinearProgressIndicator(
            minHeight: 10,
            value: normalized,
            backgroundColor: const Color(0xFFE6EFE4),
            valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFF2E7D32)),
          ),
        ),
      ],
    );
  }
}

class _CompactVolumeChart extends StatelessWidget {
  final List<CustomerDashboardDailyMoneyPoint> points;

  const _CompactVolumeChart({required this.points});

  @override
  Widget build(BuildContext context) {
    final maxValue = points.isEmpty
        ? 1.0
        : points
            .map((item) => item.amount.amount)
            .reduce((left, right) => left > right ? left : right);

    return Column(
      children: points.take(7).map((point) {
        final ratio = maxValue == 0 ? 0 : point.amount.amount / maxValue;
        return Padding(
          padding: const EdgeInsets.only(bottom: 8),
          child: Row(
            children: [
              SizedBox(
                width: 72,
                child: Text(
                  _formatShortDate(point.date),
                  style: const TextStyle(fontSize: 12),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(10),
                  child: LinearProgressIndicator(
                    minHeight: 10,
                    value: ratio.clamp(0.0, 1.0).toDouble(),
                    backgroundColor: const Color(0xFFE6EFE4),
                    valueColor: const AlwaysStoppedAnimation<Color>(
                      Color(0xFF4CAF50),
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 8),
              SizedBox(
                width: 92,
                child: Text(
                  '${point.amount.amount.toStringAsFixed(2)} ${point.amount.currency}',
                  textAlign: TextAlign.end,
                  style: const TextStyle(fontSize: 12, color: Colors.black54),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }

  String _formatShortDate(DateTime? value) {
    if (value == null) return 'N/D';
    final local = value.toLocal();
    return '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}';
  }
}

class _TransactionMiniCard extends StatelessWidget {
  final CustomerDashboardTransactionItem item;

  const _TransactionMiniCard({required this.item});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  _label(item.type),
                  style: const TextStyle(
                    fontWeight: FontWeight.w700,
                    fontSize: 14,
                  ),
                ),
              ),
              Text(
                '${item.amount.amount.toStringAsFixed(2)} ${item.amount.currency}',
                style: const TextStyle(
                  color: Color(0xFF1B5E20),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            item.reference,
            style: const TextStyle(color: Colors.black54, fontSize: 12),
          ),
          if ((item.sourceAccountNumber ?? '').isNotEmpty ||
              (item.targetAccountNumber ?? '').isNotEmpty) ...[
            const SizedBox(height: 6),
            Text(
              [
                if ((item.sourceAccountNumber ?? '').isNotEmpty)
                  'Origen ${item.sourceAccountNumber}',
                if ((item.targetAccountNumber ?? '').isNotEmpty)
                  'Destino ${item.targetAccountNumber}',
              ].join(' · '),
              style: const TextStyle(fontSize: 11, color: Colors.black54),
            ),
          ],
        ],
      ),
    );
  }

  String _label(String value) {
    switch (value) {
      case 'TRANSFER':
        return 'Transferencia';
      case 'DEPOSIT':
        return 'Depósito';
      case 'WITHDRAWAL':
        return 'Retiro';
      case 'PAYMENT':
        return 'Pago';
      case 'HOLD':
        return 'Retención';
      case 'RELEASE':
        return 'Liberación';
      default:
        return value;
    }
  }
}

class _LimitSection extends StatelessWidget {
  final String title;
  final CustomerDashboardLimitWindowUsage daily;
  final CustomerDashboardLimitWindowUsage? monthly;

  const _LimitSection({
    required this.title,
    required this.daily,
    this.monthly,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 10),
          _LimitUsageRow(label: 'Día', usage: daily),
          if (monthly != null) ...[
            const SizedBox(height: 10),
            _LimitUsageRow(label: 'Mes', usage: monthly!),
          ],
        ],
      ),
    );
  }
}

class _LimitUsageRow extends StatelessWidget {
  final String label;
  final CustomerDashboardLimitWindowUsage usage;

  const _LimitUsageRow({
    required this.label,
    required this.usage,
  });

  @override
  Widget build(BuildContext context) {
    final ratio = usage.limit.amount <= 0
        ? 0.0
        : (usage.used.amount / usage.limit.amount).clamp(0.0, 1.0).toDouble();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              label,
              style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
            ),
            Text(
              '${usage.used.amount.toStringAsFixed(2)} / ${usage.limit.amount.toStringAsFixed(2)} ${usage.limit.currency}',
              style: const TextStyle(fontSize: 12, color: Colors.black54),
            ),
          ],
        ),
        const SizedBox(height: 6),
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: LinearProgressIndicator(
            minHeight: 10,
            value: ratio,
            backgroundColor: const Color(0xFFE6EFE4),
            valueColor: AlwaysStoppedAnimation<Color>(
              usage.requiresReview ? Colors.orange : const Color(0xFF2E7D32),
            ),
          ),
        ),
      ],
    );
  }
}

class _SummaryPill extends StatelessWidget {
  final String label;
  final String value;

  const _SummaryPill({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            '$label: ',
            style: const TextStyle(
              fontWeight: FontWeight.w700,
              color: Color(0xFF1B5E20),
            ),
          ),
          Text(
            value,
            style: const TextStyle(color: Color(0xFF1B5E20)),
          ),
        ],
      ),
    );
  }
}

class _RuleChip extends StatelessWidget {
  final CustomerDashboardLimitRuleItem rule;

  const _RuleChip({required this.rule});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: rule.active ? const Color(0xFFE8F5E9) : const Color(0xFFF5F5F5),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Text(
        rule.name,
        style: TextStyle(
          color: rule.active ? const Color(0xFF1B5E20) : Colors.black54,
          fontSize: 12,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}

class _NotificationMiniCard extends StatelessWidget {
  final CustomerDashboardNotificationItem item;

  const _NotificationMiniCard({required this.item});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  item.title,
                  style: const TextStyle(
                    fontWeight: FontWeight.w700,
                    fontSize: 14,
                  ),
                ),
              ),
              Text(
                item.status,
                style: const TextStyle(
                  fontSize: 11,
                  color: Color(0xFF2E7D32),
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            '${item.type} · ${_formatDateTime(item.createdAt)}',
            style: const TextStyle(fontSize: 12, color: Colors.black54),
          ),
        ],
      ),
    );
  }

  String _formatDateTime(DateTime? value) {
    if (value == null) return 'N/D';
    final local = value.toLocal();
    final day = local.day.toString().padLeft(2, '0');
    final month = local.month.toString().padLeft(2, '0');
    final hour = local.hour.toString().padLeft(2, '0');
    final minute = local.minute.toString().padLeft(2, '0');
    return '$day/$month $hour:$minute';
  }
}

class _AlertMiniCard extends StatelessWidget {
  final CustomerDashboardAlertItem alert;

  const _AlertMiniCard({required this.alert});

  @override
  Widget build(BuildContext context) {
    final color = _severityColor(alert.severity);

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.08),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withValues(alpha: 0.18)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  alert.title,
                  style: const TextStyle(fontWeight: FontWeight.w700),
                ),
              ),
              Text(
                '${alert.count}',
                style: TextStyle(
                  color: color,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            alert.message,
            style: const TextStyle(fontSize: 12, color: Colors.black54),
          ),
        ],
      ),
    );
  }

  Color _severityColor(String value) {
    switch (value.toUpperCase()) {
      case 'HIGH':
        return Colors.red;
      case 'MEDIUM':
        return Colors.orange;
      case 'LOW':
        return Colors.green;
      default:
        return const Color(0xFF2E7D32);
    }
  }
}

class _InsightMiniCard extends StatelessWidget {
  final CustomerDashboardInsightItem insight;

  const _InsightMiniCard({required this.insight});

  @override
  Widget build(BuildContext context) {
    final color = _severityColor(insight.severity);

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withValues(alpha: 0.18)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  insight.title,
                  style: const TextStyle(fontWeight: FontWeight.w700),
                ),
              ),
              Text(
                insight.value,
                style: TextStyle(
                  color: color,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            '${insight.trend} · ${insight.message}',
            style: const TextStyle(fontSize: 12, color: Colors.black54),
          ),
        ],
      ),
    );
  }

  Color _severityColor(String value) {
    switch (value.toUpperCase()) {
      case 'HIGH':
        return Colors.red;
      case 'MEDIUM':
        return Colors.orange;
      case 'LOW':
        return Colors.green;
      default:
        return const Color(0xFF2E7D32);
    }
  }
}
