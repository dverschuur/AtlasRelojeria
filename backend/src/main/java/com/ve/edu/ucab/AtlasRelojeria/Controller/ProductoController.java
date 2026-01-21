package com.ve.edu.ucab.AtlasRelojeria.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ve.edu.ucab.AtlasRelojeria.Model.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8081"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private static final Path RUTA_JSON = Paths.get("src/main/resources/productos.json");
    private static final Path RUTA_IMAGENES = Paths.get("src/main/resources").toAbsolutePath();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping("/img/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = RUTA_IMAGENES.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
            } else {
                logger.error("No se pudo encontrar el archivo: {}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al servir el archivo {}: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        logger.error("Tamaño de archivo excedido: {}", exc.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
            "error", "El archivo es demasiado grande. El tamaño máximo permitido es 15MB"
        ));
    }

    /* Vista para administrador: muestra todos los atributos */
    @GetMapping("/inventario")
    public ResponseEntity<List<Producto>> obtenerInventario() throws IOException {  
        ObjectMapper mapper = new ObjectMapper();
        List<Producto> productos = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Producto>>() {});
        return ResponseEntity.ok(productos);
    }

    /* Vista para cliente: solo nombre, precio, descripcion e imagen */
    @GetMapping("/vista-cliente")
    public ResponseEntity<List<Map<String, Object>>> obtenerVistaCliente() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> lista = new ArrayList<>();

        List<Map<String, Object>> productos = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Map<String, Object>>>() {});
        for (Map<String, Object> p : productos) {
                Map<String, Object> vista = new HashMap<>();
                vista.put("nombre", p.get("nombre"));
                vista.put("descripcion", p.get("descripcion"));
                vista.put("precio", p.get("precio"));
                vista.put("imagen", p.get("imagen"));
                vista.put("id", p.get("id"));
                vista.put("cantidad", p.get("cantidad"));
                lista.add(vista);
        }

        return ResponseEntity.ok(lista);
    }

    // ELIMINAR
   @DeleteMapping("/eliminar-id/{id}")
public ResponseEntity<?> eliminarProductoPorId(@PathVariable String id) {
    logger.info("Recibida solicitud para eliminar producto con ID: {}", id);

    List<Producto> productos;
    try {
        // Paso 1: Leer la lista de productos actual.
        // Es importante leer el archivo dentro del bloque try para capturar cualquier error de lectura.
        String jsonContent = Files.readString(RUTA_JSON);
        productos = mapper.readValue(jsonContent, new TypeReference<List<Producto>>() {});
    } catch (IOException e) {
        logger.error("Error CRÍTICO al leer el archivo productos.json: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("error", "No se pudo acceder a la base de datos de productos."));
    }

    // Paso 2: Encontrar el producto que se va a eliminar.
    Optional<Producto> productoAEliminarOpt = productos.stream()
        .filter(p -> p.getId().equalsIgnoreCase(id))
        .findFirst();

    // Paso 3: Si no se encuentra el producto, devolver un error 404.
    if (productoAEliminarOpt.isEmpty()) {
        logger.warn("No se encontró ningún producto con el ID: {}", id);
        return ResponseEntity.status(404).body(Map.of("error", "Producto no encontrado"));
    }

    Producto productoEliminado = productoAEliminarOpt.get();

    // Paso 4: Eliminar el producto de la lista en memoria.
    productos.removeIf(p -> p.getId().equalsIgnoreCase(id));

    try {
        // Paso 5: Escribir la lista actualizada de vuelta en el archivo JSON.
        // Este es el paso más crítico. Si esto falla, no debemos eliminar la imagen.
        mapper.writerWithDefaultPrettyPrinter().writeValue(RUTA_JSON.toFile(), productos);
        logger.info("Producto con ID {} eliminado correctamente del JSON.", id);

        // Paso 6: Una vez que el JSON se actualizó correctamente, eliminar el archivo de imagen.
        try {
            Files.deleteIfExists(RUTA_IMAGENES.resolve(productoEliminado.getImagen()));
            logger.info("Imagen {} asociada eliminada.", productoEliminado.getImagen());
        } catch (IOException e) {
            // Si la eliminación de la imagen falla, solo se registra un aviso.
            // La operación principal (eliminar del JSON) ya fue exitosa.
            logger.warn("No se pudo eliminar el archivo de imagen {}: {}", productoEliminado.getImagen(), e.getMessage());
        }

        // Paso 7: Devolver la respuesta de éxito.
        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));

    } catch (IOException e) {
        // Este error ocurre si la escritura en el archivo JSON falla.
        logger.error("Error CRÍTICO al escribir en el archivo productos.json: {}", e.getMessage());
        // En este punto, no se debería intentar recuperar la lista, ya que la operación falló.
        return ResponseEntity.internalServerError().body(Map.of("error", "Error al guardar los cambios en la base de datos."));
    }
}
    /** Agregar un nuevo producto al inventario */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("cantidad") int cantidad,
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("proveedor") String proveedor
    ) {
        logger.info("Iniciando proceso de agregar producto: {}", nombre);
        logger.info("Tamaño de la imagen recibida: {} bytes", imagen.getSize());

        try {
            // Verificar que el directorio de imágenes existe
            if (!Files.exists(RUTA_IMAGENES)) {
                logger.info("Creando directorio de imágenes: {}", RUTA_IMAGENES);
                Files.createDirectories(RUTA_IMAGENES);
            }

            // Verificar que el archivo JSON existe
            if (!Files.exists(RUTA_JSON)) {
                logger.info("Creando archivo JSON: {}", RUTA_JSON);
                Files.createFile(RUTA_JSON);
                mapper.writeValue(RUTA_JSON.toFile(), new ArrayList<>());
            }

            // Leer la lista actual de productos
            List<Producto> productos;
            try {
                String jsonContent = Files.readString(RUTA_JSON);
                if (jsonContent.trim().isEmpty()) {
                    productos = new ArrayList<>();
                } else {
                    productos = mapper.readValue(jsonContent, new TypeReference<List<Producto>>() {});
                }
            } catch (IOException e) {
                logger.error("Error al leer el archivo JSON: {}", e.getMessage());
                productos = new ArrayList<>();
            }

            // Verificar si ya existe un producto con el mismo nombre
            if (productos.stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre))) {
                logger.warn("Producto duplicado: {}", nombre);
                return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un producto con ese nombre"));
            }

            // Generar un nuevo ID único
            int maxId = productos.stream()
                    .map(p -> Integer.parseInt(p.getId().replace("P", "")))
                    .max(Integer::compareTo)
                    .orElse(0);

            String nuevoId = "P" + (maxId + 1);
            logger.info("Nuevo ID generado: {}", nuevoId);

            // Procesar y guardar la imagen
            String nombreOriginal = imagen.getOriginalFilename();
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            String nombreArchivo = nuevoId + extension;
            Path rutaCompleta = RUTA_IMAGENES.resolve(nombreArchivo);
            
            logger.info("Guardando imagen en: {}", rutaCompleta);
            try {
                Files.copy(imagen.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("Error al guardar la imagen: {}", e.getMessage());
                return ResponseEntity.internalServerError().body(Map.of("error", "Error al guardar la imagen: " + e.getMessage()));
            }

            // Crear el nuevo producto
            Producto nuevoProducto = new Producto();
            nuevoProducto.setId(nuevoId);
            nuevoProducto.setNombre(nombre);
            nuevoProducto.setDescripcion(descripcion);
            nuevoProducto.setPrecio(precio);
            nuevoProducto.setCantidad(cantidad);
            nuevoProducto.setImagen(nombreArchivo);
            nuevoProducto.setProveedor(proveedor);

            // Agregar el nuevo producto
            productos.add(nuevoProducto);

            // Guardar la lista actualizada
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(RUTA_JSON.toFile(), productos);
                logger.info("Producto guardado exitosamente: {}", nuevoId);
            } catch (IOException e) {
                logger.error("Error al guardar en JSON: {}", e.getMessage());
                // Intentar eliminar la imagen si falló el guardado en JSON
                try {
                    Files.deleteIfExists(rutaCompleta);
                } catch (IOException ex) {
                    logger.error("No se pudo eliminar la imagen después del error: {}", ex.getMessage());
                }
                return ResponseEntity.internalServerError().body(Map.of("error", "Error al guardar el producto en la base de datos"));
            }

            return ResponseEntity.ok(nuevoProducto);

        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error inesperado: " + e.getMessage()));
        }
    }

    //actyalizar 
    @PutMapping("/actualizar/{id}")
