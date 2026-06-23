package com.financesystem.finance_api.bootstrap.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.sample-data")
public class SampleDataBootstrapProperties {

    /**
     * Enables optional sample data seeds on top of the always-on bootstrap data.
     */
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
