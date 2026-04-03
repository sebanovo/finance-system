package com.financesystem.finance_api.shared.controller;

import com.financesystem.finance_api.shared.model.Tenant;
import com.financesystem.finance_api.shared.repository.TenantRepository;
import com.financesystem.finance_api.shared.service.TenantService;
import com.financesystem.finance_api.tenant.repository.RoleRepository;
import com.financesystem.finance_api.tenant.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class TenantRequest {
    private String tenant;
    private String adminName;
    private String adminEmail;
    private String adminPassword;

    public TenantRequest() {
    }

    public TenantRequest(String tenant, String adminName, String adminEmail, String adminPassword) {
        this.tenant = tenant;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String name) {
        this.tenant = name;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}

@RestController
@RequestMapping("/api/register-tenant")
public class TenantAdminController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantAdminController(TenantService tenantService, TenantRepository tenantRepository,
            UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<String> createTenant(@RequestBody TenantRequest request) {
        String tenant = request.getTenant();
        String encodedPassword = passwordEncoder.encode(request.getAdminPassword());

        // Creando el esquema, ejecutando migraciones Flyway y creando el admin (vía
        // JDBC)
        tenantService.createTenant(tenant, request.getAdminName(), request.getAdminEmail(), encodedPassword);

        // Guardando el tenant en la base de datos public tabla (tenant)
        Tenant newTenant = new Tenant();
        newTenant.setId(tenant);
        newTenant.setName(tenant);
        newTenant.setPaidUntil(LocalDate.now().plusDays(30));
        newTenant.setOnTrial(true);
        newTenant.setActive(true);
        tenantRepository.save(newTenant);

        return ResponseEntity.status(201).body("Tenant y usuario admin creados para: " + newTenant);
    }
}
