export interface TenantSummaryResponse {
  totalUsers: number;
  maxUsers: number;
  activePlan: string;
  trialDaysLeft: number;
}
