/**
 * Signup Request Type
 * Used for public user registration that creates a new tenant
 */
export type SignupRequest = {
    companyName: string;
    tenantSlug: string;
    adminEmail: string;
    password: string;
    firstName: string;
    lastName: string;
};

/**
 * Signup Response Type
 */
export type SignupData = {
    tenantId: string;
    tenantSlug: string;
    userId: string;
    email: string;
    firstName: string;
    lastName: string;
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    accessExpiresInMs: number;
};

export type SignupResponse = {
    success: true;
    message: string;
    data: SignupData;
    timestamp: string;
};
