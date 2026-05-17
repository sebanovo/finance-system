package com.financesystem.finance_api.modules.tenant.accounts.domain.model;

public enum AccountType {
    WALLET,
    SAVINGS,
    CHECKING,
    CREDIT_CARD,
    PREPAID_CARD,
    LOAN;

    public boolean isTransactional() {
        return switch (this) {
            case WALLET, SAVINGS, CHECKING, PREPAID_CARD -> true;
            case CREDIT_CARD, LOAN -> false;
        };
    }
}
