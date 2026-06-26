import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';

import '../../domain/entities/tenant_summary.dart';

class OwnerDashboardView extends StatelessWidget {
  final TenantSummary? summary;
  final bool isLoading;
  final String? errorMessage;
  final VoidCallback onOpenUsers;
  final VoidCallback onOpenBackups;
  final VoidCallback onOpenFxRates;
  final VoidCallback onOpenFxFees;
  final VoidCallback onOpenAccountingPeriods;
  final VoidCallback onOpenJournalEntries;
  final VoidCallback onOpenLimits;
  final VoidCallback onOpenLoans;
  final VoidCallback onOpenServicePayments;
  final VoidCallback onOpenNotifications;
  final VoidCallback onOpenAccounts;
  final VoidCallback onOpenTransactions;

  const OwnerDashboardView({
    super.key,
    required this.summary,
    required this.isLoading,
    required this.errorMessage,
    required this.onOpenUsers,
    required this.onOpenBackups,
    required this.onOpenFxRates,
    required this.onOpenFxFees,
    required this.onOpenAccountingPeriods,
    required this.onOpenJournalEntries,
    required this.onOpenLimits,
    required this.onOpenLoans,
    required this.onOpenServicePayments,
    required this.onOpenNotifications,
    required this.onOpenAccounts,
    required this.onOpenTransactions,
  });

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Skeletonizer(
        enabled: true,
        child: _buildContent(TenantSummary(totalUsers: 0, maxUsers: 0, activePlan: '', trialDaysLeft: 0)),
      );
    }

    if (errorMessage != null) {
      return _ErrorCard(message: errorMessage!);
    }

    final data = summary;
    if (data == null) {
      return const _EmptyCard(message: 'No hay datos del dashboard del tenant.');
    }

    return _buildContent(data);
  }

  Widget _buildContent(TenantSummary data) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _HeroCard(summary: data),
        const SizedBox(height: 16),
        _QuickActions(
          onOpenUsers: onOpenUsers,
          onOpenBackups: onOpenBackups,
          onOpenFxRates: onOpenFxRates,
          onOpenFxFees: onOpenFxFees,
          onOpenAccountingPeriods: onOpenAccountingPeriods,
          onOpenJournalEntries: onOpenJournalEntries,
          onOpenLimits: onOpenLimits,
          onOpenLoans: onOpenLoans,
          onOpenServicePayments: onOpenServicePayments,
          onOpenNotifications: onOpenNotifications,
          onOpenAccounts: onOpenAccounts,
          onOpenTransactions: onOpenTransactions,
        ),
        const SizedBox(height: 16),
        _SummaryGrid(summary: data),
      ],
    );
  }
}

class _HeroCard extends StatelessWidget {
  final TenantSummary summary;

  const _HeroCard({required this.summary});

  @override
  Widget build(BuildContext context) {
    final usage = summary.userUsagePercent.clamp(0, 100).toDouble();
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: const Color(0xFF166534),
        borderRadius: BorderRadius.circular(28),
        border: Border.all(color: const Color(0xFF111827), width: 1.2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Tenant owner',
            style: TextStyle(color: Colors.white70, fontSize: 13, fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: 8),
          const Text(
            'Dashboard de la organización',
            style: TextStyle(color: Colors.white, fontSize: 26, fontWeight: FontWeight.w800),
          ),
          const SizedBox(height: 8),
          Text(
            'Plan ${summary.activePlan} · ${summary.trialDaysLeft} días de trial restantes',
            style: const TextStyle(color: Colors.white70),
          ),
          const SizedBox(height: 18),
          ClipRRect(
            borderRadius: BorderRadius.circular(999),
            child: LinearProgressIndicator(
              minHeight: 12,
              value: usage / 100,
              backgroundColor: Colors.black.withValues(alpha: 0.2),
              valueColor: const AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          ),
          const SizedBox(height: 10),
          Text(
            'Uso de usuarios ${usage.toStringAsFixed(0)}% · ${summary.totalUsers}/${summary.maxUsers}',
            style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w700),
          ),
        ],
      ),
    );
  }
}

class _QuickActions extends StatelessWidget {
  final VoidCallback onOpenUsers;
  final VoidCallback onOpenBackups;
  final VoidCallback onOpenFxRates;
  final VoidCallback onOpenFxFees;
  final VoidCallback onOpenAccountingPeriods;
  final VoidCallback onOpenJournalEntries;
  final VoidCallback onOpenLimits;
  final VoidCallback onOpenLoans;
  final VoidCallback onOpenServicePayments;
  final VoidCallback onOpenNotifications;
  final VoidCallback onOpenAccounts;
  final VoidCallback onOpenTransactions;

