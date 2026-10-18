package com.example.controller;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Producto;
import com.example.service.ProductoService;

@RestController
@RequestMapping("/producto")
public class ProductoController {
	@Autowired
	private ProductoService prodService;
	
	@GetMapping("/listarProductos")
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = prodService.listarAll();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar productos");
        }
    }

    // 2. OBTENER IMAGEN
    @GetMapping("/imagenProducto")
    public ResponseEntity<?> imagenProducto(@RequestParam(required = false) String nombreImagen) {
        try {
            Map<String, Object> resultado = prodService.obtenerImagen(nombreImagen);
            Resource imagen = (Resource) resultado.get("imagen");

            if (imagen == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen no encontrada");
            }

            HttpHeaders headers = new HttpHeaders();
            boolean imgDefault = (boolean) resultado.get("imgDefault");
            if (!imgDefault && nombreImagen != null && !nombreImagen.isEmpty()) {
                String contentType = Files.probeContentType(Paths.get(prodService.CARPETA_IMAGENES + nombreImagen));
                if (contentType != null && !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("El archivo no es una imagen válida");
                }
                if (contentType != null) {
                    headers.add(HttpHeaders.CONTENT_TYPE, contentType);
                }
            }
            return ResponseEntity.ok().headers(headers).body(imagen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la imagen");
        }
    }

    // 3. LISTAR POR CATEGORÍA (Dinámico por ID)
    @GetMapping("/listarPorCategoria/{idCategoria}")
    public ResponseEntity<?> listarPorCategoria(@PathVariable("idCategoria") Long idCategoria) {
        try {
            List<Producto> productos = prodService.listarProductosPorCategoria(idCategoria);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar productos por categoría");
        }
    }

    // 4. FILTRAR POR NOMBRE
    @GetMapping("/listarNombre/{nombre}")
    public ResponseEntity<?> listarProdNombre(@PathVariable("nombre") String nombre) {
        try {
            List<Producto> productos = prodService.listarporNombre(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar producto por nombre");
        }
    }

    // 5. FILTRAR POR ID
    @GetMapping("/listarId/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable("id") Long id) {
        try {
            Producto producto = prodService.listarID(id);
            if (producto != null) {
                return ResponseEntity.ok(producto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener producto");
        }
    }

    // 6. CREAR PRODUCTO CON IMAGEN (@RequestPart)
    @PostMapping("/crear")
    public ResponseEntity<?> registrarProducto(
            @RequestPart("producto") Producto producto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Producto productoRegistrado = prodService.registrar(producto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoRegistrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el producto");
        }
    }

    // 7. ACTUALIZAR PRODUCTO CON IMAGEN (@RequestPart)
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable("id") Long id,
            @RequestPart("producto") Producto producto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Producto productoActualizado = prodService.actualizar(id, producto, file);
            if (productoActualizado != null) {
                return ResponseEntity.ok(productoActualizado);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el producto");
        }
    }

    // 8. ELIMINAR
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable("id") Long id) {
        try {
            boolean eliminado = prodService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Producto eliminado con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }

}
