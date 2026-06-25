export interface LandingVideoItem {
  title: string;
  description: string;
  url: string;
  badge: string;
}

export interface LandingFeatureItem {
  icon: string;
  title: string;
  description: string;
}

export interface LandingMetricItem {
  value: string;
  label: string;
}

export interface LandingPlanPreview {
  code: 'DEMO' | 'BASIC' | 'PRO' | 'ENTERPRISE';
  name: string;
  description: string;
  priceLabel: string;
  badge?: string;
  ctaLabel: string;
  ctaLink: string;
}

export type LandingVideoCard = LandingVideoItem;
export type LandingFeatureCard = LandingFeatureItem;
export type LandingMetricCard = LandingMetricItem;
export type LandingPlanPreviewCard = LandingPlanPreview;
