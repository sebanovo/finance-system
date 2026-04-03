package com.financesystem.finance_api.multitenant;

public final class TenantContext {
    private static final String DEFAULT_TENANT = "public";
    private static final ThreadLocal<String> CURRENT_TENANT = ThreadLocal.withInitial(() -> DEFAULT_TENANT);

    private TenantContext() {
    }

    public static void setTenant(String tenant) {
        if (tenant == null || tenant.isBlank()) {
            CURRENT_TENANT.set(DEFAULT_TENANT);
        } else {
            CURRENT_TENANT.set(tenant);
        }
    }

    public static String getTenant() {
        String t = CURRENT_TENANT.get();
        return t == null ? DEFAULT_TENANT : t;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
