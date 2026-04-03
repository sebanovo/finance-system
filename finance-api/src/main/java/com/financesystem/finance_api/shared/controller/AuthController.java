package com.financesystem.finance_api.shared.controller;

import com.financesystem.finance_api.shared.dto.AuthRequest;
import com.financesystem.finance_api.shared.dto.AuthResponse;
import com.financesystem.finance_api.shared.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService service;
    private final com.financesystem.finance_api.tenant.service.UserService userService;

    public AuthController(AuthenticationService service, com.financesystem.finance_api.tenant.service.UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<com.financesystem.finance_api.tenant.dto.UserResponseDTO> register(
            @RequestBody com.financesystem.finance_api.tenant.dto.UserRequestDTO request
    ) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }
}
