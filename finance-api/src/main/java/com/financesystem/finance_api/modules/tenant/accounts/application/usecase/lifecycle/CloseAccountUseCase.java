package com.financesystem.finance_api.modules.tenant.accounts.application.usecase.lifecycle;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.governance.notifications.application.dto.NotificationPublishRequest;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationCategory;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationPriority;
import com.financesystem.finance_api.modules.governance.notifications.domain.model.NotificationType;
import com.financesystem.finance_api.modules.governance.notifications.domain.port.NotificationPublisherPort;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.mapper.AccountMapper;
import com.financesystem.finance_api.modules.tenant.audit.TenantAuditPayloads;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.AccountNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.exception.InvalidAccountStatusException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class CloseAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(CloseAccountUseCase.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;

    public CloseAccountUseCase(
            AccountRepository accountRepository,
            AccountMapper accountMapper,
            AuditTrailService auditTrailService,
            NotificationPublisherPort notificationPublisherPort,
            ObjectMapper objectMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.auditTrailService = auditTrailService;
        this.notificationPublisherPort = notificationPublisherPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AccountOwnerResponse execute(UUID accountId, String reason) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId
                ));

        if (existingAccount.availableBalance().compareTo(BigDecimal.ZERO) > 0
                || existingAccount.heldBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidAccountStatusException(
                    "Accounts with balance cannot be closed"
            );
        }

        Account updatedAccount = new Account(
                existingAccount.id(),
                existingAccount.userId(),
                existingAccount.accountNumber(),
                existingAccount.accountName(),
                existingAccount.customAlias(),
                existingAccount.accountType(),
                existingAccount.currency(),
                existingAccount.availableBalance(),
                existingAccount.heldBalance(),
                AccountStatus.CLOSED,
                normalizeReason(reason),
                false,
                existingAccount.primary(),
                existingAccount.openedAt(),
                Instant.now(),
                existingAccount.createdAt(),
                existingAccount.updatedAt()
        );

        Account savedAccount = accountRepository.save(updatedAccount);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNT_CLOSED,
                "ACCOUNT",
                savedAccount.id().toString(),
                TenantAuditPayloads.details(
                        "accountNumber", savedAccount.accountNumber(),
                        "reason", savedAccount.statusReason(),
                        "fromStatus", existingAccount.status().name(),
                        "toStatus", savedAccount.status().name()
                ),
                TenantAuditPayloads.accountState(existingAccount),
                TenantAuditPayloads.accountState(savedAccount)
        );

        ObjectNode data = objectMapper.createObjectNode()
                .put("accountId", savedAccount.id().toString())
                .put("accountNumber", savedAccount.accountNumber())
                .put("status", AccountStatus.CLOSED.name())
                .put("reason", savedAccount.statusReason())
                .put("event", "CLOSED");

        publishNotificationSafely(new NotificationPublishRequest(
                savedAccount.userId(),
                NotificationType.ACCOUNT_CLOSED,
                NotificationCategory.ACCOUNTS,
                NotificationPriority.HIGH,
                "Account closed",
                "Your account " + savedAccount.accountNumber() + " has been closed.",
                data,
                null,
                "/accounts/" + savedAccount.id(),
                null
        ));

        AccountOwnerView view = accountRepository.findViewById(savedAccount.id())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account view not found with id: " + savedAccount.id()
                ));

        return accountMapper.toOwnerResponse(view);
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return "Closed by administrator";
        }

        String trimmed = reason.trim();

        return trimmed.isEmpty()
                ? "Closed by administrator"
                : trimmed;
    }

    private void publishNotificationSafely(NotificationPublishRequest request) {
        try {
            notificationPublisherPort.publish(request);
        } catch (Exception exception) {
            log.warn("Unable to publish account closed notification: {}", exception.getMessage());
        }
    }
}
