package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.Rol;
import com.example.model.Usuario;
import com.example.repository.RolRepository;
=======
import org.springframework.stereotype.Service;

import com.example.model.Usuario;
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
import com.example.repository.UsuarioRepository;

@Service
public class UsuarioService {
	@Autowired
<<<<<<< HEAD
    private UsuarioRepository usuarioR;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarAll() {
=======
	private UsuarioRepository usuarioR;
	
	public List<Usuario> listarAll() {
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
        return usuarioR.findAll();
    }

    public List<Usuario> listarporNombre(String nombre) {
        return usuarioR.findByNombre(nombre);
    }

    public Usuario listarID(Long id) {
        return usuarioR.findById(id).orElse(null);
    }

<<<<<<< HEAD
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioR.buscarPorCorreo(correo);
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !esBCrypt(usuario.getContrasena())) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
=======
    public Usuario guardar(Usuario usuario) {
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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
<<<<<<< HEAD
            if (detalles.getContrasena() != null && !detalles.getContrasena().trim().isEmpty()) {
                String nuevaClave = detalles.getContrasena().trim();
                existente.setContrasena(esBCrypt(nuevaClave) ? nuevaClave : passwordEncoder.encode(nuevaClave));
            }
=======
            existente.setContrasena(detalles.getContrasena());
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
            existente.setEstado(detalles.getEstado());
            return usuarioR.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (usuarioR.existsById(id)) {
<<<<<<< HEAD
            usuarioR.deleteById(id);
=======
        	usuarioR.deleteById(id);
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
            return true;
        }
        return false;
    }

<<<<<<< HEAD
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

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
}
