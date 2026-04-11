package com.financesystem.finance_api.modules.governance.notifications.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notifications.password-reset")
public class PasswordResetNotificationProperties {

    private int expirationMinutes = 30;
    private String resetUrlBase = "http://localhost:4200/reset-password";

    public int getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public String getResetUrlBase() {
        return resetUrlBase;
    }

    public void setResetUrlBase(String resetUrlBase) {
        this.resetUrlBase = resetUrlBase;
    }
}