package com.financesystem.finance_api.shared.controller;

import com.financesystem.finance_api.shared.model.Tenant;
import com.financesystem.finance_api.shared.repository.TenantRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantRepository tenantRepository;

    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // GET: listar todos los productos
    @GetMapping
    public List<Tenant> listar() {
        return tenantRepository.findAll();
    }

    // POST: crear un nuevo producto
    @SuppressWarnings("null")
    @PostMapping
    public Tenant crear(@RequestBody Tenant producto) {
        return tenantRepository.save(producto);
    }

    @SuppressWarnings("null")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable String id) {
        try {
            tenantRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found, cannot delete");
        }
    }
}
