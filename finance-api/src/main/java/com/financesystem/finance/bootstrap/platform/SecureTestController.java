package com.financesystem.finance.bootstrap.platform;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.common.security.context.SecurityContextFacade;
import com.financesystem.finance.common.security.principal.AuthenticatedUserPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecureTestController {

    private final SecurityContextFacade securityContextFacade;

    public SecureTestController(SecurityContextFacade securityContextFacade) {
        this.securityContextFacade = securityContextFacade;
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        AuthenticatedUserPrincipal principal = securityContextFacade.getCurrentPrincipal();

        return ApiResponse.success(
                "Authenticated principal resolved successfully",
                Map.of(
                        "subject", principal.subject(),
                        "tenantSlug", principal.tenantSlug(),
                        "roles", principal.roles()
                )
        );
    }
}