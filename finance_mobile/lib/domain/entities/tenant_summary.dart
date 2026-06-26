class TenantSummary {
  final int totalUsers;
  final int maxUsers;
  final String activePlan;
  final int trialDaysLeft;

  const TenantSummary({
    required this.totalUsers,
    required this.maxUsers,
    required this.activePlan,
    required this.trialDaysLeft,
  });

  double get userUsagePercent {
    if (maxUsers <= 0) return 0;
    return (totalUsers / maxUsers) * 100;
  }
}
