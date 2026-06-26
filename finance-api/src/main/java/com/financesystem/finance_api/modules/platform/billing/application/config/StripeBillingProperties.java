package com.financesystem.finance_api.modules.platform.billing.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.billing.stripe")
public class StripeBillingProperties {

    private String secretKey;
    private String webhookSecret;
    private String frontendUrlBase;
    private String successUrl;
    private String cancelUrl;
    private String customerPortalReturnUrl;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public String getFrontendUrlBase() {
        return frontendUrlBase;
    }

    public void setFrontendUrlBase(String frontendUrlBase) {
        this.frontendUrlBase = frontendUrlBase;
    }

    public String getSuccessUrl() {
        return resolveFrontendUrl(successUrl);
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getCancelUrl() {
        return resolveFrontendUrl(cancelUrl);
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getCustomerPortalReturnUrl() {
        return resolveFrontendUrl(customerPortalReturnUrl);
    }

    public void setCustomerPortalReturnUrl(String customerPortalReturnUrl) {
        this.customerPortalReturnUrl = customerPortalReturnUrl;
    }

    private String resolveFrontendUrl(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String trimmed = value.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        String base = frontendUrlBase == null ? "" : frontendUrlBase.trim();
        if (base.isBlank()) {
            return trimmed;
        }

        boolean baseEndsWithSlash = base.endsWith("/");
        boolean valueStartsWithSlash = trimmed.startsWith("/");

        if (baseEndsWithSlash && valueStartsWithSlash) {
            return base + trimmed.substring(1);
        }
        if (!baseEndsWithSlash && !valueStartsWithSlash) {
            return base + "/" + trimmed;
        }
        return base + trimmed;
    }
}
