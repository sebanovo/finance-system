package com.financesystem.finance_api.modules.governance.notifications.domain.port;

import com.financesystem.finance_api.modules.governance.notifications.application.dto.PushNotificationMessage;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.PushNotificationResult;

public interface PushNotificationProviderPort {

    PushNotificationResult send(PushNotificationMessage message);
}