public ResponseEntity<?> actualizarProducto(
        @PathVariable String id,
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("precio") double precio,
        @RequestParam("cantidad") int cantidad,
        @RequestParam("proveedor") String proveedor,
        @RequestParam(value = "imagen", required = false) MultipartFile nuevaImagen
) {
    logger.info("Iniciando actualización del producto con ID: {}", id);

    try {
        // Leer productos actuales
        List<Producto> productos = mapper.readValue(Files.readString(RUTA_JSON), new TypeReference<List<Producto>>() {});
        Optional<Producto> productoOpt = productos.stream().filter(p -> p.getId().equalsIgnoreCase(id)).findFirst();

        if (productoOpt.isEmpty()) {
            logger.warn("Producto con ID {} no encontrado.", id);
            return ResponseEntity.status(404).body(Map.of("error", "Producto no encontrado"));
        }

        Producto producto = productoOpt.get();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCantidad(cantidad);
        producto.setProveedor(proveedor);

        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            // Eliminar imagen anterior
            try {
                Files.deleteIfExists(RUTA_IMAGENES.resolve(producto.getImagen()));
            } catch (IOException e) {
                logger.warn("No se pudo eliminar la imagen anterior: {}", e.getMessage());
            }

            // Guardar la nueva imagen
            String extension = Objects.requireNonNull(nuevaImagen.getOriginalFilename())
                                      .substring(nuevaImagen.getOriginalFilename().lastIndexOf("."));
            String nuevoNombreArchivo = id + extension;
            Path rutaImagenNueva = RUTA_IMAGENES.resolve(nuevoNombreArchivo);
            Files.copy(nuevaImagen.getInputStream(), rutaImagenNueva, StandardCopyOption.REPLACE_EXISTING);
            producto.setImagen(nuevoNombreArchivo);
        }

        // Guardar cambios en el archivo JSON
        mapper.writerWithDefaultPrettyPrinter().writeValue(RUTA_JSON.toFile(), productos);
        logger.info("Producto con ID {} actualizado correctamente.", id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto actualizado correctamente"));

    } catch (IOException e) {
        logger.error("Error al actualizar el producto: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(Map.of("error", "Error al actualizar el producto"));
    }
}


    @GetMapping
    public List<Producto> obtenerProductos() throws IOException {
        String contenido = Files.readString(RUTA_JSON);
        return mapper.readValue(contenido, new TypeReference<List<Producto>>() {});
    }
}
