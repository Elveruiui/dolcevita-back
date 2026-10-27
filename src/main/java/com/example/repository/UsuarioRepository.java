package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	List<Usuario> findByNombre(String nombre);
	
	@Query("SELECT u FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)")
    Optional<Usuario> buscarPorCorreo(@Param("correo") String correo);

}
