package com.financesystem.finance.modules.identity.auth.application.usecase;

import org.springframework.stereotype.Service;

@Service
public class LogoutTenantUserUseCase {

    public void execute() {
        // Stateless JWT logout:
        // the client discards tokens and stops sending them.
    }
}