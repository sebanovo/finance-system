package com.financesystem.finance_api.modules.governance.notifications.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notifications.account-activation")
public class AccountActivationNotificationProperties {

    private int expirationMinutes = 1440;
    private String activationUrlBase = "http://localhost:4200/activate";

    public int getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(int expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public String getActivationUrlBase() {
        return activationUrlBase;
    }

    public void setActivationUrlBase(String activationUrlBase) {
        this.activationUrlBase = activationUrlBase;
    }
}
