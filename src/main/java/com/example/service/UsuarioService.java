package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.Rol;
import com.example.model.Usuario;
import com.example.repository.RolRepository;
import com.example.repository.UsuarioRepository;

@Service
public class UsuarioService {
	@Autowired
    private UsuarioRepository usuarioR;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarAll() {
        return usuarioR.findAll();
    }

    public List<Usuario> listarporNombre(String nombre) {
        return usuarioR.findByNombre(nombre);
    }

    public Usuario listarID(Long id) {
        return usuarioR.findById(id).orElse(null);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioR.buscarPorCorreo(correo);
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !esBCrypt(usuario.getContrasena())) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return usuarioR.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario detalles) {
        Optional<Usuario> optional = usuarioR.findById(id);
        if (optional.isPresent()) {
            Usuario existente = optional.get();
            existente.setRol(detalles.getRol());
            existente.setNombre(detalles.getNombre());
            existente.setApellido(detalles.getApellido());
            existente.setCorreo(detalles.getCorreo());
            existente.setTelefono(detalles.getTelefono());
            if (detalles.getContrasena() != null && !detalles.getContrasena().trim().isEmpty()) {
                String nuevaClave = detalles.getContrasena().trim();
                existente.setContrasena(esBCrypt(nuevaClave) ? nuevaClave : passwordEncoder.encode(nuevaClave));
            }
            existente.setEstado(detalles.getEstado());
            return usuarioR.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (usuarioR.existsById(id)) {
            usuarioR.deleteById(id);
            return true;
        }
        return false;
    }

    public Usuario autenticar(String correo, String contrasenaPlana) throws Exception {
        Usuario usuario = usuarioR.buscarPorCorreo(correo == null ? "" : correo.trim())
                .orElseThrow(() -> new Exception("Correo o contraseña incorrectos."));

        if (usuario.getEstado() != 1) {
            throw new Exception("Tu cuenta se encuentra inactiva. Contacta al administrador.");
        }

        String guardada = usuario.getContrasena() == null ? "" : usuario.getContrasena();
        boolean coincide;
        if (esBCrypt(guardada)) {
            coincide = passwordEncoder.matches(contrasenaPlana, guardada);
        } else {
            coincide = guardada.equals(contrasenaPlana);
            if (coincide) {
                usuario.setContrasena(passwordEncoder.encode(contrasenaPlana));
                usuarioR.save(usuario);
            }
        }

        if (!coincide) {
            throw new Exception("Correo o contraseña incorrectos.");
        }
        return usuario;
    }

    public Usuario registrarCliente(Usuario nuevo) throws Exception {
        String correo = nuevo.getCorreo() == null ? "" : nuevo.getCorreo().trim();
        if (correo.isEmpty()) {
            throw new Exception("El correo es obligatorio.");
        }
        if (usuarioR.buscarPorCorreo(correo).isPresent()) {
            throw new Exception("Ya existe una cuenta registrada con ese correo.");
        }
        if (nuevo.getContrasena() == null || nuevo.getContrasena().length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres.");
        }

        Rol rolCliente = rolRepository.buscarPorNombre("Cliente")
                .orElseThrow(() -> new Exception("No se encontró el rol Cliente en la base de datos."));

        nuevo.setCorreo(correo);
        nuevo.setRol(rolCliente);
        nuevo.setEstado(1);
        nuevo.setContrasena(passwordEncoder.encode(nuevo.getContrasena()));
        return usuarioR.save(nuevo);
    }

    private boolean esBCrypt(String texto) {
        return texto != null && (texto.startsWith("$2a$") || texto.startsWith("$2b$") || texto.startsWith("$2y$"));
    }

}
