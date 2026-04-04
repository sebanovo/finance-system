package com.financesystem.finance.common.mail;

public record MailMessage(
        String to,
        String subject,
        String body
) {
}