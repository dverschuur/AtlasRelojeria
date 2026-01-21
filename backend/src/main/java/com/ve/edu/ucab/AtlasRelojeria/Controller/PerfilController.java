package com.ve.edu.ucab.AtlasRelojeria.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ve.edu.ucab.AtlasRelojeria.Model.Perfil;
import com.ve.edu.ucab.AtlasRelojeria.Model.Producto;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/perfiles")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://localhost:8081"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class PerfilController {

    private static final Path RUTA_JSON = Paths.get("src/main/resources/perfiles.json");
    private static final Path RUTA_PRODUCTOS = Paths.get("src/main/resources/productos.json");
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<List<Perfil>> obtenerPerfiles() throws IOException {
        List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
        return ResponseEntity.ok(perfiles);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearPerfil(@RequestBody Perfil nuevo) throws IOException {
        List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
        perfiles.add(nuevo);
        Files.writeString(RUTA_JSON, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(perfiles));
        return ResponseEntity.ok("Perfil creado exitosamente");
    }

    @DeleteMapping("/eliminar/{nombre}")
    public ResponseEntity<String> eliminarPerfil(@PathVariable String nombre) throws IOException {
        List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
        List<Perfil> actualizados = perfiles.stream()
            .filter(p -> !p.getNombre().equalsIgnoreCase(nombre))
            .toList();
        Files.writeString(RUTA_JSON, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualizados));
        return ResponseEntity.ok("Perfil eliminado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Perfil datos) throws IOException {
        List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
        
        Optional<Perfil> coincidencia = perfiles.stream()
            .filter(p -> p.getId().equalsIgnoreCase(datos.getId()) &&
                     p.getContrasena().equals(datos.getContrasena()))
                .findFirst();

        if (coincidencia.isPresent()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Login correcto");
            respuesta.put("esAdministrador", coincidencia.get().isEsAdministrador());
            respuesta.put("usuario", coincidencia.get());
            return ResponseEntity.ok(respuesta);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("mensaje", "Credenciales inválidas"));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> editarPerfil(@PathVariable String id, @RequestBody Map<String, Object> nuevosDatos) {
        try {
            // Leer el JSON
            List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});

            // Buscar el perfil a editar
            Optional<Perfil> perfilOpt = perfiles.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst();

            if (perfilOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Perfil no encontrado"));
            }

            Perfil perfil = perfilOpt.get();

            // Actualizar los campos
            if (nuevosDatos.containsKey("nombre")) {
                perfil.setNombre((String) nuevosDatos.get("nombre"));
            }
            if (nuevosDatos.containsKey("edad")) {
                perfil.setEdad((Integer) nuevosDatos.get("edad"));
            }
            if (nuevosDatos.containsKey("contrasena")) {
                perfil.setContrasena((String) nuevosDatos.get("contrasena"));
            }

            // Guardar cambios
            mapper.writerWithDefaultPrettyPrinter().writeValue(RUTA_JSON.toFile(), perfiles);
            return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado correctamente"));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al actualizar el perfil"));
        }
    }

    @GetMapping("/lista-deseos/{id}")
    public ResponseEntity<?> obtenerListaDeseos(@PathVariable String id) {
        try {
            // Leer el perfil
            List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
            Optional<Perfil> perfilOpt = perfiles.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

            if (perfilOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Perfil perfil = perfilOpt.get();

            // Leer todos los productos
            List<Producto> todosLosProductos = mapper.readValue(Files.readString(RUTA_PRODUCTOS), new TypeReference<List<Producto>>() {});

            // Filtrar solo los productos que están en la lista de deseos
            List<Producto> productosDeseados = todosLosProductos.stream()
                .filter(p -> perfil.getListaDeseos().contains(p.getId()))
                .collect(Collectors.toList());

            return ResponseEntity.ok(productosDeseados);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al obtener la lista de deseos"));
        }
    }

    @PostMapping("/toggle-lista-deseos/{id}")
    public ResponseEntity<?> toggleListaDeseos(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String productoId = body.get("productoId");
            if (productoId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de producto no proporcionado"));
            }

            // Leer perfiles
            List<Perfil> perfiles = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Perfil>>() {});
            
            // Encontrar el perfil
            Optional<Perfil> perfilOpt = perfiles.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

            if (perfilOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Perfil perfil = perfilOpt.get();

            // Toggle el producto en la lista de deseos
            if (perfil.estaEnListaDeseos(productoId)) {
                perfil.quitarDeListaDeseos(productoId);
            } else {
                perfil.agregarAListaDeseos(productoId);
            }

            // Guardar los cambios
            mapper.writerWithDefaultPrettyPrinter().writeValue(RUTA_JSON.toFile(), perfiles);

            // Obtener los productos actualizados de la lista de deseos
            List<Producto> todosLosProductos = mapper.readValue(Files.readString(RUTA_PRODUCTOS), new TypeReference<List<Producto>>() {});
            List<Producto> productosDeseados = todosLosProductos.stream()
                .filter(p -> perfil.getListaDeseos().contains(p.getId()))
                .collect(Collectors.toList());

            return ResponseEntity.ok(productosDeseados);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al actualizar la lista de deseos"));
        }
    }
} 