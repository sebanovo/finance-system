export type AuthRefreshData = {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  accessExpiresInMs: number;
};

export type AuthRefreshApiResponse = {
  success: true;
  message: string;
  data: AuthRefreshData;
  timestamp: string;
};
