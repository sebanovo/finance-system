package com.financesystem.finance_api.common.mail;

public record MailMessage(
        String to,
        String subject,
        String body,
        boolean html
) {
}
