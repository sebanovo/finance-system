package com.financesystem.finance_api.common.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailDeliveryService implements MailDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpMailDeliveryService.class);

    private final JavaMailSender javaMailSender;
    private final AppMailProperties appMailProperties;

    public SmtpMailDeliveryService(
            JavaMailSender javaMailSender,
            AppMailProperties appMailProperties
    ) {
        this.javaMailSender = javaMailSender;
        this.appMailProperties = appMailProperties;
    }

    @Override
    public void send(MailMessage message) {
        logger.info("Sending email notification to '{}'.", message.to());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(appMailProperties.getFrom());
        simpleMailMessage.setTo(message.to());
        simpleMailMessage.setSubject(message.subject());
        simpleMailMessage.setText(message.body());

        javaMailSender.send(simpleMailMessage);

        logger.info("Email notification sent successfully to '{}'.", message.to());
    }
}