package com.financesystem.finance_api.common.mail;

public interface MailDeliveryService {

    void send(MailMessage message);
}