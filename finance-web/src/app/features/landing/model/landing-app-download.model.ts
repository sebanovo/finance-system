export interface LandingAppDownloadFeature {
  icon: string;
  title: string;
  description: string;
}

export interface LandingAppDownloadData {
  badge: string;
  title: string;
  description: string;
  driveDownloadUrl: string;
  qrImageUrl: string;
  features: LandingAppDownloadFeature[];
}
