package com.financesystem.finance_api.modules.governance.notifications.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notifications.push.firebase")
public class FirebasePushProperties {

    private String credentialsPath = "classpath:firebase/service-account.example.json";
    private String projectId = "finance-system-dev";

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
