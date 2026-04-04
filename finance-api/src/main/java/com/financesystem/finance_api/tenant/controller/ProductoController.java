package com.financesystem.finance_api.tenant.controller;

import com.financesystem.finance_api.tenant.model.Producto;
import com.financesystem.finance_api.tenant.repository.ProductoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // GET: listar todos los productos
    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // POST: crear un nuevo producto
    @SuppressWarnings("null")
    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVentaById(@PathVariable Long id) {
        @SuppressWarnings("null")
        Optional<Producto> venta = productoRepository.findById(id);

        if (venta.isPresent()) {
            return ResponseEntity.ok(venta.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "PRODUCTO_NOT_FOUND");
            error.put("message", "Producto con ID " + id + " no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable long id) {
        try {
            productoRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted"); // Returns 200 OK

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found, cannot delete");
        }
    }
}
