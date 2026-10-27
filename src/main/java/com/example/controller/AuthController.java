package com.example.controller;

import java.text.Normalizer;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Usuario;
import com.example.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String ATRIBUTO_USUARIO = "USUARIO_SESION_ID";

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales, HttpServletRequest request) {
        try {
            String correo = credenciales.get("correo");
            String contrasena = credenciales.get("contrasena");
            Usuario usuario = usuarioService.autenticar(correo, contrasena);
            iniciarSesionSpring(usuario, request);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Usuario datos, HttpServletRequest request) {
        try {
            Usuario creado = usuarioService.registrarCliente(datos);
            iniciarSesionSpring(creado, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> usuarioActual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.noContent().build();
        }
        Object idObj = session.getAttribute(ATRIBUTO_USUARIO);
        if (!(idObj instanceof Long)) {
            return ResponseEntity.noContent().build();
        }
        Usuario usuario = usuarioService.listarID((Long) idObj);
        if (usuario == null || usuario.getEstado() != 1) {
            session.invalidate();
            SecurityContextHolder.clearContext();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    private void iniciarSesionSpring(Usuario usuario, HttpServletRequest request) {
        String nombreRol = usuario.getRol() != null ? usuario.getRol().getNombre_rol() : "CLIENTE";
        String rolNormalizado = Normalizer.normalize(nombreRol, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .trim()
                .toUpperCase()
                .replace(' ', '_');

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rolNormalizado);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                usuario.getCorreo(), null, Collections.singletonList(authority));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute(ATRIBUTO_USUARIO, usuario.getId_usuario());
    }
}