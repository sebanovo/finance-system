package com.financesystem.finance_api.modules.tenant.transactions.application.port;

public interface AccountingPostingPort {

    void postTransaction(TransactionAccountingRequest request);
}
