package com.financesystem.finance_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financesystem.finance_api.tenant.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
