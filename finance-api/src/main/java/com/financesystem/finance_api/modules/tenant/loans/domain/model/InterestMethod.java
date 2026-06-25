package com.financesystem.finance_api.modules.tenant.loans.domain.model;

public enum InterestMethod {
    /** Flat interest: total interest spread evenly across installments. */
    FLAT,
    /** French amortization: fixed installment, decreasing interest. */
    FRENCH
}
