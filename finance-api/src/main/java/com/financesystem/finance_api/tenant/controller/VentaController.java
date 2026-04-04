package com.financesystem.finance_api.tenant.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import java.time.OffsetDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/ventas")
// public class VentaController {

//     @Autowired
//     private VentaRepository ventaRepository;

//     @Autowired
//     private ProductoRepository productoRepository;

//     /**
//      * Obtener todas las ventas
//      */
//     @GetMapping
//     public ResponseEntity<List<Venta>> getAllVentas() {
//         List<Venta> ventas = ventaRepository.findAll();
//         return ResponseEntity.ok(ventas);
//     }

//     /**
//      * Obtener una venta por ID
//      */
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getVentaById(@PathVariable Long id) {
//         @SuppressWarnings("null")
//         Optional<Venta> venta = ventaRepository.findById(id);

//         if (venta.isPresent()) {
//             return ResponseEntity.ok(venta.get());
//         } else {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "VENTA_NOT_FOUND");
//             error.put("message", "Venta con ID " + id + " no encontrada");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }
//     }

//     /**
//      * Crear una nueva venta
//      * Ejemplo de request body:
//      * {
//      * "productoId": 1,
//      * "cantidad": 2
//      * }
//      */
//     @PostMapping
//     public ResponseEntity<?> createVenta(@RequestBody VentaRequest request) {
//         // Validar que el producto existe
//         @SuppressWarnings("null")
//         Optional<Producto> productoOpt = productoRepository.findById(request.getProductoId());

//         if (!productoOpt.isPresent()) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "PRODUCTO_NOT_FOUND");
//             error.put("message", "Producto con ID " + request.getProductoId() + " no encontrado");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }

//         // Validar cantidad
//         if (request.getCantidad() == null || request.getCantidad() <= 0) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "INVALID_CANTIDAD");
//             error.put("message", "La cantidad debe ser mayor a 0");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//         }

//         Producto producto = productoOpt.get();
//         Double total = producto.getPrecio() * request.getCantidad();

//         // Crear la venta
//         Venta venta = new Venta();
//         venta.setProducto(producto);
//         venta.setCantidad(request.getCantidad());
//         venta.setTotal(total);

//         Venta savedVenta = ventaRepository.save(venta);

//         // Construir respuesta con detalles
//         Map<String, Object> response = new HashMap<>();
//         response.put("id", savedVenta.getId());
//         response.put("producto", producto.getNombre());
//         response.put("cantidad", savedVenta.getCantidad());
//         response.put("precioUnitario", producto.getPrecio());
//         response.put("total", savedVenta.getTotal());
//         response.put("createdAt", savedVenta.getCreatedAt());

//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
//     }

//     /**
//      * Actualizar una venta existente
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateVenta(@PathVariable Long id, @RequestBody VentaRequest request) {
//         // Verificar que la venta existe
//         @SuppressWarnings("null")
//         Optional<Venta> ventaOpt = ventaRepository.findById(id);

//         if (!ventaOpt.isPresent()) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "VENTA_NOT_FOUND");
//             error.put("message", "Venta con ID " + id + " no encontrada");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }

//         // Verificar que el producto existe si se está cambiando
//         @SuppressWarnings("null")
//         Optional<Producto> productoOpt = productoRepository.findById(request.getProductoId());

//         if (!productoOpt.isPresent()) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "PRODUCTO_NOT_FOUND");
//             error.put("message", "Producto con ID " + request.getProductoId() + " no encontrado");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }

//         // Validar cantidad
//         if (request.getCantidad() == null || request.getCantidad() <= 0) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "INVALID_CANTIDAD");
//             error.put("message", "La cantidad debe ser mayor a 0");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//         }

//         Venta venta = ventaOpt.get();
//         Producto producto = productoOpt.get();

//         // Actualizar datos
//         venta.setProducto(producto);
//         venta.setCantidad(request.getCantidad());
//         venta.setTotal(producto.getPrecio() * request.getCantidad());

//         Venta updatedVenta = ventaRepository.save(venta);

//         return ResponseEntity.ok(updatedVenta);
//     }

//     /**
//      * Eliminar una venta
//      */
//     @SuppressWarnings("null")
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteVenta(@PathVariable Long id) {
//         @SuppressWarnings("null")
//         Optional<Venta> venta = ventaRepository.findById(id);

//         if (!venta.isPresent()) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "VENTA_NOT_FOUND");
//             error.put("message", "Venta con ID " + id + " no encontrada");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }

//         ventaRepository.deleteById(id);

//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Venta eliminada correctamente");
//         response.put("id", id.toString());

//         return ResponseEntity.ok(response);
//     }

//     /**
//      * Obtener ventas de un producto específico
//      */
//     @GetMapping("/producto/{productoId}")
//     public ResponseEntity<?> getVentasByProducto(@PathVariable Long productoId) {
//         Optional<Producto> producto = productoRepository.findById(productoId);

//         if (!producto.isPresent()) {
//             Map<String, String> error = new HashMap<>();
//             error.put("error", "PRODUCTO_NOT_FOUND");
//             error.put("message", "Producto con ID " + productoId + " no encontrado");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//         }

//         List<Venta> ventas = ventaRepository.findById(productoId);

//         Map<String, Object> response = new HashMap<>();
//         response.put("producto", producto.get().getNombre());
//         response.put("ventas", ventas);
//         response.put("totalVentas", ventas.size());

//         return ResponseEntity.ok(response);
//     }

//     /**
//      * Obtener resumen de ventas
//      */
//     @GetMapping("/resumen")
//     public ResponseEntity<?> getResumenVentas() {
//         List<Venta> ventas = ventaRepository.findAll();

//         if (ventas.isEmpty()) {
//             Map<String, String> response = new HashMap<>();
//             response.put("message", "No hay ventas registradas");
//             return ResponseEntity.ok(response);
//         }

//         long totalVentas = ventas.size();
//         double montoTotal = ventas.stream()
//                 .mapToDouble(Venta::getTotal)
//                 .sum();

//         Map<String, Object> resumen = new HashMap<>();
//         resumen.put("totalVentas", totalVentas);
//         resumen.put("montoTotal", montoTotal);
//         resumen.put("promedioPorVenta", montoTotal / totalVentas);

//         return ResponseEntity.ok(resumen);
//     }

//     /**
//      * DTO para recibir datos de venta
//      */
//     public static class VentaRequest {
//         private Long productoId;
//         private Integer cantidad;

//         public Long getProductoId() {
//             return productoId;
//         }

//         public void setProductoId(Long productoId) {
//             this.productoId = productoId;
//         }

//         public Integer getCantidad() {
//             return cantidad;
//         }

//         public void setCantidad(Integer cantidad) {
//             this.cantidad = cantidad;
//         }
//     }
// }
import com.financesystem.finance_api.tenant.model.Producto;
import com.financesystem.finance_api.tenant.model.Venta;
import com.financesystem.finance_api.tenant.repository.ProductoRepository;
import com.financesystem.finance_api.tenant.repository.VentaRepository;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaController(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Venta> getAllVentas() {
        // Hibernate usará el search_path del tenant actual gracias a tu filtro
        return ventaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVentaById(@PathVariable Long id) {
        @SuppressWarnings("null")
        Optional<Venta> venta = ventaRepository.findById(id);

        if (venta.isPresent()) {
            return ResponseEntity.ok(venta.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "VENTA_NOT_FOUND");
            error.put("message", "Venta con ID " + id + " no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<?> createVenta(@RequestBody VentaRequest request) {
        // 1. Buscar el producto en el esquema del tenant
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en este tenant"));

        // 2. Crear la venta y calcular el total
        Venta venta = new Venta();
        venta.setProducto(producto);
        venta.setCantidad(request.getCantidad());
        venta.setTotal(producto.getPrecio() * request.getCantidad());

        Venta savedVenta = ventaRepository.save(venta);
        return ResponseEntity.ok(savedVenta);
    }
}

// DTO simple para recibir la petición
class VentaRequest {
    private Long productoId;
    private Integer cantidad;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}