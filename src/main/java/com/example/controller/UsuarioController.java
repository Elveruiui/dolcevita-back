package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Usuario;
import com.example.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioS;
	
	@GetMapping("/listarUsuarios")
    public ResponseEntity<?> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioS.listarAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar usuarios");
        }
    }

    @GetMapping("/listarNombre/{nombre}")
    public ResponseEntity<?> listarUsuNombre(@PathVariable("nombre") String nombre) {
        try {
            List<Usuario> usuarios = usuarioS.listarporNombre(nombre);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar usuarios por nombre");
        }
    }

    @GetMapping("/listarId/{id}")
    public ResponseEntity<?> listarUsuId(@PathVariable("id") Long id) {
        try {
            Usuario usuario = usuarioS.listarID(id);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar usuario por ID");
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioS.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario");
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioS.actualizar(id, usuario);
            if (usuarioActualizado != null) {
                return ResponseEntity.ok(usuarioActualizado);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = usuarioS.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Usuario eliminado con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }
    }

}
