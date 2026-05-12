package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface AccountOwnerViewProjection {

    UUID getId();

    UUID getUserId();

    String getUserEmail();

    String getUserFirstName();

    String getUserLastName();

    String getAccountNumber();

    String getAccountName();

    String getCustomAlias();

    String getAccountType();

    String getCurrency();

    BigDecimal getAvailableBalance();

    BigDecimal getHeldBalance();

    String getStatus();

    String getStatusReason();

    boolean getActive();

    boolean getPrimary();

    Instant getOpenedAt();

    Instant getClosedAt();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}