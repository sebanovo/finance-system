package com.financesystem.finance.bootstrap.platform;

import com.financesystem.finance.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicTestController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(
                "Finance API is running correctly",
                Map.of("status", "ok")
        );
    }

    @GetMapping("/security-status")
    public ApiResponse<Map<String, String>> securityStatus() {
        return ApiResponse.success(
                "Security base is active",
                Map.of("jwt", "enabled")
        );
    }
}
