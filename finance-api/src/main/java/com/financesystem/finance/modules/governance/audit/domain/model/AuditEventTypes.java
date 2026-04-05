package com.financesystem.finance.modules.governance.audit.domain.model;

public final class AuditEventTypes {

    public static final String TENANT_CREATED = "TENANT_CREATED";
    public static final String TENANT_ACTIVATED = "TENANT_ACTIVATED";
    public static final String TENANT_DEACTIVATED = "TENANT_DEACTIVATED";

    public static final String SUBSCRIPTION_ASSIGNED = "SUBSCRIPTION_ASSIGNED";
    public static final String PUBLIC_SIGNUP_COMPLETED = "PUBLIC_SIGNUP_COMPLETED";

    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String USER_ACTIVATED = "USER_ACTIVATED";
    public static final String USER_DEACTIVATED = "USER_DEACTIVATED";

    public static final String ROLE_CREATED = "ROLE_CREATED";
    public static final String ROLE_UPDATED = "ROLE_UPDATED";
    public static final String ROLE_ACTIVATED = "ROLE_ACTIVATED";
    public static final String ROLE_DEACTIVATED = "ROLE_DEACTIVATED";
    public static final String USER_ROLES_ASSIGNED = "USER_ROLES_ASSIGNED";

    public static final String LOGIN = "LOGIN";
    public static final String TOKEN_REFRESHED = "TOKEN_REFRESHED";
    public static final String LOGOUT = "LOGOUT";
    public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    public static final String PASSWORD_RESET_REQUESTED = "PASSWORD_RESET_REQUESTED";
    public static final String PASSWORD_RESET_COMPLETED = "PASSWORD_RESET_COMPLETED";

    private AuditEventTypes() {
    }
}
