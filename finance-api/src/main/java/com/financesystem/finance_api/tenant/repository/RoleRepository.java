package com.financesystem.finance_api.tenant.repository;

import com.financesystem.finance_api.tenant.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