  const _QuickActions({
    required this.onOpenUsers,
    required this.onOpenBackups,
    required this.onOpenFxRates,
    required this.onOpenFxFees,
    required this.onOpenAccountingPeriods,
    required this.onOpenJournalEntries,
    required this.onOpenLimits,
    required this.onOpenLoans,
    required this.onOpenServicePayments,
    required this.onOpenNotifications,
    required this.onOpenAccounts,
    required this.onOpenTransactions,
  });

  @override
  Widget build(BuildContext context) {
    final actions = <({String label, IconData icon, VoidCallback onTap})>[
      (label: 'Usuarios', icon: Icons.people_outline, onTap: onOpenUsers),
      (label: 'Cuentas', icon: Icons.account_balance_wallet_outlined, onTap: onOpenAccounts),
      (label: 'Tx', icon: Icons.swap_horiz_outlined, onTap: onOpenTransactions),
      (label: 'Préstamos', icon: Icons.request_quote_outlined, onTap: onOpenLoans),
      (label: 'FX rates', icon: Icons.currency_exchange_outlined, onTap: onOpenFxRates),
      (label: 'Comisiones', icon: Icons.percent_outlined, onTap: onOpenFxFees),
      (label: 'Períodos', icon: Icons.calendar_month_outlined, onTap: onOpenAccountingPeriods),
      (label: 'Asientos', icon: Icons.receipt_long_outlined, onTap: onOpenJournalEntries),
      (label: 'Límites', icon: Icons.tune_outlined, onTap: onOpenLimits),
      (label: 'Servicios', icon: Icons.receipt_outlined, onTap: onOpenServicePayments),
      (label: 'Notificaciones', icon: Icons.notifications_outlined, onTap: onOpenNotifications),
      (label: 'Respaldos', icon: Icons.backup_outlined, onTap: onOpenBackups),
    ];

    return Wrap(
      spacing: 10,
      runSpacing: 10,
      children: actions
          .map(
            (action) => ActionChip(
              avatar: Icon(action.icon, size: 18, color: const Color(0xFF166534)),
              label: Text(action.label),
              onPressed: action.onTap,
              backgroundColor: Colors.white,
              side: const BorderSide(color: Color(0xFF111827)),
              labelStyle: const TextStyle(color: Color(0xFF111827), fontWeight: FontWeight.w600),
            ),
          )
          .toList(),
    );
  }
}

class _SummaryGrid extends StatelessWidget {
  final TenantSummary summary;

  const _SummaryGrid({required this.summary});

  @override
  Widget build(BuildContext context) {
    final items = [
      _ItemData('Usuarios', summary.totalUsers.toString(), Icons.people_outline, const Color(0xFF166534), 'Activos ${summary.totalUsers}'),
      _ItemData('Plan', summary.activePlan, Icons.workspace_premium_outlined, const Color(0xFF111827), 'Max users ${summary.maxUsers}'),
      _ItemData('Uso', '${summary.userUsagePercent.toStringAsFixed(0)}%', Icons.bar_chart_outlined, const Color(0xFF14532D), 'Capacidad utilizada'),
      _ItemData('Trial', '${summary.trialDaysLeft} días', Icons.schedule_outlined, const Color(0xFF166534), 'Tiempo restante'),
    ];

    return LayoutBuilder(
      builder: (context, constraints) {
        final width = constraints.maxWidth >= 900
            ? (constraints.maxWidth - 12) / 2
            : constraints.maxWidth;
        return Wrap(
          spacing: 12,
          runSpacing: 12,
          children: items
              .map((item) => SizedBox(width: width, child: _StatCard(data: item)))
              .toList(),
        );
      },
    );
  }
}

class _ItemData {
  final String title;
  final String value;
  final IconData icon;
  final Color color;
  final String subtitle;

  const _ItemData(this.title, this.value, this.icon, this.color, this.subtitle);
}

class _StatCard extends StatelessWidget {
  final _ItemData data;

  const _StatCard({required this.data});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFF111827)),
      ),
      child: Row(
        children: [
          Container(
            width: 46,
            height: 46,
            decoration: BoxDecoration(
              color: data.color.withValues(alpha: 0.12),
              borderRadius: BorderRadius.circular(14),
            ),
            child: Icon(data.icon, color: data.color),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(data.title, style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
                const SizedBox(height: 6),
                Text(data.value, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w800)),
                const SizedBox(height: 2),
                Text(data.subtitle, style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _ErrorCard extends StatelessWidget {
  final String message;
  const _ErrorCard({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: const Color(0xFFFEE2E2),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFFEF4444)),
      ),
      child: Text(message, style: const TextStyle(color: Color(0xFF7F1D1D))),
    );
  }
}

class _EmptyCard extends StatelessWidget {
  final String message;
  const _EmptyCard({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Text(message, style: const TextStyle(fontWeight: FontWeight.w600)),
    );
  }
}
