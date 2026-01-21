package com.ve.edu.ucab.AtlasRelojeria.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ve.edu.ucab.AtlasRelojeria.Model.Ventas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ve.edu.ucab.AtlasRelojeria.Model.Producto;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:8080")
public class VentasController {

    private static final Path RUTA_VENTAS = Paths.get("src/main/resources/ventas.json");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping
    public ResponseEntity<List<Ventas>> obtenerVentas() throws IOException {
        List<Ventas> ventas = Files.exists(RUTA_VENTAS)
                ? mapper.readValue(Files.readString(RUTA_VENTAS), new TypeReference<List<Ventas>>() {})
                : new ArrayList<>();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<Ventas> buscarVentaPorId(@PathVariable String idVenta) throws IOException {
        List<Ventas> ventas = Files.exists(RUTA_VENTAS)
                ? mapper.readValue(Files.readString(RUTA_VENTAS), new TypeReference<List<Ventas>>() {})
                : new ArrayList<>();

        return ventas.stream()
                .filter(v -> v.getIdVenta() == idVenta)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> agregarVenta(@RequestBody Ventas nuevaVenta) throws IOException {
        List<Ventas> ventas = Files.exists(RUTA_VENTAS)
                ? mapper.readValue(Files.readString(RUTA_VENTAS), new TypeReference<List<Ventas>>() {})
                : new ArrayList<>();

        nuevaVenta.setIdVenta(String.format("V%03d", ventas.size() + 1));
        nuevaVenta.setFecha(LocalDate.now(ZoneId.of("America/Caracas")));
        ventas.add(nuevaVenta);

        Files.writeString(RUTA_VENTAS, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ventas));
        

        //PARA ACTUALIZAR CANTIDAD
        Path rutaProductos = Paths.get("src/main/resources/productos.json");

        List<Producto> productos = Files.exists(rutaProductos)
                ? mapper.readValue(Files.readString(rutaProductos), new TypeReference<List<Producto>>() {})
                : new ArrayList<>();

        for (Producto p : productos) {
            if (p.getId().equals(nuevaVenta.getIdProducto())) {
                int nuevaCantidad = p.getCantidad() - 1;
                p.setCantidad(nuevaCantidad);
                break;
            }
        }

        Files.writeString(rutaProductos, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(productos));
        
        return ResponseEntity.ok("Venta registrada exitosamente");
        }
} 
