package com.financesystem.finance_api.modules.tenant.accounts.domain.model;

public final class AccountNumberPrefix {

    private AccountNumberPrefix() {
    }

    public static String from(AccountType accountType) {
        return switch (accountType) {
            case WALLET -> "WAL";
            case SAVINGS -> "SAV";
            case CHECKING -> "CHK";
            case CREDIT_CARD -> "CCD";
            case PREPAID_CARD -> "PPD";
            case LOAN -> "LOA";
        };
    }
}
