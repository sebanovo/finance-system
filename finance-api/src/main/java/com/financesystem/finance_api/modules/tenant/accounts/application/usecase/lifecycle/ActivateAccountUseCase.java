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

import java.util.Map;
import java.util.UUID;

@Service
public class ActivateAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActivateAccountUseCase.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuditTrailService auditTrailService;
    private final NotificationPublisherPort notificationPublisherPort;
    private final ObjectMapper objectMapper;

    public ActivateAccountUseCase(
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
    public AccountOwnerResponse execute(UUID accountId) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId
                ));

        if (existingAccount.status() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException(
                    "Closed accounts cannot be activated"
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
                AccountStatus.ACTIVE,
                null,
                true,
                existingAccount.primary(),
                existingAccount.openedAt(),
                existingAccount.closedAt(),
                existingAccount.createdAt(),
                existingAccount.updatedAt()
        );

        Account savedAccount = accountRepository.save(updatedAccount);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.ACCOUNT_ACTIVATED,
                "ACCOUNT",
                savedAccount.id().toString(),
                Map.of(
                        "accountNumber", savedAccount.accountNumber()
                )
        );

        ObjectNode data = objectMapper.createObjectNode()
                .put("accountId", savedAccount.id().toString())
                .put("accountNumber", savedAccount.accountNumber())
                .put("status", AccountStatus.ACTIVE.name())
                .put("event", "ACTIVATED");

        publishNotificationSafely(new NotificationPublishRequest(
                savedAccount.userId(),
                NotificationType.ACCOUNT_ACTIVATED,
                NotificationCategory.ACCOUNTS,
                NotificationPriority.NORMAL,
                "Account activated",
                "Your account " + savedAccount.accountNumber() + " is now active.",
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

    private void publishNotificationSafely(NotificationPublishRequest request) {
        try {
            notificationPublisherPort.publish(request);
        } catch (Exception exception) {
            log.warn("Unable to publish account activation notification: {}", exception.getMessage());
        }
    }
}
