package com.financesystem.finance_api.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financesystem.finance_api.shared.model.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, String> {
}
