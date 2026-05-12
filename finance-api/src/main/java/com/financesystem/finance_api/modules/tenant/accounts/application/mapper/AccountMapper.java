package com.financesystem.finance_api.modules.tenant.accounts.application.mapper;

import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountBalanceResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.AccountOwnerResponse;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.Account;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountOwnerView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper {

    public AccountOwnerResponse toOwnerResponse(AccountOwnerView account) {
        return new AccountOwnerResponse(
                account.id(),
                account.userId(),
                account.userEmail(),
                account.userFirstName(),
                account.userLastName(),
                buildFullName(account.userFirstName(), account.userLastName()),
                account.accountNumber(),
                account.accountName().name(),
                toAccountNameLabel(account.accountName()),
                account.customAlias(),
                resolveDisplayName(account.accountName(), account.customAlias()),
                account.accountType().name(),
                account.currency().name(),
                safeAmount(account.availableBalance()),
                safeAmount(account.heldBalance()),
                calculateTotalBalance(account.availableBalance(), account.heldBalance()),
                account.status().name(),
                account.statusReason(),
                account.active(),
                account.primary(),
                account.openedAt(),
                account.closedAt(),
                account.createdAt(),
                account.updatedAt()
        );
    }

    public AccountBalanceResponse toBalanceResponse(Account account) {
        return new AccountBalanceResponse(
                account.id(),
                account.accountNumber(),
                account.accountName().name(),
                toAccountNameLabel(account.accountName()),
                account.customAlias(),
                resolveDisplayName(account.accountName(), account.customAlias()),
                account.accountType().name(),
                account.currency().name(),
                safeAmount(account.availableBalance()),
                safeAmount(account.heldBalance()),
                calculateTotalBalance(account.availableBalance(), account.heldBalance()),
                account.status().name(),
                account.active()
        );
    }

    private BigDecimal calculateTotalBalance(BigDecimal availableBalance, BigDecimal heldBalance) {
        return safeAmount(availableBalance).add(safeAmount(heldBalance));
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String resolveDisplayName(AccountName accountName, String customAlias) {
        String normalizedAlias = normalize(customAlias);

        if (normalizedAlias != null) {
            return normalizedAlias;
        }

        return toAccountNameLabel(accountName);
    }

    private String toAccountNameLabel(AccountName accountName) {
        if (accountName == null) {
            return null;
        }

        return switch (accountName) {
            case MAIN_WALLET -> "Wallet principal";
            case SAVINGS_ACCOUNT -> "Cuenta de ahorro";
            case CHECKING_ACCOUNT -> "Cuenta corriente";
            case CREDIT_CARD_ACCOUNT -> "Tarjeta de crédito";
            case PREPAID_CARD_ACCOUNT -> "Tarjeta prepago";
            case LOAN_ACCOUNT -> "Cuenta de préstamo";
            case BUSINESS_ACCOUNT -> "Cuenta empresarial";
            case SECONDARY_ACCOUNT -> "Cuenta secundaria";
        };
    }

    private String buildFullName(String firstName, String lastName) {
        String normalizedFirstName = normalize(firstName);
        String normalizedLastName = normalize(lastName);

        if (normalizedFirstName == null && normalizedLastName == null) {
            return null;
        }

        if (normalizedFirstName == null) {
            return normalizedLastName;
        }

        if (normalizedLastName == null) {
            return normalizedFirstName;
        }

        return normalizedFirstName + " " + normalizedLastName;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
