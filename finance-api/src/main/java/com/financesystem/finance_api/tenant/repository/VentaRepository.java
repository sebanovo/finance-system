package com.financesystem.finance_api.tenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.financesystem.finance_api.tenant.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByProductoId(Long productoId);

    @Query("SELECT v FROM Venta v WHERE v.total > :monto")
    List<Venta> findVentasMayoresQue(@Param("monto") Double monto);
}