package com.financesystem.finance_api.modules.tenant.loans.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.Loan;
import com.financesystem.finance_api.modules.tenant.loans.domain.model.LoanInstallment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Publishes in-app/push notifications for loan lifecycle events. Failures are
 * swallowed (logged) so notification problems never break the loan operation.
 */
@Service
public class LoanNotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoanNotificationService.class);
    private static final String ACTION_URL = "/me/loans";

    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;

    public LoanNotificationService(
            NotificationPublisherPort notificationPublisherPort,
            ObjectMapper objectMapper
    ) {
        this.notificationPublisherPort = notificationPublisherPort;
        this.objectMapper = objectMapper;
    }

    public void loanApproved(Loan loan) {
        publish(loan, NotificationType.LOAN_APPROVED, NotificationPriority.NORMAL,
                "Préstamo aprobado",
                "Tu solicitud de préstamo por " + money(loan.principal()) + " " + loan.currency()
                        + " fue aprobada.");
    }

    public void loanRejected(Loan loan) {
        publish(loan, NotificationType.LOAN_REJECTED, NotificationPriority.NORMAL,
                "Préstamo rechazado",
                "Tu solicitud de préstamo por " + money(loan.principal()) + " " + loan.currency()
                        + " fue rechazada.");
    }

    public void loanDisbursed(Loan loan) {
        publish(loan, NotificationType.LOAN_DISBURSED, NotificationPriority.HIGH,
                "Préstamo desembolsado",
                "Se acreditaron " + money(loan.principal()) + " " + loan.currency()
                        + " en tu cuenta. Revisa tu cronograma de pagos.");
    }

    public void paymentRecorded(Loan loan, BigDecimal amount, BigDecimal outstanding) {
        publish(loan, NotificationType.LOAN_PAYMENT_RECORDED, NotificationPriority.NORMAL,
                "Pago registrado",
                "Registramos un pago de " + money(amount) + " " + loan.currency()
                        + ". Saldo pendiente: " + money(outstanding) + " " + loan.currency() + ".");
    }

    public void loanPaidOff(Loan loan) {
        publish(loan, NotificationType.LOAN_PAID_OFF, NotificationPriority.HIGH,
                "Préstamo pagado",
                "¡Felicidades! Cancelaste por completo tu préstamo.");
    }

    public void installmentOverdue(Loan loan, LoanInstallment installment) {
        publish(loan, NotificationType.LOAN_INSTALLMENT_OVERDUE, NotificationPriority.HIGH,
                "Cuota vencida",
                "La cuota #" + installment.number() + " (" + money(installment.totalDue()) + " "
                        + loan.currency() + ") venció el " + installment.dueDate() + ".");
    }

    private void publish(Loan loan, NotificationType type, NotificationPriority priority, String title, String body) {
        try {
            ObjectNode data = objectMapper.createObjectNode()
                    .put("loanId", loan.id().toString())
                    .put("status", loan.status().name())
                    .put("currency", loan.currency().name());

            notificationPublisherPort.publish(new NotificationPublishRequest(
                    loan.userId(),
                    type,
                    NotificationCategory.PAYMENTS,
                    priority,
                    title,
                    body,
                    data,
                    null,
                    ACTION_URL,
                    null
            ));
        } catch (Exception exception) {
            log.warn("Unable to publish loan notification {} for loan {}: {}",
                    type, loan.id(), exception.getMessage());
        }
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.toPlainString();
    }
}
