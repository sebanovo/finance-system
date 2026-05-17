package com.financesystem.finance_api.modules.governance.notifications.domain.port;

import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;

public interface NotificationPublisherPort {

    void publish(NotificationPublishRequest request);
}
