package com.financesystem.finance_api.common.tenancy.exception;

public class TenantResolutionException extends RuntimeException {

    public TenantResolutionException(String message) {
        super(message);
    }
}