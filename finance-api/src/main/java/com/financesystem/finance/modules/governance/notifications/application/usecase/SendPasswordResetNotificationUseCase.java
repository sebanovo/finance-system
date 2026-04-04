package com.financesystem.finance.modules.governance.notifications.application.usecase;

import com.financesystem.finance.common.mail.MailDeliveryService;
import com.financesystem.finance.common.mail.MailMessage;
import com.financesystem.finance.modules.governance.notifications.application.config.PasswordResetNotificationProperties;
import com.financesystem.finance.modules.governance.notifications.domain.model.PasswordResetNotification;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class SendPasswordResetNotificationUseCase {

    private final MailDeliveryService mailDeliveryService;
    private final PasswordResetNotificationProperties properties;

    public SendPasswordResetNotificationUseCase(
            MailDeliveryService mailDeliveryService,
            PasswordResetNotificationProperties properties
    ) {
        this.mailDeliveryService = mailDeliveryService;
        this.properties = properties;
    }

    public void execute(PasswordResetNotification notification) {
        String resetLink = buildResetLink(notification);
        String expiresAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .format(notification.expiresAt().atOffset(ZoneOffset.UTC));

        String subject = "Recuperación de contraseña";
        String body = """
                Hola,

                Recibimos una solicitud para restablecer tu contraseña.

                Tenant: %s
                Enlace de recuperación:
                %s

                Este enlace vence en:
                %s UTC

                Si no solicitaste este cambio, puedes ignorar este correo.
                """.formatted(
                notification.tenantSlug(),
                resetLink,
                expiresAt
        );

        mailDeliveryService.send(new MailMessage(
                notification.email(),
                subject,
                body
        ));
    }

    private String buildResetLink(PasswordResetNotification notification) {
        String token = URLEncoder.encode(notification.resetToken(), StandardCharsets.UTF_8);
        String tenant = URLEncoder.encode(notification.tenantSlug(), StandardCharsets.UTF_8);

        return properties.getResetUrlBase() + "?token=" + token + "&tenant=" + tenant;
    }
}