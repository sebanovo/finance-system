package com.financesystem.finance_api.modules.governance.notifications.application.usecase;

import com.financesystem.finance_api.common.mail.MailDeliveryService;
import com.financesystem.finance_api.common.mail.MailMessage;
import com.financesystem.finance_api.modules.governance.notifications.application.config.AccountActivationNotificationProperties;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.AccountActivationNotification;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class SendAccountActivationNotificationUseCase {

    private final MailDeliveryService mailDeliveryService;
    private final AccountActivationNotificationProperties properties;

    public SendAccountActivationNotificationUseCase(
            MailDeliveryService mailDeliveryService,
            AccountActivationNotificationProperties properties
    ) {
        this.mailDeliveryService = mailDeliveryService;
        this.properties = properties;
    }

    public void execute(AccountActivationNotification notification) {
        String activationLink = buildActivationLink(notification);
        String expiresAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .format(notification.expiresAt().atOffset(ZoneOffset.UTC));

        String subject = "Activa tu cuenta";
        String body = """
                Hola,

                Tu cuenta fue creada y solo falta confirmar tu correo electrónico.

                Tenant: %s

                Si usas la aplicación móvil, ingresa este código de activación:
                Token: %s

                O usa el siguiente enlace para activar tu cuenta:
                %s

                Este enlace vence en:
                %s UTC

                Si no esperabas este correo, puedes ignorarlo.
                """.formatted(
                notification.tenantSlug(),
                notification.activationToken(),
                activationLink,
                expiresAt
        );

        mailDeliveryService.send(new MailMessage(
                notification.email(),
                subject,
                body
        ));
    }

    private String buildActivationLink(AccountActivationNotification notification) {
        String token = URLEncoder.encode(notification.activationToken(), StandardCharsets.UTF_8);
        String tenant = URLEncoder.encode(notification.tenantSlug(), StandardCharsets.UTF_8);

        return properties.getActivationUrlBase() + "?token=" + token + "&tenant=" + tenant;
    }
}
